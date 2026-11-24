package buttle.apptpl.common.data;

import java.time.LocalDateTime; // 또는 java.util.Date

import buttle.apptpl.common.constant.AuditConstants;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass // 여러 엔티티에 공통 필드를 상속하기 위함
@EntityListeners(AuditingEntityListener.class) // 이 엔티티의 생명주기를 감시하여 Auditing 적용
@Getter
@Setter
public abstract class BaseAuditingEntity { // 또는 BaseAuditingEntity
    @CreatedDate
    @Column(name = "reg_dt", updatable = false) // 등록일자는 수정되지 않음
    private LocalDateTime regDt;

    @CreatedBy
    @Column(name = "rgtr_id", updatable = false, length = 50) // 등록자도 수정되지 않음
    private String rgtrId;

    @LastModifiedDate
    @Column(name = "mdfcn_dt")
    private LocalDateTime mdfcnDt;

    @LastModifiedBy
    @Column(name = "mdfr_id", length = 50)
    private String mdfrId;
    
    @PrePersist // 엔티티가 영속화되기 전에 실행
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.regDt = now;
        this.mdfcnDt = now;
        // 🚀 SecurityUtils를 활용한 사용자 정보 설정
        String currentUserId = getCurrentUserId();
        this.rgtrId = currentUserId;
        this.mdfrId = currentUserId;
    }

    @PreUpdate // 엔티티가 업데이트되기 전에 실행
    public void preUpdate() {
        this.mdfcnDt = LocalDateTime.now();
        // 🚀 SecurityUtils를 활용한 변경자 정보 설정
        this.mdfrId = getCurrentUserId();
    }

    /**
     * 🚀 현재 사용자 ID를 가져오는 유틸리티 메서드 (SecurityUtils 활용)
     * @return 현재 사용자 ID
     */
    private String getCurrentUserId() {
        // 1️⃣ 로그인된 사용자 정보 조회 시도
        SecUser currentUser = SecurityUtils.getCurrentUser();
        
        if (currentUser != null && currentUser.getUserId() != null) {
            // 로그인된 사용자의 회원 ID 반환
            return currentUser.getUserId();
        }
        
        // 2️⃣ 로그인되지 않은 경우 익명 사용자 정보 사용
        SecUser anonymousUser = SecurityUtils.getAnonymousUser();
        if (anonymousUser != null && anonymousUser.getLgnId() != null) {
            // 익명 사용자의 로그인 ID 반환 (예: "sid-session123")
            return anonymousUser.getLgnId();
        }
        
        // 3️⃣ 모든 방법이 실패한 경우 기본값 반환
        return AuditConstants.SYSTEM;
    }
}