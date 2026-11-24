package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptPswdPlcyEntity;

import java.math.BigDecimal;

/**
 * 비밀번호 정책관리 Repository
 */
@Repository
public interface CmptPswdPlcyRepo extends JpaRepository<CmptPswdPlcyEntity, BigDecimal> {
} 