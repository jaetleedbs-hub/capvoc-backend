package buttle.apptpl.common.log.personal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import buttle.apptpl.common.framework.config.EnvConfig;
import micoai.corebase.pii.Pii;
import micoai.corebase.pii.PiiType;
import micoai.corebase.util.CoreUtils.aes256;

/**
 * 실 도메인 적용 실증 — 응답 DTO 의 {@code @Pii} 필드가 조회 시 개인정보 접근이력에
 * <마스킹 + AES-256-GCM> 으로 남는지 실 DB(pc = chatcall-dev)로 확인한다.
 *
 * <p>기존엔 접근이력이 0건이었다(실 DTO 에 @Masking 부재). @Pii 를 붙이면 이 경로로 이력이 쌓인다.
 */
@Tag("integration")   // 실 DB 가 있어야 돈다 — 기본 `./gradlew test` 에서는 제외된다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class PersonalAccessPiiIntegrationTest {

	private static final String LOG_ID = "alog-pii-it";
	private static final String RGTR = "pii-it";

	@Autowired
	private PersonalAccessLogService service;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EnvConfig envConfig;

	/** @Pii 가 붙은 응답 DTO(고객 조회 결과를 흉내). */
	static class CustResultDto {
		@Pii(PiiType.NAME)
		public String custNm;
		@Pii(PiiType.MOBILE)
		public String custTelno;

		CustResultDto(String nm, String tel) {
			this.custNm = nm;
			this.custTelno = tel;
		}
	}

	@AfterEach
	void cleanup() throws Exception {
		try (Connection con = dataSource.getConnection();
				var st = con.prepareStatement(
						"delete from \"${DB_SCHEMA}\".log_personal_access_log where log_id = ?")) {
			st.setString(1, LOG_ID);
			st.executeUpdate();
		}
	}

	@Test
	@DisplayName("응답의 @Pii 필드가 접근이력에 마스킹+GCM 으로 남는다")
	void piiFieldsAreAudited() throws Exception {
		// saveLogAsync 는 @Async 이나 self-invocation 이라 동기 실행 → 호출 직후 조회 가능.
		service.processAndSavePersonalLogs(new CustResultDto("홍길동", "01012345678"), LOG_ID, RGTR);

		try (Connection con = dataSource.getConnection();
				var st = con.prepareStatement(
						"select fld_nm, msk_val, enc_val, msk_typ from \"${DB_SCHEMA}\".log_personal_access_log "
						+ "where log_id = ? order by fld_nm")) {
			st.setString(1, LOG_ID);
			ResultSet rs = st.executeQuery();

			int count = 0;
			boolean sawName = false, sawTel = false;
			while (rs.next()) {
				count++;
				String fld = rs.getString("fld_nm");
				String msk = rs.getString("msk_val");
				String enc = rs.getString("enc_val");

				// 마스킹값은 원본과 다르고, GCM 암호문을 복호하면 원본이 나온다.
				if ("custNm".equals(fld)) {
					sawName = true;
					assertNotEquals("홍길동", msk);
					assertEquals("홍길동", aes256.decrypt(enc, envConfig.getAesKey()), "이름 GCM 복호=원본");
					assertEquals("NAME", rs.getString("msk_typ"));
				} else if ("custTelno".equals(fld)) {
					sawTel = true;
					assertNotEquals("01012345678", msk);
					assertEquals("01012345678", aes256.decrypt(enc, envConfig.getAesKey()), "전화 GCM 복호=원본");
				}
			}
			assertEquals(2, count, "이름·전화 2건이 접근이력에 남아야 한다");
			assertEquals(true, sawName && sawTel);
		}
	}
}
