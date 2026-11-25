package buttle.apptpl.common.model.cmpt.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Table(name = "cmpt_locale", schema = "cdm_own")
@Data
public class CmptLocaleEntity extends BaseAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "locale", nullable = false, length = 20)
    private String locale;

    @Column(name = "locale_nm", length = 20)
    private String localeNm;

    @Column(name = "sort_sn")
    private Integer sortSn;

    @Column(name = "rmrk_cn", length = 4000)
    private String rmrkCn;
} 