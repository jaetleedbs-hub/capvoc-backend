package buttle.apptpl.common.constant;

/**
 * 감사(Auditing) 관련 공통 상수.
 *
 * <p>등록자/수정자(rgtrId/mdfrId) 등 감사 필드에 인증 컨텍스트가 없을 때 사용하는
 * 시스템 기본 사용자 식별자를 한 곳에서 관리한다. 기존에 여러 서비스에 흩어져 있던
 * {@code "SYSTEM"} 인라인 문자열을 이 상수로 통일한다(값 변경 없음).</p>
 */
public final class AuditConstants {

    /** 인증 컨텍스트가 없을 때 감사 필드에 사용하는 시스템 사용자 식별자. */
    public static final String SYSTEM = "SYSTEM";

    private AuditConstants() {
    }
}
