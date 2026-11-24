package buttle.apptpl.common.model.cmpt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

import java.io.Serial;
import java.io.Serializable;

/**
 * 회원사유형별코드
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_cmpy_cd_group")
public class CmptCmpyCdGroupEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CmptCmpyCdGroupPkey id;

    /**
     * 코드그룹명
     */
    @Column(name = "cd_group_nm", nullable = true, length = 100)
    private String cdGroupNm;

    /**
     * 비고내용
     */
    @Column(name = "rmrk_cn", nullable = true, length = 4000)
    private String rmrkCn;

    /**
     * 사용여부
     */
    @Column(name = "use_yn", nullable = true, length = 1)
    private String useYn;

    /**
     * 코드사용처코드
     */
    @Column(name = "cd_plcus_cd", nullable = true, length = 20)
    private String cdPlcusCd;
}
