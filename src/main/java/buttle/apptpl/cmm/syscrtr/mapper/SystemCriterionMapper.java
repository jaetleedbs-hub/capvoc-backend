package buttle.apptpl.cmm.syscrtr.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.syscrtr.vo.SYS5010001Vo;
import buttle.apptpl.cmm.syscrtr.vo.SYS5010002Vo;

@Mapper
public interface SystemCriterionMapper {

    /**
     * 기준 정보 조회
     * @param param 조회 조건
     * @return 기준 정보 목록
     */
    List<SYS5010002Vo> selectBaseInfoList(SYS5010001Vo param);

    /**
     * 기준 정보 상세 조회
     * @param param 조회 조건
     * @return 기준 정보 상세
     */
    SYS5010002Vo selectBaseInfoDetail(SYS5010001Vo param);
} 