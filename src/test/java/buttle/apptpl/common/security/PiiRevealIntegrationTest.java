package buttle.apptpl.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import buttle.apptpl.common.framework.config.EnvConfig;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.pii.PiiAccessDeniedException;
import micoai.corebase.pii.PiiReveal;
import micoai.corebase.pii.PiiRevealContext;
import micoai.corebase.pii.PiiType;
import micoai.corebase.util.CoreUtils.aes256;

/**
 * 개인정보 원본 열람(PiiReveal) 전 경로 실증 — 권한자는 감사 기록 후 원본을 받고,
 * 미인증은 거부된다. 실 DB(pc = chatcall-dev)의 표준 log_personal_access_log 를 확인한다.
 */
@Tag("integration")   // 실 DB 가 있어야 돈다 — 기본 `./gradlew test` 에서는 제외된다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class PiiRevealIntegrationTest {

	private static final String ACCESSOR = "pii-reveal-it";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EnvConfig envConfig;

	@AfterEach
	void cleanup() throws Exception {
		SecurityContextHolder.clearContext();
		try (Connection con = dataSource.getConnection();
				var st = con.prepareStatement(
						"delete from \"${DB_SCHEMA}\".log_personal_access_log where rgtr_id = ?")) {
			st.setString(1, ACCESSOR);
			st.executeUpdate();
		}
	}

	private void authenticate() {
		SecUser user = SecUser.builder().userId("u-1").lgnId(ACCESSOR).userNm("관리자").build();
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(user, "", List.of()));
	}

	@Test
	@DisplayName("권한자 — 원본을 받고, 그 열람이 감사(마스킹+GCM)로 남는다")
	void revealsAndAudits() throws Exception {
		authenticate();
		String raw = "01099998888";

		String result = PiiReveal.reveal(new PiiRevealContext(
				ACCESSOR, "custProfile", "custTelno", PiiType.MOBILE, raw, "cust-777"));

		assertEquals(raw, result, "권한자는 원본을 받는다");

		try (Connection con = dataSource.getConnection();
				var st = con.prepareStatement(
						"select fld_nm, msk_val, enc_val, grp_nm from \"${DB_SCHEMA}\".log_personal_access_log "
						+ "where rgtr_id = ?")) {
			st.setString(1, ACCESSOR);
			ResultSet rs = st.executeQuery();

			assertNotNull(rs);
			int count = 0;
			while (rs.next()) {
				count++;
				String mskVal = rs.getString("msk_val");
				String encVal = rs.getString("enc_val");
				String grpNm = rs.getString("grp_nm");

				assertNotEquals(raw, mskVal, "마스킹값은 원본과 달라야 한다");
				// 감사의 암호문을 복호하면 원본이 나온다(GCM 표준).
				assertEquals(raw, aes256.decrypt(encVal, envConfig.getAesKey()),
						"감사 암호문 복호 = 원본");
				// 정보주체 식별자가 남는다.
				assertEquals("custProfile#cust-777", grpNm);
			}
			assertEquals(1, count, "원본 열람 1건이 감사에 남아야 한다");
		}
	}

	@Test
	@DisplayName("미인증 — 원본 열람이 거부되고 감사도 남지 않는다")
	void deniesUnauthenticated() throws Exception {
		SecurityContextHolder.clearContext();

		assertThrows(PiiAccessDeniedException.class, () -> PiiReveal.reveal(new PiiRevealContext(
				ACCESSOR, "custProfile", "custTelno", PiiType.MOBILE, "01099998888", "cust-777")));

		try (Connection con = dataSource.getConnection();
				var st = con.prepareStatement(
						"select count(*) from \"${DB_SCHEMA}\".log_personal_access_log where rgtr_id = ?")) {
			st.setString(1, ACCESSOR);
			ResultSet rs = st.executeQuery();
			rs.next();
			assertEquals(0, rs.getInt(1), "거부된 열람은 감사되지 않아야 한다");
		}
	}
}
