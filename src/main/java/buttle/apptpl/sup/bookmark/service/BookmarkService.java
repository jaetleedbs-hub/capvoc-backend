package buttle.apptpl.sup.bookmark.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.sup.bookmark.dto.COM2070001Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070002Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070003Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070004Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070005Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070006Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070007Dto;
import buttle.apptpl.sup.bookmark.dto.COM2070008Dto;
import buttle.apptpl.sup.bookmark.mapper.BookmarkMapper;
import buttle.apptpl.common.model.sup.entity.SupBokmkCtgyEntity;
import buttle.apptpl.common.model.sup.entity.SupBokmkCtgyPkey;
import buttle.apptpl.common.model.sup.entity.SupBokmkMenuEntity;
import buttle.apptpl.common.model.sup.entity.SupBokmkMenuPkey;
import buttle.apptpl.common.model.sup.repository.SupBokmkCtgyRepo;
import buttle.apptpl.common.model.sup.repository.SupBokmkMenuRepo;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkMapper mapper;
    private final SupBokmkMenuRepo supBokmkMenuRepo;
    private final SupBokmkCtgyRepo supBokmkCtgyRepo;

    enum Action {
        DELETE,
        INSERT
    }

    /**
     * 즐겨찾기 저장
     * @param dto 슬라이드프레임_즐겨찾기 등록 요청
     * @return 슬라이드프레임_즐겨찾기 등록 응답
     */
    public COM2070002Dto insertBookmark(COM2070001Dto dto) {

        if (ObjectUtils.isEmpty(dto.getBokmkMenuId())) {
            throw new InvalidationException("즐겨찾기 메뉴를 선택하세요.");
        }

        Action action = Action.valueOf(dto.getAction());


        // 북마크 카테고리 ID (프레임에서 저장하는 즐겨찾기는 0으로 고정함)
        Integer bokmkCtgySn = dto.getBokmkCtgySn();
        if( bokmkCtgySn == null) {
            bokmkCtgySn = 0;
        }

        // 사용자 ID
        String userId = SecurityUtils.getCurrentUserId();
        // 복합키 생성 및 기존 데이터 확인
        SupBokmkMenuPkey supBokmkMenuPkey = new SupBokmkMenuPkey(dto.getBokmkMenuId(), bokmkCtgySn, userId);
        Optional<SupBokmkMenuEntity> optionalSupBokmkMenuEntity = supBokmkMenuRepo.findById(supBokmkMenuPkey);
        // 이미 등록된 경우에는 삭제한다.
        if( action == Action.DELETE ) {
            String message = "기존에 저장된 즐겨찾기가 없습니다.";
            if( optionalSupBokmkMenuEntity.isPresent() ) {
                supBokmkMenuRepo.deleteById(supBokmkMenuPkey);
                message = "즐겨찾기에서 삭제하였습니다.";
            }
            return COM2070002Dto.builder().action(dto.getAction()).message(message).build();
        } else {
            if( optionalSupBokmkMenuEntity.isPresent() ) {
                return COM2070002Dto.builder().action(dto.getAction()).message("이미 즐겨찾기에 등록된 메뉴입니다.").build();
            }
        }

        // 카테고리가 미리 생성되었는지 확인
        SupBokmkCtgyPkey supBokmkCtgyPkey = new SupBokmkCtgyPkey(bokmkCtgySn, userId);

        // 카테고리 생성 확인 후 없을 경우 카테고리 생성
        Optional<SupBokmkCtgyEntity> existingCtgyOpt = supBokmkCtgyRepo.findById(supBokmkCtgyPkey);
        if(!existingCtgyOpt.isPresent() ) {
            // 카테고리 생성
            SupBokmkCtgyEntity ctgy = new SupBokmkCtgyEntity();
            ctgy.setId(supBokmkCtgyPkey);
            ctgy.setBokmkCtgyNm("즐겨찾기");
            ctgy.setSortSn(1);
            supBokmkCtgyRepo.save(ctgy);
        }
        
        // 새로운 즐겨찾기 저장
        SupBokmkMenuEntity supBokmkMenuEntity = new SupBokmkMenuEntity();
        supBokmkMenuEntity.setId(supBokmkMenuPkey);
        supBokmkMenuEntity.setSortSn(1);

        supBokmkMenuRepo.save(supBokmkMenuEntity);
        return COM2070002Dto.builder().action(dto.getAction()).message("즐겨찾기에 등록하였습니다.").build();
    }

    /**
     * 즐겨찾기 카테고리 목록 조회
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<COM2070003Dto> selectBookmarkCategoryList() {
        // 현재 사용자 정보 조회
        String userId = SecurityUtils.getCurrentUserId();
        
        // MyBatis Mapper를 사용하여 카테고리 목록 조회
        return mapper.selectBookmarkCategoryList(userId);
    }

    /**
     * 즐겨찾기 카테고리 저장
     * @param dto 카테고리 저장 요청
     * @return 카테고리 저장 응답
     */
    public COM2070005Dto insertBookmarkCategory(COM2070004Dto dto) {
        // 현재 사용자 정보 조회
        String userId = SecurityUtils.getCurrentUserId();
        // 카테고리명 필수 검증
        if (ObjectUtils.isEmpty(dto.getBokmkCtgyNm()) || dto.getBokmkCtgyNm().trim().isEmpty()) {
            throw new InvalidationException("카테고리명을 입력해주세요.");
        }

        Integer bokmkCtgySn = dto.getBokmkCtgySn();
        
        // 신규 등록인 경우 시퀀스 자동 생성
        if (bokmkCtgySn == null || bokmkCtgySn == 0) {
            Integer maxSequence = mapper.selectMaxCategorySequence(userId);
            bokmkCtgySn = (maxSequence != null ? maxSequence : 0) + 1;
        }

        // 복합키 생성
        SupBokmkCtgyPkey supBokmkCtgyPkey = new SupBokmkCtgyPkey(bokmkCtgySn, userId);

        // 기존 데이터 확인
        Optional<SupBokmkCtgyEntity> existingCtgyOpt = supBokmkCtgyRepo.findById(supBokmkCtgyPkey);

        SupBokmkCtgyEntity ctgyEntity;
        String message;

        if (existingCtgyOpt.isPresent()) {
            // 수정
            ctgyEntity = existingCtgyOpt.get();
            ctgyEntity.setBokmkCtgyNm(dto.getBokmkCtgyNm());
            if (dto.getSortSn() != null) {
                ctgyEntity.setSortSn(dto.getSortSn());
            }
            message = "카테고리가 수정되었습니다.";
        } else {
            // 신규 등록
            ctgyEntity = new SupBokmkCtgyEntity();
            ctgyEntity.setId(supBokmkCtgyPkey);
            ctgyEntity.setBokmkCtgyNm(dto.getBokmkCtgyNm());
            ctgyEntity.setSortSn(dto.getSortSn() != null ? dto.getSortSn() : 999);
            message = "카테고리가 등록되었습니다.";
        }

        // 저장
        supBokmkCtgyRepo.save(ctgyEntity);

        return COM2070005Dto.builder()
                .bokmkCtgySn(bokmkCtgySn)
                .bokmkCtgyNm(dto.getBokmkCtgyNm())
                .sortSn(ctgyEntity.getSortSn())
                .message(message)
                .build();
    }

    /**
     * 즐겨찾기 카테고리 삭제
     * @param dto 카테고리 삭제 요청
     * @return 카테고리 삭제 응답
     */
    public COM2070007Dto deleteBookmarkCategory(COM2070006Dto dto) {
        // 현재 사용자 정보 조회
        String userId = SecurityUtils.getCurrentUserId();

        // 카테고리 시퀀스 필수 검증
        if (dto.getBokmkCtgySn() == null) {
            throw new InvalidationException("삭제할 카테고리를 선택해주세요.");
        }

        // 복합키 생성
        SupBokmkCtgyPkey supBokmkCtgyPkey = new SupBokmkCtgyPkey(dto.getBokmkCtgySn(), userId);

        // 기존 데이터 확인
        Optional<SupBokmkCtgyEntity> existingCtgyOpt = supBokmkCtgyRepo.findById(supBokmkCtgyPkey);

        if (!existingCtgyOpt.isPresent()) {
            throw new InvalidationException("삭제할 카테고리를 찾을 수 없습니다.");
        }
        
        // 즐겨찾기에 속한 하위 메뉴 삭제
        supBokmkMenuRepo.findAll(Example.of(SupBokmkMenuEntity.builder().id(SupBokmkMenuPkey.builder().userId(userId).bokmkCtgySn(dto.getBokmkCtgySn()).build()).build())).stream().forEach(menu -> {
            supBokmkMenuRepo.deleteById(menu.getId());
        });

        // 카테고리 삭제
        supBokmkCtgyRepo.deleteById(supBokmkCtgyPkey);

        return COM2070007Dto.builder()
                .bokmkCtgySn(dto.getBokmkCtgySn())
                .message("카테고리가 삭제되었습니다.")
                .build();
    }

    /**
     * 즐겨찾기 목록 조회
     * @return 즐겨찾기 목록
     */
    @Transactional(readOnly = true)
    public List<COM2070008Dto> selectBookmarkList() {
        // 현재 사용자 정보 조회
        String userId = SecurityUtils.getCurrentUserId();
        return mapper.selectBookmarkList(userId);
    }
} 