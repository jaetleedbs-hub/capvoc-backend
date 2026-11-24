package buttle.apptpl.common.security.bruteforce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;

/**
 * <b>비밀번호 만료 판정이 형식 불일치로 꺼지지 않게</b> 하는 시험.
 *
 * <p>근거: {@code docs/quality/조사-계정정책-임직원데이터-20260905.md} §A-2 D2.
 *
 * <p><b>무엇이 고장나 있었나.</b> 이전 {@code cmpt_user.pswd_chg_dt} 문자열 컬럼은 쓰는 곳이
 * 둘이고 형식이 갈려 있었다. 현재 {@code cdm_own.usr.pswd_chg_dttm} 은 timestamp이며
 * JPA 변환기가 compact string 과 timestamp 사이를 변환한다.
 *
 * <table border="1">
 * <caption>쓰는 쪽 둘</caption>
 * <tr><th>쓰는 곳</th><th>형식</th><th>길이</th></tr>
 * <tr><td>{@code PasswordChangeService} (사용자 스스로 변경)</td><td>{@code yyyyMMdd}</td><td>8</td></tr>
 * <tr><td>{@code CmpyUserMapper.resetUserPassword} (관리자 초기화)</td>
 *     <td>{@code yyyyMMddHHmmss}</td><td>14</td></tr>
 * </table>
 *
 * <p>읽는 쪽은 <b>길이가 8이 아니면 포기</b>했고, 포기의 결과가 «만료 아님»(0일 경과)이었다.
 * ⇒ <b>관리자가 비밀번호를 초기화하면 그 계정의 만료 검사가 그때부터 영구히 꺼졌다.</b>
 * 오류가 아니라 경고 로그 한 줄이었고 로그인은 정상 성공했다.
 *
 * <p><b>고치는 방향이 둘이라는 것이 요점이다.</b> 쓰는 쪽을 8자로 모으는 것만으로는 모자라다 —
 * 이전 14자 문자열 값은 여전히 검사 밖에 있기 때문이다. 그래서 읽는 쪽도 함께 넓혔다.
 *
 * <p>⚠ 그리고 <b>fail-closed 로 뒤집지 않았다.</b> «못 읽으면 만료»로 바꾸면 형식이 하나
 * 어긋나는 순간 전 계정이 비밀번호 변경 화면으로 몰린다. 아래 {@link #unreadableValueStaysFailOpen()}
 * 가 그 선택을 <b>일부러</b> 고정한다 — 다음 사람이 «fail-open 이니 뒤집자»로 읽지 않도록.
 */
class PasswordExpiryFormatTest {

	/** DB 실측 정책 (2026-09-05, {@code cmpt_pswd_plcy}): 변경 주기 90일. */
	private static final int POLICY_CYCLE_DAYS = 90;

	private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter YMD_HMS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private LoginAttemptUtils loginAttemptUtils;

	@BeforeEach
	void setUp() {
		CmptPswdPlcyEntity policy = new CmptPswdPlcyEntity();
		policy.setPswdChgCycCnt(BigDecimal.valueOf(POLICY_CYCLE_DAYS));

		CommonUserAuthService commonUserAuthService = mock(CommonUserAuthService.class);
		when(commonUserAuthService.getPswdPlcy()).thenReturn(policy);

		// 이 경로가 쓰는 협력자는 정책 조회와 인코더뿐이다. 나머지는 건드리지 않는다.
		loginAttemptUtils = new LoginAttemptUtils(
				null, null, commonUserAuthService, null, new CustomPasswordEncoder(4));
	}

	// ───────────────────────────────────────────────────── 읽는 쪽 — 형식의 폭

	@Test
	@DisplayName("★ 14자(yyyyMMddHHmmss)로 저장된 값도 만료 판정을 받는다 — 관리자 초기화 계정")
	void fourteenCharValueIsStillEvaluated() {
		String written14 = LocalDateTime.now().minusDays(POLICY_CYCLE_DAYS + 10L).format(YMD_HMS);

		assertEquals(14, written14.length(), "전제: 옛 초기화 SQL 이 남긴 값은 14자다");
		assertTrue(loginAttemptUtils.isCredentialsExpired(user(written14)),
				"주기(" + POLICY_CYCLE_DAYS + "일)를 넘겼는데 만료로 보이지 않는다. "
						+ "읽는 쪽이 14자를 못 읽으면 그 계정의 만료 검사는 영구히 꺼진 것이다.");
	}

	/**
	 * <b>음성 대조.</b> 위 시험이 «고치기 전에는 실패했다»는 것을 이 자리에서 실증한다.
	 * 옛 규칙(길이 8이 아니면 0)을 그대로 재현해, 같은 입력에 <b>정반대 답</b>이 나오는 것을 고정한다.
	 * 이 시험이 깨진다면 읽는 쪽이 옛 규칙으로 되돌아갔다는 뜻이다.
	 */
	@Test
	@DisplayName("★ 음성 대조 — 옛 규칙(길이 8만 허용)이었다면 같은 값이 「만료 아님」이 된다")
	void oldLengthEightOnlyRuleWouldHaveMissedIt() {
		String written14 = LocalDateTime.now().minusDays(POLICY_CYCLE_DAYS + 10L).format(YMD_HMS);

		// 옛 구현 그대로: 길이가 8이 아니면 <읽기를 포기하고> 경과일수 0 → 0 >= 90 은 거짓 → 「만료 아님」
		long daysUnderOldRule = (written14.length() == 8)
				? ChronoUnit.DAYS.between(LocalDate.parse(written14, YMD), LocalDate.now())
				: 0L;
		assertFalse(daysUnderOldRule >= POLICY_CYCLE_DAYS,
				"전제 확인: 옛 규칙에서는 이 값이 「만료 아님」으로 떨어졌다");

		// 지금 구현은 같은 값을 읽어 낸다.
		assertEquals(written14.substring(0, 8), LoginAttemptUtils.extractYyyyMMdd(written14),
				"14자에서 앞 8자를 뽑아야 한다");
		assertTrue(loginAttemptUtils.isCredentialsExpired(user(written14)),
				"고친 뒤에는 같은 값이 만료로 판정돼야 한다 — 이 두 줄의 차이가 곧 이번 수정이다");
	}

	@Test
	@DisplayName("8자(yyyyMMdd)는 그대로 동작한다 — 기존 3계정이 쓰는 형식")
	void eightCharValueStillWorks() {
		assertTrue(loginAttemptUtils.isCredentialsExpired(
						user(LocalDate.now().minusDays(POLICY_CYCLE_DAYS + 1L).format(YMD))),
				"주기를 넘긴 8자 값은 만료여야 한다");

		assertFalse(loginAttemptUtils.isCredentialsExpired(
						user(LocalDate.now().minusDays(10).format(YMD))),
				"주기 안의 8자 값은 만료가 아니어야 한다");
	}

	@Test
	@DisplayName("주기 경계 — 정확히 주기일이면 만료다")
	void expiresExactlyOnTheCycleBoundary() {
		assertTrue(loginAttemptUtils.isCredentialsExpired(
				user(LocalDate.now().minusDays(POLICY_CYCLE_DAYS).format(YMD))));
		assertFalse(loginAttemptUtils.isCredentialsExpired(
				user(LocalDate.now().minusDays(POLICY_CYCLE_DAYS - 1L).format(YMD))));
	}

	@Test
	@DisplayName("형식 추출 — 8자와 그 뒤에 시각이 붙은 값만 받고, 짧거나 숫자가 아니면 거절한다")
	void extractsOnlyWellFormedDates() {
		assertEquals("20250101", LoginAttemptUtils.extractYyyyMMdd("20250101"));
		assertEquals("20250101", LoginAttemptUtils.extractYyyyMMdd("20250101093000"));
		assertEquals("20250101", LoginAttemptUtils.extractYyyyMMdd("  20250101  "), "공백은 다듬는다");

		assertNull(LoginAttemptUtils.extractYyyyMMdd("2025010"), "8자에 못 미치면 날짜가 아니다");
		assertNull(LoginAttemptUtils.extractYyyyMMdd("2025-01-01"), "구분자가 섞이면 다른 형식이다");
		assertNull(LoginAttemptUtils.extractYyyyMMdd("abcdefgh"));
		assertNull(LoginAttemptUtils.extractYyyyMMdd(""));
		assertNull(LoginAttemptUtils.extractYyyyMMdd(null));
	}

	// ───────────────────────────────────────────────────── 뒤집지 않은 선택들

	/**
	 * ⚠ 이 시험은 <b>결함이 아니라 결정</b>을 고정한다. 파싱 실패를 «만료»로 처리하면
	 * 형식이 하나 어긋나는 순간 전 계정이 강제 변경 화면으로 몰려 콘솔이 막힌 것처럼 보인다.
	 * fail-open 을 없애는 길은 판정을 뒤집는 것이 아니라 <b>읽을 수 있는 형식을 넓히는 것</b>이고,
	 * 그것이 위 시험들이다.
	 */
	@Test
	@DisplayName("읽어 낼 수 없는 값은 「만료 아님」으로 둔다 — fail-closed 로 뒤집지 않는다")
	void unreadableValueStaysFailOpen() {
		assertFalse(loginAttemptUtils.isCredentialsExpired(user("not-a-date")),
				"파싱 실패를 만료로 바꾸면 형식이 어긋나는 순간 사람이 못 들어온다");
	}

	@Test
	@DisplayName("값이 아예 없으면 만료다 — 없음은 fail-closed 가 맞다")
	void missingValueIsExpired() {
		assertTrue(loginAttemptUtils.isCredentialsExpired(user(null)));
		assertTrue(loginAttemptUtils.isCredentialsExpired(user("")));
	}

	/**
	 * 같은 헬퍼를 {@code isAccountExpired} 도 쓴다(입사일자·퇴사일자). 형식의 폭을 넓히면서
	 * 그쪽 판정이 흔들리지 않았는지 함께 본다.
	 */
	@Test
	@DisplayName("입사·퇴사일자 판정은 달라지지 않는다 — 같은 헬퍼를 쓰기 때문")
	void accountExpiryUnchanged() {
		CmptUserEntity active = user(LocalDate.now().format(YMD));
		active.setUseYn("Y");
		active.setJncmpYmd("20250101");
		active.setRsgntnYmd(null);
		assertFalse(loginAttemptUtils.isAccountExpired(active));

		CmptUserEntity resigned = user(LocalDate.now().format(YMD));
		resigned.setUseYn("Y");
		resigned.setJncmpYmd("20250101");
		resigned.setRsgntnYmd(LocalDate.now().minusDays(1).format(YMD));
		assertTrue(loginAttemptUtils.isAccountExpired(resigned), "퇴사일이 지났으면 계정 만료다");

		CmptUserEntity unused = user(LocalDate.now().format(YMD));
		unused.setUseYn("N");
		unused.setJncmpYmd("20250101");
		assertTrue(loginAttemptUtils.isAccountExpired(unused));
	}

	// ───────────────────────────────────────────────────── 쓰는 쪽 — 형식 하나

	/**
	 * 읽는 쪽을 넓혀도 <b>새로 쓰는 값</b>은 하나여야 한다. 여기서 SQL 원문을 보는 이유는,
	 * 이 경로가 단위시험으로 도달하기 어려운 MyBatis 문장이고 <b>실제로 갈라졌던 자리</b>이기 때문이다.
	 */
	@Test
	@DisplayName("★ 관리자 초기화 SQL 이 timestamp에 자정으로 저장한다")
	void adminResetWritesTimestampAtMidnight() throws IOException {
		Path mapper = Paths.get(
				"src/main/java/buttle/apptpl/cmm/cmpyuser/mapper/CmpyUserMapper.xml");
		assertTrue(Files.exists(mapper), "매퍼가 없다(경로가 바뀌었나): " + mapper);

		String xml = Files.readString(mapper, StandardCharsets.UTF_8);
		Matcher update = Pattern.compile(
						"<update\\b[^>]*\\bid=\"resetUserPassword\"[^>]*>(.*?)</update>", Pattern.DOTALL)
				.matcher(xml);
		assertTrue(update.find(), "resetUserPassword 문장을 찾지 못했다 — 이 시험이 고장났다");

		String body = update.group(1);
		assertTrue(body.contains("pswd_chg_dttm = CURRENT_DATE::timestamp"),
				"관리자 초기화는 기존 8자리 날짜와 같이 자정 timestamp를 저장해야 한다:\n" + body);
		assertFalse(body.contains("TO_CHAR(CURRENT_TIMESTAMP"),
				"timestamp 대상 컬럼에 문자열 날짜를 저장하지 않는다:\n" + body);
	}

	// ───────────────────────────────────────────────────── 픽스처

	private CmptUserEntity user(String pswdChgDt) {
		CmptUserEntity entity = new CmptUserEntity();
		entity.setUserId("test-user");
		entity.setPswdChgDt(pswdChgDt);
		return entity;
	}
}
