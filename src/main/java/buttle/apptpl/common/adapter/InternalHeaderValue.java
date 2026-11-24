package buttle.apptpl.common.adapter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 내부 서비스 호출용 헤더 값 인코딩.
 *
 * <p>HTTP 헤더 값은 ISO-8859-1 만 안전하다. 한글 같은 비-ASCII 를 그대로 실으면 수신 측 Tomcat 이
 * ISO-8859-1 로 해석해 깨지고, 그 상태로 처리가 진행되다 실패한다.</p>
 *
 * <p>실측(2026-08-02): {@code X-User-Name} 에 "데모사용자"(UTF-8 바이트)를 실어 desk 로 보내면
 * <b>500</b>, 같은 요청을 ASCII 이름으로 보내면 200 이었다. desk 는 실패 원인을 {@code /error} 로
 * 넘기며 일반 500("시스템 오류로 작업을 중단하였습니다")으로 바꿔버려, 한글 이름을 쓰는 관리자는
 * 원인 표시 없이 기능이 막혔다(회원사 상세 → 계약관리 탭).</p>
 *
 * <p>그래서 <b>보내는 쪽에서 ASCII 로 인코딩</b>한다. 수신 측은 {@code URLDecoder} 로 복원한다.</p>
 *
 * @since 2026-08-02
 * @author AICC 개발팀
 */
public final class InternalHeaderValue {

    private InternalHeaderValue() {
    }

    /**
     * 헤더 값을 ASCII 안전하게 인코딩한다(UTF-8 percent-encoding).
     *
     * <p>ASCII 전용 값은 대부분 그대로 유지되며, 공백은 {@code +} 로 인코딩된다(수신 측 디코딩으로 복원).</p>
     *
     * @param value 원본 값 (null 허용)
     * @return 인코딩된 값. null/빈 값은 그대로 반환
     */
    public static String encode(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
