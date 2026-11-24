package buttle.apptpl.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import buttle.apptpl.common.framework.config.EnvConfig;
import micoai.corebase.pii.PiiAttrType;
import micoai.corebase.pii.PiiBlindIndex;

/**
 * [PII-6] UMS 수신자 연락처의 <b>검색 키(blind index)</b> 산출 — 저장과 검색이 함께 쓰는 유일한 입구.
 *
 * <h3>왜 클래스를 따로 두는가</h3>
 * 이 값은 <b>발송 대상을 만들 때 저장</b>되고 <b>발송 이력 화면에서 찾는다</b>. 두 곳이 재료
 * (표 이름·컬럼 이름·속성 종류)를 각자 적으면, 한쪽만 바뀌어도 <b>컴파일은 통과하고 런타임에
 * 0건</b>이 된다. 그 증상은 "그런 수신자가 없다"와 구별되지 않는다.
 * 이름 트랙이 같은 이유로 {@code PiiBlindIndex.custName} 을 두었다 — 같은 방식을 따른다.
 *
 * <h3>도메인 분리</h3>
 * 키 재료에 표·컬럼이 섞인다({@code |attr|cmpt_ums_rcvr|mbr_telno}). 그래서 같은 번호라도
 * {@code cust_inf.cust_telno_bidx} 나 {@code usr.mbl_tel_no_bidx} 와 <b>값이 다르다</b> —
 * 표를 대조하는 것만으로 사람이 연결되지 않는다.
 *
 * <p><b>그래서 기존 bidx 를 복사해 오면 안 된다.</b> 백필도 평문에서 다시 산출한다.
 */
@Component
public class UmsRcvrContactKey {

    /** 도메인 분리의 재료. 표·컬럼 이름을 <b>여기 한 곳에만</b> 적는다. */
    private static final String TABLE = "cmpt_ums_rcvr";
    private static final String COL_TELNO = "mbr_telno";
    private static final String COL_EMAIL = "mbr_eml_addr";

    @Autowired
    private EnvConfig envConfig;

    /**
     * 전화번호 검색 키.
     *
     * @param rawTelno <b>평문</b> 번호. 암호문을 넘기면 정규화가 무의미해지고 검색이 빗나간다
     * @return blind index. 숫자가 하나도 없는 값이면 {@code null}(색인하지 않는다)
     */
    public String telno(String rawTelno) {
        return PiiBlindIndex.of(TABLE, COL_TELNO, PiiAttrType.PHONE, rawTelno, envConfig.getAesKey());
    }

    /**
     * 이메일 검색 키.
     *
     * @param rawEmail <b>평문</b> 주소
     * @return blind index. 비어 있으면 {@code null}
     */
    public String email(String rawEmail) {
        return PiiBlindIndex.of(TABLE, COL_EMAIL, PiiAttrType.EMAIL, rawEmail, envConfig.getAesKey());
    }
}
