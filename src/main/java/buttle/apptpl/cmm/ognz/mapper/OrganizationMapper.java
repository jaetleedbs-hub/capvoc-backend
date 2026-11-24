package buttle.apptpl.cmm.ognz.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.ognz.vo.SYS0010001Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0010002Vo;
import buttle.apptpl.cmm.ognz.vo.SYS0010003Vo;
import java.util.List;

/**
 * 조직관리 Mapper
 */
@Mapper
public interface OrganizationMapper {

    /**
     * 조직 조회
     */
    List<SYS0010002Vo> selectOrgList(SYS0010001Vo param);

    /**
     * 조직 상세 정보 조회
     */
    SYS0010003Vo selectOrgDtlInfo(SYS0010001Vo param);
} 