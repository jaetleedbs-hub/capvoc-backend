package buttle.apptpl.common.security;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.config.EnvConfig;
import buttle.apptpl.common.log.personal.LogtPersonalAccessLogEntity;
import buttle.apptpl.common.log.personal.LogtPersonalAccessLogRepo;
import micoai.corebase.pii.PiiAuditGroup;
import micoai.corebase.pii.PiiAuditLogger;
import micoai.corebase.pii.PiiContext;
import micoai.corebase.pii.PiiMasker;
import micoai.corebase.pii.PiiRevealContext;
import micoai.corebase.util.CoreUtils;
import micoai.corebase.util.CoreUtils.aes256;

/**
 * admin 의 개인정보 원본 열람 감사 기록기. corebase {@link PiiContext} 에 등록해
 * {@code PiiReveal.reveal()} 의 열람을 admin 자기 스키마 {@code ${DB_SCHEMA}.log_personal_access_log}
 * (N-18 되돌림, 2026-07-26)에 남긴다. LogtPersonalAccessLogEntity 를 통하므로 스키마는 그 엔티티가 정한다.
 *
 * <p>원본은 저장하지 않는다 — {@link PiiMasker} 마스킹값과 AES-256-GCM 암호문만 남긴다(구조는 표준과 동일).
 * 본 조회와 분리된 트랜잭션(REQUIRES_NEW)으로 기록한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPiiAuditLogger implements PiiAuditLogger {

	private final LogtPersonalAccessLogRepo repo;
	private final EnvConfig envConfig;

	@PostConstruct
	public void register() {
		PiiContext.setAuditLogger(this);
		log.info("[PII] AdminPiiAuditLogger 등록 — 원본 열람을 log_personal_access_log 에 기록");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void recordReveal(PiiRevealContext ctx) {
		String logId = CoreUtils.string.getNewId("rvl-");
		String masked = PiiMasker.mask(ctx.rawValue(), ctx.type());
		String enc = aes256.encryptGcm(ctx.rawValue(), envConfig.getAesKey());

		LogtPersonalAccessLogEntity e = LogtPersonalAccessLogEntity.builder()
				.logId(logId)
				.hstNo(0)
				// 정보주체 식별자·열람 사유를 그룹명에 함께 남긴다(표준 스키마에 별도 컬럼이 없어 합성).
				// 세 앱이 같은 규칙을 쓰도록 corebase 의 합성기를 통한다.
				.grpNm(PiiAuditGroup.compose(ctx))
				.rowNo(0)
				.fldNm(ctx.fieldName())
				.mskVal(masked)
				.encVal(enc)
				.mskTyp(ctx.type().name())
				.rgtrId(ctx.accessorId())
				.regDt(LocalDateTime.now())
				.build();

		repo.save(e);
	}
}
