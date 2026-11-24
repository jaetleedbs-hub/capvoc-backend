package buttle.apptpl.cmm.ognz.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.ognz.vo.SYS0020001Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0020002Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0020003Vo;
import java.util.List;

/**
 * 조직찾기 Mapper
 */
@Mapper
public interface OrganizationSearchMapper {

    /**
     * 조직 조회
     */
    List<SYS0020002Vo> selectOrgList(SYS0020001Vo param);

    /**
     * 조직 상세 목록
     */
    List<SYS0020003Vo> selectOrgDtlList(SYS0020001Vo param);
} 