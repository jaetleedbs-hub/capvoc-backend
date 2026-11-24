package buttle.apptpl.cmm.cd.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.cmm.cd.vo.SYS5040001Vo;
import buttle.apptpl.cmm.cd.vo.SYS5040002Vo;
import buttle.apptpl.cmm.cd.vo.SYS5040003Vo;
import buttle.apptpl.cmm.cd.vo.SYS5040004Vo;
import buttle.apptpl.cmm.cd.vo.SYS5040005Vo;

@Mapper
public interface CodeMapper {

    /**
     * 시스템 대분류 코드 조회
     * @param param 조회 조건
     * @return 시스템 대분류 코드 목록
     */
    List<SYS5040002Vo> selectSysLCatCdList(SYS5040001Vo param);

    /**
     * 시스템 대분류 코드 상세 조회
     * @param param 조회 조건
     * @return 시스템 대분류 코드
     */
    SYS5040002Vo selectSysLCatCdDtl(SYS5040001Vo param);

    /**
     * 시스템 소분류 코드 조회
     * @param param 조회 조건
     * @return 시스템 소분류 코드 목록
     */
    List<SYS5040004Vo> selectSysSCatCdList(SYS5040003Vo param);

    /**
     * 시스템 소분류 코드 상세 조회
     * @param param 조회 조건
     * @return 시스템 소분류 코드
     */
    SYS5040004Vo selectSysSCatCdDtl(SYS5040003Vo param);

    /**
     * 시스템 소분류 코드 참고 정보 조회
     * @param param 조회 조건
     * @return 시스템 소분류 코드 참고 정보 목록
     */
    List<SYS5040005Vo> selectSysSCatCdRefList(SYS5040003Vo param);
} 