package buttle.apptpl.sup.memo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import buttle.apptpl.sup.memo.dto.COM2080004Dto;
import buttle.apptpl.sup.memo.dto.COM2080002Dto;
import buttle.apptpl.sup.memo.vo.COM2080001Vo;
import buttle.apptpl.sup.memo.vo.COM2080002Vo;

import java.util.List;

@Mapper
public interface MemoMapper {

    /**
     * 메모 상세 조회
     * @param param 조회 조건
     * @return 메모 정보
     */
    COM2080002Vo selectMemo(COM2080001Vo param);

    /**
     * 메모 목록 조회
     * @param param 조회 조건
     * @return 메모 목록
     */
    List<COM2080002Dto> selectMemoList(@Param("userId") String userId);

     /**
     * @param userId 사용자 ID
     * @return 최대 시퀀스 번호
     */
    Integer selectMaxCategorySequence(@Param("userId") String userId);

    /**
     * 메모 저장
     * @param param 저장 정보
     * @return 처리 건수
     */
    int insertMemo(COM2080002Vo param);
    
    /**
     * 메모 수정
     * @param param 수정 정보
     * @return 처리 건수
     */
    int updateMemo(COM2080002Vo param);

    /**
     * 메모 삭제
     * @param param 삭제 조건
     * @return 처리 건수
     */
    int deleteMemo(COM2080001Vo param);

    /**
     * 메모 카테고리 삭제
     * @param param 삭제 조건
     * @return 처리 건수
     */
    int deleteMemoCategory(COM2080001Vo param);

    /**
     * 메모 카테고리 조회
     * @param param 조회 조건
     * @return 메모 카테고리 목록
     */
    List<COM2080004Dto> selectMemoCategory(@Param("userId") String userId);

    /**
     * 메모 카테고리 저장
     * @param param 저장 정보
     * @return 처리 건수
     */
    int insertMemoCategory(COM2080001Vo param);

    /**
     * 메모 최대 시퀀스 조회
     * @param memoSid 메모 카테고리 시퀀스ID
     * @return 최대 시퀀스 번호
     */
    Integer selectMaxMemoSequence(@Param("userId") String userId);

    /**
     * 메모 폴더 변경 업데이트
     * @param param 업데이트 정보
     * @param originalMemoCtgySid 기존 폴더 ID
     * @return 처리 건수
     */
    int updateMemoFolder(@Param("memo") COM2080002Vo param, @Param("originalMemoCtgySid") Integer originalMemoCtgySid);
} 