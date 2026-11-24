package buttle.apptpl.auth.password.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.auth.password.dto.COM0020001Dto;
import buttle.apptpl.auth.password.dto.COM0020002Dto;
import buttle.apptpl.auth.password.mapper.PasswordChangeMapper;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdHstryEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptPswdHstryRepo;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;
import buttle.apptpl.common.util.ModelConveter;
import micoai.corebase.exception.InvalidationException;

/**
 * <b>비밀번호 재사용 금지가 실제로 막는지</b> 고정하는 시험.
 *
 * <p>근거: {@code docs/quality/조사-계정정책-임직원데이터-20260905.md} §A-3.
 *
 * <p><b>무엇이 고장나 있었나.</b> 정책표({@code cmpt_pswd_plcy.pswd_reuse_yn})는 {@code 'N'}(재사용 금지)
 * 이었고 코드도 그 분기를 탔는데, 판정을 <b>SQL 의 해시 동등비교</b>로 했다:
 *
 * <pre>{@code WHERE user_id = #{userId} AND pswd = #{pswd}}</pre>
 *
 * <p>저장 형식은 BCrypt 이고 BCrypt 는 <b>호출마다 무작위 솔트</b>를 쓴다. 그래서 같은 비밀번호를
 * 다시 넣어도 해시 문자열이 매번 달라 그 조건은 <b>언제나 거짓</b>, {@code count} 는 <b>항상 0</b>
 * 이었다. ⇒ 정책이 켜져 있고 화면도 오류를 낼 준비가 돼 있는데 <b>그 조건이 참이 되는 경우가 없었다.</b>
 * 「검사가 없다」보다 나쁘다 — 점검 대응 시 «재사용 금지 적용됨»으로 답하게 되는 자리다.
 *
 * <p><b>왜 시험이 필요한가.</b> 이 결함은 코드를 읽어서는 보이지 않는다. SQL 도, 자바 분기도,
 * 정책값도 전부 «맞게» 생겼다. 어긋난 것은 <b>BCrypt 의 성질</b>이고, 그 성질은 시험으로만 드러난다.
 * 아래 {@link #hashEqualityWouldNeverMatch()} 가 그 성질을 대놓고 고정한다.
 */
class PasswordReuseCheckTest {

	private static final String USER_ID = "test-user";

	/** 정책을 전부 만족하는 비밀번호 — 8자 이상 · 대문자 · 소문자 · 특수문자. */
	private static final String CURRENT_PASSWORD = "Curr3nt!Pw";
	private static final String REUSED_PASSWORD = "Reused!Pw";
	private static final String FRESH_PASSWORD = "Fresh!Pw9";

	/** DB 실측 정책 (2026-09-05, {@code cmpt_pswd_plcy}): 변경 주기 90일. */
	private static final int POLICY_CYCLE_DAYS = 90;

	private PasswordChangeMapper mapper;
	private CmptUserRepo cmptUserRepo;
	private CmptPswdHstryRepo cmptPswdHstryRepo;
	private CustomPasswordEncoder encoder;
	private PasswordChangeService service;

	@BeforeEach
	void setUp() {
		mapper = mock(PasswordChangeMapper.class);
		cmptUserRepo = mock(CmptUserRepo.class);
		cmptPswdHstryRepo = mock(CmptPswdHstryRepo.class);
		encoder = new CustomPasswordEncoder(4);

		ModelConveter modelConveter = mock(ModelConveter.class);
		when(modelConveter.map(any(), eq(COM0020002Dto.class)))
				.thenReturn(COM0020002Dto.builder().userId(USER_ID).build());

		CommonUserAuthService commonUserAuthService = mock(CommonUserAuthService.class);
		when(commonUserAuthService.getPswdPlcy()).thenReturn(reuseForbiddenPolicy());

		when(cmptUserRepo.findById(USER_ID))
				.thenReturn(java.util.Optional.of(userWithPassword(CURRENT_PASSWORD)));

		service = new PasswordChangeService(mapper, modelConveter, cmptUserRepo, cmptPswdHstryRepo,
				encoder, mock(LoginAttemptUtils.class), commonUserAuthService);
	}

	// ───────────────────────────────────────────────────── 본 시험

	@Test
	@DisplayName("★ 같은 비밀번호를 다시 쓰면 거부된다 — 이력의 해시는 원문과 모양이 전혀 다르다")
	void rejectsPasswordAlreadyInHistory() {
		// 이력에는 <다른 솔트로 만들어진> 해시가 들어 있다. 문자열로는 새 해시와 절대 같지 않다.
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(encoder.encode("SomeOther!Pw1"), encoder.encode(REUSED_PASSWORD)));

		InvalidationException thrown = assertThrows(InvalidationException.class,
				() -> service.updatePassword(request(CURRENT_PASSWORD, REUSED_PASSWORD)));

		assertEquals("최근 기간 내에 사용한 비밀번호입니다.", thrown.getMessage());
		verify(cmptUserRepo, never()).save(any(CmptUserEntity.class));
		verify(cmptPswdHstryRepo, never()).save(any(CmptPswdHstryEntity.class));
	}

	/**
	 * <b>음성 대조.</b> 위 시험이 «고치기 전에는 통과해 버렸다»는 것을 여기서 실증한다.
	 * 옛 구현의 판정 규칙(해시 문자열 동등비교)을 그대로 재현하면, 같은 비밀번호인데도
	 * <b>한 건도 걸리지 않는다.</b>
	 */
	@Test
	@DisplayName("★ 음성 대조 — 해시 문자열 동등비교(옛 SQL)로는 같은 비밀번호를 한 건도 못 잡는다")
	void hashEqualityWouldNeverMatch() {
		List<String> history = Arrays.asList(encoder.encode(REUSED_PASSWORD),
				encoder.encode(REUSED_PASSWORD), encoder.encode(REUSED_PASSWORD));

		// 옛 구현이 SQL 에 넘기던 값: 새 비밀번호를 그 자리에서 다시 encode 한 해시
		String encodedForComparison = encoder.encode(REUSED_PASSWORD);

		// 옛 규칙 그대로: WHERE pswd = #{pswd}
		long countUnderOldRule = history.stream().filter(h -> h.equals(encodedForComparison)).count();
		assertEquals(0, countUnderOldRule,
				"전제 확인: BCrypt 는 솔트가 매번 달라 같은 비밀번호라도 해시가 같아지지 않는다");
		for (String stored : history) {
			assertNotEquals(stored, encodedForComparison);
		}

		// 지금 규칙: matches() 로 원문과 대조한다
		assertTrue(history.stream().anyMatch(h -> encoder.matches(REUSED_PASSWORD, h)),
				"고친 뒤에는 같은 비밀번호가 이력에서 걸려야 한다 — 이 두 판정의 차이가 곧 이번 수정이다");
	}

	@Test
	@DisplayName("이력에 없는 비밀번호는 통과한다 — 재사용 검사가 정상 변경을 막지 않는다")
	void allowsPasswordNotInHistory() {
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Collections.singletonList(encoder.encode("SomeOther!Pw1")));

		COM0020002Dto result = service.updatePassword(request(CURRENT_PASSWORD, FRESH_PASSWORD));

		assertEquals("SUCCESS", result.getResultCode());
		verify(cmptPswdHstryRepo).save(any(CmptPswdHstryEntity.class));
	}

	@Test
	@DisplayName("이력이 비어 있어도 깨지지 않는다 — 처음 바꾸는 계정")
	void handlesEmptyHistory() {
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Collections.emptyList());

		assertEquals("SUCCESS", service.updatePassword(request(CURRENT_PASSWORD, FRESH_PASSWORD))
				.getResultCode());
	}

	@Test
	@DisplayName("검사 창은 정책의 변경 주기(pswd_chg_cyc_cnt)를 쓴다 — 90일")
	void usesPolicyCycleAsWindow() {
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Collections.emptyList());

		service.updatePassword(request(CURRENT_PASSWORD, FRESH_PASSWORD));

		ArgumentCaptor<Integer> days = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Integer> limit = ArgumentCaptor.forClass(Integer.class);
		verify(mapper).selectRecentPasswordHashes(eq(USER_ID), days.capture(), limit.capture());

		assertEquals(POLICY_CYCLE_DAYS, days.getValue(),
				"창을 정책이 아닌 값으로 잡으면 정책을 고쳐도 검사가 안 따라온다");
		assertTrue(limit.getValue() > 0,
				"BCrypt 비교는 행마다 도는 비싼 연산이라 상한이 있어야 한다");
	}

	// ───────────────────────────────────────────────────── 원문 누출

	/**
	 * 이 판정은 <b>원문 비밀번호를 손에 쥐어야만</b> 성립한다(BCrypt 는 원문 없이 비교할 수 없다).
	 * 그래서 원문이 밖으로 새지 않는 것이 함께 지켜져야 한다 — 밖으로 나가는 것은
	 * «맞았다/아니다» 한 비트뿐이다.
	 */
	@Test
	@DisplayName("★ 거부 메시지·응답에 원문 비밀번호를 싣지 않는다")
	void doesNotLeakRawPassword() {
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Collections.singletonList(encoder.encode(REUSED_PASSWORD)));

		InvalidationException thrown = assertThrows(InvalidationException.class,
				() -> service.updatePassword(request(CURRENT_PASSWORD, REUSED_PASSWORD)));

		assertFalse(String.valueOf(thrown.getMessage()).contains(REUSED_PASSWORD),
				"예외 메시지에 원문이 실렸다");
		assertFalse(String.valueOf(thrown.getExceptionMessage()).contains(REUSED_PASSWORD),
				"예외 본문에 원문이 실렸다");
	}

	// ───────────────────────────────────────────────────── 저장 형식

	/**
	 * §A-2 D2 와 한 묶음이다. 사용자 스스로 바꾼 경로가 쓰는 형식이 8자여야, 관리자 초기화 경로
	 * (8자로 맞췄다)와 하나가 된다.
	 */
	@Test
	@DisplayName("변경 성공 시 pswd_chg_dt 를 8자(yyyyMMdd)로 쓴다")
	void writesEightCharChangeDate() {
		when(mapper.selectRecentPasswordHashes(eq(USER_ID), anyInt(), anyInt()))
				.thenReturn(Collections.emptyList());

		service.updatePassword(request(CURRENT_PASSWORD, FRESH_PASSWORD));

		ArgumentCaptor<CmptUserEntity> saved = ArgumentCaptor.forClass(CmptUserEntity.class);
		verify(cmptUserRepo).save(saved.capture());

		String written = saved.getValue().getPswdChgDt();
		assertTrue(written != null && written.matches("\\d{8}"),
				"pswd_chg_dt 가 yyyyMMdd 8자가 아니다: " + written);
	}

	// ───────────────────────────────────────────────────── 되돌아오지 못하게

	/**
	 * 본 시험들은 «지금 코드가 맞는가»를 본다. 이것은 «옛 방식이 되살아나지 않았는가»를 본다 —
	 * 같은 결함이 다른 이름으로 다시 들어오는 것이 이 계열에서 흔하다.
	 */
	@Test
	@DisplayName("★ 해시를 SQL 에서 동등비교하는 문장이 돌아오지 않았는가 — 보조 그물")
	void noHashEqualityInSql() throws IOException {
		Path mapperXml = Paths.get(
				"src/main/java/buttle/apptpl/auth/password/mapper/PasswordChangeMapper.xml");
		assertTrue(Files.exists(mapperXml), "매퍼가 없다(경로가 바뀌었나): " + mapperXml);

		String sql = Files.readString(mapperXml, StandardCharsets.UTF_8)
				.replaceAll("(?s)<!--.*?-->", " "); // 주석에 옛 문장을 <설명으로> 적어 두었다

		assertFalse(sql.matches("(?s).*\\bpswd\\s*=\\s*#\\{.*"),
				"해시를 SQL 에서 동등비교하고 있다. BCrypt 는 솔트가 매번 달라 그 조건은 언제나 거짓이다:\n"
						+ sql);
		assertFalse(sql.contains("countChangePassword"),
				"옛 판정 문장이 되살아났다");
	}

	// ───────────────────────────────────────────────────── 픽스처

	private COM0020001Dto request(String currentPassword, String newPassword) {
		COM0020001Dto dto = new COM0020001Dto();
		dto.setUserId(USER_ID);
		dto.setCurrentPassword(currentPassword);
		dto.setNewPassword(newPassword);
		dto.setConfirmPassword(newPassword);
		return dto;
	}

	private CmptUserEntity userWithPassword(String rawPassword) {
		CmptUserEntity entity = new CmptUserEntity();
		entity.setUserId(USER_ID);
		entity.setPswd(encoder.encode(rawPassword));
		entity.setLgnYn("Y");
		entity.setLgnTryCnt(0);
		return entity;
	}

	/** DB 실측값과 같은 정책: 90일 · 최소 8자 · 대소문자·특수문자 필수 · 숫자 불필요 · 재사용 금지. */
	private CmptPswdPlcyEntity reuseForbiddenPolicy() {
		CmptPswdPlcyEntity policy = new CmptPswdPlcyEntity();
		policy.setPswdChgCycCnt(BigDecimal.valueOf(POLICY_CYCLE_DAYS));
		policy.setPswdMinLen(BigDecimal.valueOf(8));
		policy.setPswdUcReqYn("Y");
		policy.setPswdLcReqYn("Y");
		policy.setPswdNumReqYn("N");
		policy.setPswdSpcCharReqYn("Y");
		policy.setPswdReuseYn("N");
		policy.setPswdDenyChar(null);
		return policy;
	}
}
