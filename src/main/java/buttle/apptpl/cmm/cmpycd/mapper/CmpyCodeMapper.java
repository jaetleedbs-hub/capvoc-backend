package buttle.apptpl.cmm.cmpycd.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.api.shared.vo.*;

import java.util.List;

/**
 * BO403_S 회원사 공통 코드 관리 Mapper
 */
@Mapper
public interface CmpyCodeMapper {
    
    /**
     * 코드 그룹 목록조회
     */
    List<BO403SCodeGroupListResVo> selectCodeGroupList(BO403SCodeGroupSearchReqVo vo);
    
    /**
     * 코드 목록조회
     */
    List<BO403SCodeListResVo> selectCodeList(BO403SCodeSearchReqVo vo);
    
    /**
     * 코드 그룹 존재 여부 확인
     */
    int selectCodeGroupExists(BO403SCodeGroupSaveReqVo vo);
    
    /**
     * 코드 존재 여부 확인
     */
    int selectCodeExists(BO403SCodeSaveReqVo vo);
    
    /**
     * 코드 그룹 등록
     */
    int insertCodeGroup(BO403SCodeGroupSaveReqVo vo);
    
    /**
     * 코드 그룹 수정
     */
    int updateCodeGroup(BO403SCodeGroupSaveReqVo vo);
    
    /**
     * 코드 등록
     */
    int insertCode(BO403SCodeSaveReqVo vo);
    
    /**
     * 코드 수정
     */
    int updateCode(BO403SCodeSaveReqVo vo);
}
