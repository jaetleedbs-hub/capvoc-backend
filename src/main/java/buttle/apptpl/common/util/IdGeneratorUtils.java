package buttle.apptpl.common.util;

import micoai.corebase.util.CoreUtils;

/**
 * 접두어가 붙은 식별자 생성기.
 *
 * <p><b>[V85] 시각으로 식별자를 만들지 않는다.</b> 예전 구현은
 * {@code prefix + LocalDateTime.now().format("yyyyMMddHHmmss")} 였다 —
 * 같은 초에 두 건을 만들면 <b>같은 ID</b> 가 나온다. 지금은 호출자가 0곳이라 사고가 나지
 * 않았을 뿐이고, 누군가 이 메서드를 쓰기 시작하는 순간 조용히 데이터가 겹친다.
 *
 * <p>같은 부류의 결함을 표에서 걷어내면서(시각을 PK 로 쓰던 표 5개) 이 자리도 함께 고쳤다.
 * 근거: {@code docs/design/design-pk-time-collision.md} §1-E
 *
 * <p>구현은 {@code CoreUtils.string.getNewId(String)} 에 위임한다 —
 * 저장소에 이미 있는 유일한 생성기다. 여기에 따로 만들면 언젠가 둘이 갈린다.
 */
public class IdGeneratorUtils {

    private IdGeneratorUtils() {
    }

    public static String generateCampaignId() {
        return generateIdWithPrefix("CMP");
    }

    /**
     * @param prefix 식별자 앞에 붙일 접두어(ASCII)
     * @return {@code prefix} + 하이픈 없는 UUID
     */
    public static String generateIdWithPrefix(String prefix) {
        return CoreUtils.string.getNewId(prefix);
    }
}
