package buttle.apptpl.common.model.common.entity;

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
 * 공통 첨부파일 그룹
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cmpt_atch_file_group")
public class CommonAtchFileGroupEntity extends BaseAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 첨부파일그룹ID
     */
    @Id
    @Column(name = "atch_file_group_id", nullable = false, length = 50)
    private String id;

    /** WB-5: 소유 회사(다운로드 소유권 검증용). null 이면 접근 거부(deny). */
    @Column(name = "owner_cmpy_id", length = 64)
    private String ownerCmpyId;

    /** WB-5: 소유 주체 유형 — CUST | MBR | SYSTEM | null */
    @Column(name = "owner_subject_type", length = 16)
    private String ownerSubjectType;

    /** WB-5: 소유 주체 식별자(custId/mbrId). company 스코프만이면 null */
    @Column(name = "owner_subject_id", length = 64)
    private String ownerSubjectId;
}
