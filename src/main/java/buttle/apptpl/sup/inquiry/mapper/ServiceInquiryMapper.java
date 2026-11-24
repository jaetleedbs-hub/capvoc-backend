package buttle.apptpl.sup.inquiry.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.api.shared.vo.InquirySearchReqVo;
import buttle.apptpl.api.shared.vo.InquiryListResVo;
import buttle.apptpl.api.shared.vo.InquiryAnswerSaveReqVo;
import buttle.apptpl.api.shared.vo.InquiryRegisterReqVo;
import buttle.apptpl.api.shared.vo.InquiryTypeResVo;
import buttle.apptpl.api.shared.vo.MyInquiryListResVo;
import buttle.apptpl.api.shared.vo.MyInquirySearchReqVo;

import java.util.List;

/**
 * BO303_S 서비스 문의 관리 Mapper
 */
@Mapper
public interface ServiceInquiryMapper {
    
    /**
     * 서비스 문의 목록 조회
     * @param param 조회 조건
     * @return 서비스 문의 목록
     */
    List<InquiryListResVo> selectInquiryList(InquirySearchReqVo param);
    
    /**
     * 서비스 문의 답변 저장
     * @param param 저장 데이터
     * @return 저장 건수
     */
    int updateInquiryAnswer(InquiryAnswerSaveReqVo param);

    /**
     * 서비스 문의 등록
     * @param param 등록 데이터
     * @return 등록 건수
     */
    int insertInquiry(InquiryRegisterReqVo param);

    /**
     * 내 서비스 문의 목록 조회 (등록자 본인 것만)
     * @param param 조회 조건
     * @param pageable 페이징 정보
     * @return 서비스 문의 목록
     */
    Page<MyInquiryListResVo> selectMyInquiryList(MyInquirySearchReqVo param, Pageable pageable);

    /**
     * 문의 유형 목록 조회
     * @return 문의 유형 목록
     */
    List<InquiryTypeResVo> selectInquiryTypeList();
}
