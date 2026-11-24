package buttle.apptpl.board.faq.service;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import buttle.apptpl.api.shared.dto.CategoryDeleteReqDto;
import buttle.apptpl.api.shared.dto.CategoryListReqDto;
import buttle.apptpl.api.shared.dto.FaqCategoryResDto;
import buttle.apptpl.api.shared.dto.CategorySaveReqDto;
import buttle.apptpl.common.constant.AuditConstants;
import buttle.apptpl.board.faq.mapper.FaqCategoryMapper;
import buttle.apptpl.api.shared.vo.CategoryDeleteReqVo;
import buttle.apptpl.api.shared.vo.CategoryListReqVo;
import buttle.apptpl.api.shared.vo.CategoryListResVo;
import buttle.apptpl.api.shared.vo.CategorySaveReqVo;
import buttle.apptpl.common.util.ModelConveter;

import java.util.List;

/**
 * BO302_P1 FAQ 분류 관리 Service
 */
@Service
@RequiredArgsConstructor
@Transactional
public class FaqCategoryService {

    private final FaqCategoryMapper mapper;
    private final ModelConveter modelConveter;

    /**
     * FAQ 분류 목록 조회
     * @param dto 분류 목록 요청
     * @return 분류 목록
     */
    public List<FaqCategoryResDto> selectCategoryList(CategoryListReqDto dto) {
        CategoryListReqVo vo = modelConveter.map(dto, CategoryListReqVo.class);
        List<CategoryListResVo> result = mapper.selectCategoryList(vo);
        return modelConveter.mapList(result, FaqCategoryResDto.class);
    }

    /**
     * FAQ 분류 저장 (등록/수정)
     * @param dto 분류 저장 요청
     * @return 저장 건수
     */
    public Integer saveCategory(CategorySaveReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getCdNm())) {
            throw new InvalidationException("분류명을 입력하세요.");
        }

        if (dto.getCdNm().length() > 50) {
            throw new InvalidationException("분류명은 50자 이내로 입력하세요.");
        }

        CategorySaveReqVo vo = modelConveter.map(dto, CategorySaveReqVo.class);
        
        // 현재 사용자 ID 설정
        String userId = getCurrentUserId();

        // 코드가 있으면 수정, 없으면 등록
        if (!ObjectUtils.isEmpty(dto.getCd())) {
            // 수정: 코드 존재 여부 확인
            int exists = mapper.existsCategory(dto.getCd());
            if (exists == 0) {
                throw new InvalidationException("수정할 분류 정보가 존재하지 않습니다.");
            }
            vo.setUpdId(userId);
            return mapper.updateCategory(vo);
        } else {
            // 등록: 정렬순서가 없으면 자동 채번
            if (vo.getSortOrder() == null) {
                vo.setSortOrder(mapper.selectNextSortOrder());
            }
            vo.setRegId(userId);
            return mapper.insertCategory(vo);
        }
    }

    /**
     * FAQ 분류 삭제
     * @param dto 분류 삭제 요청
     * @return 삭제 건수
     */
    public Integer deleteCategory(CategoryDeleteReqDto dto) {
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getCd())) {
            throw new InvalidationException("삭제할 분류를 선택하세요.");
        }

        // 코드 존재 확인
        int exists = mapper.existsCategory(dto.getCd());
        if (exists == 0) {
            throw new InvalidationException("삭제할 분류 정보가 존재하지 않습니다.");
        }

        CategoryDeleteReqVo vo = modelConveter.map(dto, CategoryDeleteReqVo.class);
        return mapper.deleteCategory(vo);
    }
    
    /**
     * 현재 사용자 ID를 가져오는 유틸리티 메서드
     * @return 현재 사용자 ID
     */
    private String getCurrentUserId() {
        SecUser currentUser = SecurityUtils.getCurrentUser();
        
        if (currentUser != null && currentUser.getUserId() != null) {
            return currentUser.getUserId();
        }
        
        // 로그인되지 않은 경우 기본값 반환
        return AuditConstants.SYSTEM;
    }
}
