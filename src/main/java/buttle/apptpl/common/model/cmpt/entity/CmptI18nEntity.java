package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import buttle.apptpl.common.data.BaseAuditingEntity;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_i18n", schema = "cdm_own")
public class CmptI18nEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "i18n_id", nullable = false, length = 50)
    private String i18nId;

    @Column(name = "locale", nullable = false, length = 20)
    private String locale;

    @Column(name = "msg_cd", length = 100)
    private String msgCd;

    @Column(name = "msg_cn", length = 4000)
    private String msgCn;

    @Column(name = "rmrk_cn", length = 4000)
    private String rmrkCn;
} 