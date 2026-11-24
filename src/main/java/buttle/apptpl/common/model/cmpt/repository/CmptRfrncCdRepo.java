package buttle.apptpl.common.model.cmpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import buttle.apptpl.common.model.cmpt.entity.CmptRfrncCdEntity;
import buttle.apptpl.common.model.cmpt.entity.CmptRfrncCdPkey;

/**
 * 참조 코드 Repository
 */
@Repository
public interface CmptRfrncCdRepo extends JpaRepository<CmptRfrncCdEntity, CmptRfrncCdPkey> {

    @Modifying
    @Query("DELETE FROM CmptRfrncCdEntity a WHERE a.id.cdGroup = :cdGroup AND a.id.cd = :cd AND a.id.locale = :locale")
    void deleteById(@Param("cdGroup") String cdGroup, @Param("cd") String cd, @Param("locale") String locale);

} 