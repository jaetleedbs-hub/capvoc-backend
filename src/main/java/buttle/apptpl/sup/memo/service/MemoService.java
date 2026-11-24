package buttle.apptpl.sup.memo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.sup.memo.dto.COM2080001Dto;
import buttle.apptpl.sup.memo.dto.COM2080002Dto;
import buttle.apptpl.sup.memo.dto.COM2080003Dto;
import buttle.apptpl.sup.memo.dto.COM2080004Dto;
import buttle.apptpl.sup.memo.mapper.MemoMapper;
import buttle.apptpl.sup.memo.vo.COM2080001Vo;
import buttle.apptpl.sup.memo.vo.COM2080002Vo;
import buttle.apptpl.common.model.sup.entity.SupMemoCtgyEntity;
import buttle.apptpl.common.model.sup.entity.SupMemoCtgyPkey;
import buttle.apptpl.common.model.sup.entity.SupMemoEntity;
import buttle.apptpl.common.model.sup.entity.SupMemoPkey;
import buttle.apptpl.common.model.sup.repository.SupMemoCtgyRepo;
import buttle.apptpl.common.model.sup.repository.SupMemoRepo;
import buttle.apptpl.common.util.ModelConveter;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {

    private final MemoMapper mapper;
    private final ModelConveter modelConveter;
    private final SupMemoRepo supMemoRepo;
    private final SupMemoCtgyRepo supMemoCtgyRepo;

    enum Action {
        DELETE,
        INSERT,
        UPDATE
    }

    /**
     * 저장
     * @param dto 슬라이드프레임_메모 등록
     * @return 슬라이드프레임_메모 등록 응답
     */
    public COM2080001Dto updateMemo(COM2080001Dto dto) {
        if(dto == null){
            throw new InvalidationException ("요청한 데이터가 없습니다.");
        }

        String userId = SecurityUtils.getCurrentUserId();
        Action action = Action.valueOf(dto.getAction());
       
        // 필수 필드 검증
        if (ObjectUtils.isEmpty(dto.getMemoCn()) && action != Action.DELETE) {
            throw new InvalidationException("메모 내용을 입력하세요.");
        }

        Integer memoCtgySid = dto.getMemoCtgySid();
        Integer memoSid = dto.getMemoSid();
        
        // 기존 폴더 정보 확인 (수정 모드에서 폴더 변경 시 사용)
        Integer originalMemoCtgySid = dto.getOriginalMemoCtgySid();

        // 복합키 생성 (항상 기존 카테고리 키로 검색)
        Integer searchMemoCtgySid = (originalMemoCtgySid != null && action == Action.UPDATE) ? originalMemoCtgySid : memoCtgySid;
        SupMemoPkey supMemoPkey = new SupMemoPkey(memoSid, searchMemoCtgySid, SecurityUtils.getCurrentUserId());

        // 기존 데이터 확인
        Optional<SupMemoEntity> existingMemo = supMemoRepo.findById(supMemoPkey);
        // String message;

        if (existingMemo.isPresent() && action == Action.UPDATE) {
            // 기존 데이터 업데이트
            SupMemoEntity memoEntity = existingMemo.get();
            
            // 폴더가 변경된 경우 직접 SQL UPDATE로 처리 (memo_sid는 identity라서 재생성 불가)
            if (originalMemoCtgySid != null && !originalMemoCtgySid.equals(memoCtgySid)) {
                // MyBatis 매퍼를 사용하여 직접 SQL UPDATE
                COM2080002Vo updateVo = new COM2080002Vo();
                updateVo.setUserId(userId);
                updateVo.setMemoSid(memoSid);
                updateVo.setMemoCtgySid(memoCtgySid);  // 새로운 폴더 ID
                updateVo.setMemoTtlNm(dto.getMemoTtlNm());
                updateVo.setMemoCn(dto.getMemoCn());
                updateVo.setMemoAlrmYn(dto.getMemoAlrmYn());
                updateVo.setMemoAlrmDt(dto.getMemoAlrmDt());
                updateVo.setSortSn(memoEntity.getSortSn()); // 기존 sortSn 유지
                
                // 직접 SQL로 memo_ctgy_sid 업데이트 (WHERE 조건에 기존 폴더 ID 사용)
                mapper.updateMemoFolder(updateVo, originalMemoCtgySid);
                return COM2080001Dto.builder()
                    .memoCtgySid(memoCtgySid)
                    .memoSid(memoSid)
                    .memoCn(dto.getMemoCn())
                    .memoTtlNm(dto.getMemoTtlNm())
                    .memoAlrmYn(dto.getMemoAlrmYn())
                    .memoAlrmDt(dto.getMemoAlrmDt())
                    .sortSn(dto.getSortSn())
                    .build();
            }
            
            // 데이터 업데이트 (sortSn은 기존 값 유지)
            memoEntity.setMemoCn(dto.getMemoCn());
            memoEntity.setMemoTtlNm(dto.getMemoTtlNm());
            memoEntity.setMemoAlrmYn(dto.getMemoAlrmYn());
            memoEntity.setMemoAlrmDt(dto.getMemoAlrmDt());
            // sortSn은 수정하지 않음 (기존 순서 유지)
            supMemoRepo.save(memoEntity);

            // message = "메모가 수정되었습니다.";  

        } else if(memoSid == null || !existingMemo.isPresent()) {
            COM2080002Vo vo = modelConveter.map(dto, COM2080002Vo.class);
            if(vo == null){
                throw new InvalidationException ("전달된 값이 올바르지 않습니다.");
            }
            vo.setUserId(userId);
            if (ObjectUtils.isEmpty(dto.getMemoAlrmYn())) {
                vo.setMemoAlrmYn("N");
            }
            if(memoCtgySid == null) {
                vo.setMemoCtgySid(0);
            }
            mapper.insertMemo(vo);
        }

        if( action == Action.DELETE ) {
            if(existingMemo.isPresent()) {
                supMemoRepo.deleteById(supMemoPkey);
            }
            return COM2080001Dto.builder()
                .memoCtgySid(memoCtgySid)
                .memoSid(memoSid)
                .memoCn(dto.getMemoCn())
                .memoAlrmYn(dto.getMemoAlrmYn())
                .memoAlrmDt(dto.getMemoAlrmDt())
                .sortSn(dto.getSortSn())
                .build();
        }

        return COM2080001Dto.builder()
            .memoCtgySid(memoCtgySid)
            .memoSid(memoSid)
            .memoCn(dto.getMemoCn())
            .memoAlrmYn(dto.getMemoAlrmYn())
            .memoAlrmDt(dto.getMemoAlrmDt())    
            .sortSn(dto.getSortSn())
            // .message(message)
            .build();
    }

    /**
     * 메모 카테고리 등록
     * @param dto 메모 카테고리 등록 요청
     * @return 메모 카테고리 등록 응답
     */
    public COM2080003Dto insertMemoCategory(COM2080003Dto dto) {
        if(dto == null){
            throw new InvalidationException ("요청한 데이터가 없습니다.");
        }

        String userId = SecurityUtils.getCurrentUserId();
        if (ObjectUtils.isEmpty(dto.getMemoCtgyNm()) || dto.getMemoCtgyNm().trim().isEmpty()) {
            throw new InvalidationException("카테고리명을 입력해주세요.");
        }

        // 신규 등록인 경우 시퀀스 자동 생성
        Integer memoCtgySid = dto.getMemoCtgySid();
        if(memoCtgySid == null || memoCtgySid == 0) {
            Integer maxSequence = mapper.selectMaxCategorySequence(userId);
            memoCtgySid = (maxSequence != null ? maxSequence : 0) + 1;
        }

        // VO 변환 (COM2080001Vo 사용)
        COM2080001Vo vo = COM2080001Vo.builder()
            .userId(userId)
            .memoCtgySid(memoCtgySid)
            .memoCtgyNm(dto.getMemoCtgyNm())
            .sortSn(dto.getSortSn() != null ? dto.getSortSn() : 1)
            .build();

        // MyBatis Mapper를 통한 저장
        mapper.insertMemoCategory(vo);

        return COM2080003Dto.builder()
            .memoCtgySid(memoCtgySid)
            .memoCtgyNm(dto.getMemoCtgyNm())
            .sortSn(dto.getSortSn() != null ? dto.getSortSn() : 1)
            .build();
    }

    /**
     * 슬라이드프레임_메모 카테고리 삭제
     * @param dto 슬라이드프레임_메모 카테고리 삭제 요청
     * @return 슬라이드프레임_메모 카테고리 삭제 응답
     */
    public COM2080001Dto deleteMemoCategory(COM2080001Dto dto) {
        if(dto == null){
            throw new InvalidationException ("요청한 데이터가 없습니다.");
        }

        String userId = SecurityUtils.getCurrentUserId();
        if (ObjectUtils.isEmpty(dto.getMemoCtgySid())) {
            throw new InvalidationException("삭제할 카테고리를 선택해주세요.");
        }

        // 복합키 생성
        SupMemoCtgyPkey supMemoCtgyPkey = new SupMemoCtgyPkey(dto.getMemoCtgySid(), userId);

        // 기존 데이터 확인
        Optional<SupMemoCtgyEntity> existingMemoCtgy = supMemoCtgyRepo.findById(supMemoCtgyPkey);

        if(!existingMemoCtgy.isPresent()) {
            throw new InvalidationException("삭제할 카테고리를 찾을 수 없습니다.");
        }

        // 카테고리에 속한 하위 메뉴 삭제
        supMemoRepo.findAll(Example.of(SupMemoEntity.builder().id(SupMemoPkey.builder().userId(userId).memoCtgySid(dto.getMemoCtgySid()).build()).build())).stream().forEach(memo -> {
            supMemoRepo.deleteById(memo.getId());
        });

        supMemoCtgyRepo.deleteById(supMemoCtgyPkey);

        return COM2080001Dto.builder()
            .memoCtgySid(dto.getMemoCtgySid())
            .build();
    }
        

    /**
     * 슬라이드프레임_메모 등록 조회
     * @param dto 슬라이드프레임_메모 등록
     * @return 슬라이드프레임_메모 등록 응답
     */
    @Transactional(readOnly = true)
    public COM2080001Dto selectMemo(String memoId, String memoCtgySid) {
        if(memoId == null || memoCtgySid == null){
            throw new InvalidationException ("요청한 데이터가 없습니다.");
        }

        COM2080001Vo vo = COM2080001Vo.builder()
            .userId(SecurityUtils.getCurrentUserId())
            .memoSid(Integer.parseInt(memoId))
            .memoCtgySid(Integer.parseInt(memoCtgySid))
            .build();
        COM2080002Vo result = mapper.selectMemo(vo);
        return modelConveter.map(result, COM2080001Dto.class);
    }

    /**
     * 슬라이드프레임_메모 등록 목록조회
     * @return 슬라이드프레임_메모 등록 응답 목록
     */
    @Transactional(readOnly = true)
    public List<COM2080002Dto> selectMemoList() {
        String userId = SecurityUtils.getCurrentUserId();
       
        return mapper.selectMemoList(userId);
    }

    /**
     * 슬라이드프레임_메모 등록 삭제
     * @param dto 슬라이드프레임_메모 등록 요청
     * @return 처리 건수
     */
    public Integer deleteMemo(COM2080001Dto dto) {
        if(dto == null){
            throw new InvalidationException ("요청한 데이터가 없습니다.");
        }

        String userId = SecurityUtils.getCurrentUserId();

        COM2080001Vo vo = COM2080001Vo.builder()
            .userId(userId)
            .memoSid(dto.getMemoSid())
            .memoCtgySid(dto.getMemoCtgySid())
            .build();
        return mapper.deleteMemo(vo);
    }

    /**
     * 슬라이드프레임_메모 카테고리 목록조회
     * @return 슬라이드프레임_메모 카테고리 목록조회 응답
     */
    @Transactional(readOnly = true)
    public List<COM2080004Dto> selectMemoCategory() {
        String userId = SecurityUtils.getCurrentUserId();
        return mapper.selectMemoCategory(userId);
    }

    @Transactional(readOnly = true)
    public Integer selectMaxMemoSequence(String userId) {
        return mapper.selectMaxMemoSequence(userId);
    }
} 