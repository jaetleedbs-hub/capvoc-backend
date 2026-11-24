package buttle.apptpl.sup.inquiry.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import buttle.apptpl.api.shared.vo.InquiryTypeDeleteReqVo;
import buttle.apptpl.api.shared.vo.InquiryTypeListResVo;
import buttle.apptpl.api.shared.vo.InquiryTypeSaveReqVo;
import buttle.apptpl.api.shared.vo.InquiryTypeSearchReqVo;

import java.util.List;

/**
 * BO303_P1 문의 유형 관리 Mapper
 */
@Mapper
public interface InquiryTypeMapper {

    /**
     * 문의 유형 목록 조회
     *
     * @param param 검색 조건
     * @return 문의 유형 목록
     */
    List<InquiryTypeListResVo> selectInquiryTypeList(InquiryTypeSearchReqVo param);

    /**
     * 코드명 중복 확인
     *
     * @param cdNm 코드명
     * @return 중복 개수
     */
    int checkDuplicateCdNm(@Param("cdNm") String cdNm);

    /**
     * 다음 정렬 순서 조회
     *
     * @return 다음 정렬 순서
     */
    int getNextSortSn();

    /**
     * 문의 유형 등록
     *
     * @param param 등록 데이터
     * @return 등록 건수
     */
    int insertInquiryType(InquiryTypeSaveReqVo param);

    /**
     * 문의 유형 수정
     *
     * @param param 수정 데이터
     * @return 수정 건수
     */
    int updateInquiryType(InquiryTypeSaveReqVo param);

    /**
     * 문의 유형 사용 여부 확인
     *
     * @param cd 코드
     * @return 사용 건수
     */
    int checkInquiryTypeInUse(@Param("cd") String cd);

    /**
     * 문의 유형 삭제 (논리 삭제)
     *
     * @param param 삭제 데이터
     * @return 삭제 건수
     */
    int deleteInquiryType(InquiryTypeDeleteReqVo param);
}
