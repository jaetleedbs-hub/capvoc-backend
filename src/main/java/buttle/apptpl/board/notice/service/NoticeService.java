package buttle.apptpl.board.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.board.notice.mapper.NoticeMapper;
import buttle.apptpl.api.shared.vo.*;
import buttle.apptpl.cmm.notification.dto.NoticeDto;
import buttle.apptpl.cmm.notification.mapper.NotificationMapper;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * BO301_S 시스템 공지사항 관리 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeMapper mapper;
    private final NotificationMapper notificationMapper;

    /** 공지 게시판 ID — 이 화면은 시스템 공지사항 전용이다(매퍼도 'NOTICE' 로 고정돼 있다). */
    private static final String NOTICE_BBS_ID = "NOTICE";

    /**
     * 파일 업로드
     */
    public NoticeFileUploadResDto uploadNoticeFile(MultipartFile file) {
        log.debug("파일 업로드 시작 - fileName: {}", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new InvalidationException("업로드할 파일을 선택해 주세요.");
        }

        try {
            // 파일 그룹 ID 생성
            String atchFileGroupId = UUID.randomUUID().toString();
            
            // TODO: 실제 파일 저장 로직 구현 필요
            // 여기서는 간단히 응답만 생성
            
            return NoticeFileUploadResDto.builder()
                    .atchFileGroupId(atchFileGroupId)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .build();

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new InvalidationException("파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 공지사항 목록조회
     */
    public List<NoticeListResDto> getNoticeList(NoticeSearchReqDto dto) {
        log.debug("공지사항 목록조회 시작 - bbsId: {}, ttl: {}, viewUseYn: {}", 
                dto.getBbsId(), dto.getTtl(), dto.getViewUseYn());

        try {
            BO301SNoticeSearchReqVo vo = new BO301SNoticeSearchReqVo();
            BeanUtils.copyProperties(dto, vo);
            
            // 기본값 설정
            if (ObjectUtils.isEmpty(vo.getBbsId())) {
                vo.setBbsId("NOTICE");
            }

            log.debug("Mapper 호출 전 - vo: {}", vo);
            List<BO301SNoticeListResVo> voList = mapper.selectNoticeList(vo);
            log.debug("Mapper 호출 후 - 조회 건수: {}", voList != null ? voList.size() : 0);

            return voList.stream().map(v -> {
                NoticeListResDto result = new NoticeListResDto();
                BeanUtils.copyProperties(v, result);
                return result;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("공지사항 목록조회 실패 - 상세 에러", e);
            throw new RuntimeException("공지사항 목록 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 공지사항 상세조회
     */
    public NoticeDetailResDto getNoticeDetail(String pstId) {
        log.debug("공지사항 상세조회 시작 - pstId: {}", pstId);

        if (ObjectUtils.isEmpty(pstId)) {
            throw new InvalidationException("게시물ID를 입력해 주세요.");
        }

        try {
            // 조회수 증가
            log.debug("조회수 증가 - pstId: {}", pstId);
            mapper.updateInquiryCount(pstId);
            
            // 상세 조회
            BO301SNoticeDetailResVo vo = mapper.selectNoticeDetail(pstId);
            
            if (vo == null) {
                throw new InvalidationException("해당 공지사항을 찾을 수 없습니다.");
            }

            // 이 사용자의 <읽음> 기록. inq_cnt(누적 조회수)와는 다른 값이다 —
            // 알림 배지의 미확인 건수는 사용자별 읽음(cmpt_bbs_read_sts)으로만 줄어든다.
            markCurrentUserRead(pstId);

            NoticeDetailResDto result = new NoticeDetailResDto();
            BeanUtils.copyProperties(vo, result);
            
            log.debug("공지사항 상세조회 완료 - 조회수: {}", vo.getInqCnt());
            
            return result;

        } catch (Exception e) {
            log.error("공지사항 상세조회 실패", e);
            throw new InvalidationException("공지사항 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 상세를 연 사용자에게 <읽음>을 기록한다.
     *
     * <p>desk 는 같은 일을 `/api/cmm/notification/notice/read` 로 하는데 그 경로는
     * S2S 전용(브라우저 세션은 403)이고 `mbrId` 를 <요청 본문>에서 받는다.
     * 관리콘솔은 그 규약을 쓰지 않고 인증 주체에서만 사용자를 얻는다.
     *
     * <p>익명 세션은 기록하지 않는다 — `SecurityUtils.getCurrentUser()` 는 인증이 없을 때
     * `sid-<세션id>` 를 userId 로 갖는 SecUser 를 만들어 주므로, 그대로 쓰면
     * 표에 세션마다 다른 가짜 사용자가 쌓인다.
     */
    private void markCurrentUserRead(String pstId) {
        SecUser secUser = SecurityUtils.getCurrentUser();
        if (secUser == null || !Boolean.TRUE.equals(secUser.getEnabled())) {
            return;
        }

        NoticeDto readDto = new NoticeDto();
        readDto.setBbsId(NOTICE_BBS_ID);
        readDto.setPstId(pstId);
        readDto.setMbrId(secUser.getUserId());
        notificationMapper.insertNoticeReadStatus(readDto);
    }

    /**
     * 공지사항 저장 (파일 포함)
     */
    public NoticeCommonResDto saveNoticeWithFile(NoticeSaveReqDto dto, MultipartFile file) {
        log.debug("공지사항 저장 시작 (파일 포함) - pstId: {}, 파일명: {}", 
                  dto.getPstId(), 
                  file != null ? file.getOriginalFilename() : "없음");

        // 유효성 검증
        if (ObjectUtils.isEmpty(dto.getBbsId())) {
            throw new InvalidationException("게시판ID를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getTtl())) {
            throw new InvalidationException("제목을 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getPstCn())) {
            throw new InvalidationException("내용을 입력해 주세요.");
        }

        try {
            // 파일이 있으면 업로드 처리
            if (file != null && !file.isEmpty()) {
                log.debug("파일 업로드 처리 시작");
                NoticeFileUploadResDto fileResult = uploadNoticeFile(file);
                dto.setAtchFileGroupId(fileResult.getAtchFileGroupId());

                log.debug("파일 업로드 완료 - atchFileGroupId: {}, fileName: {}", 
                          fileResult.getAtchFileGroupId(), fileResult.getFileName());
            }

            BO301SNoticeSaveReqVo vo = new BO301SNoticeSaveReqVo();
            BeanUtils.copyProperties(dto, vo);

            int result;
            
            if (ObjectUtils.isEmpty(dto.getPstId())) {
                // 신규 등록
                vo.setPstId(UUID.randomUUID().toString());
                result = mapper.insertNotice(vo);
                log.debug("신규 등록 완료 - pstId: {}", vo.getPstId());
            } else {
                // 수정
                int exists = mapper.selectNoticeExists(dto.getPstId());
                if (exists == 0) {
                    throw new InvalidationException("해당 공지사항을 찾을 수 없습니다.");
                }
                result = mapper.updateNotice(vo);
                log.debug("수정 완료 - pstId: {}", vo.getPstId());
            }

            log.debug("공지사항 저장 완료 - pstId: {}, 첨부파일: {}", 
                      vo.getPstId(), vo.getAtchFileGroupId());

            return NoticeCommonResDto.builder()
                    .successCount(result)
                    .message("저장 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("공지사항 저장 실패", e);
            throw new InvalidationException("공지사항 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 공지사항 저장
     */
    public NoticeCommonResDto saveNotice(NoticeSaveReqDto dto) {
        log.debug("공지사항 저장 시작 - pstId: {}", dto.getPstId());

        // 유효성 검증
        if (ObjectUtils.isEmpty(dto.getBbsId())) {
            throw new InvalidationException("게시판ID를 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getTtl())) {
            throw new InvalidationException("제목을 입력해 주세요.");
        }
        if (ObjectUtils.isEmpty(dto.getPstCn())) {
            throw new InvalidationException("내용을 입력해 주세요.");
        }

        try {
            BO301SNoticeSaveReqVo vo = new BO301SNoticeSaveReqVo();
            BeanUtils.copyProperties(dto, vo);

            int result;
            
            if (ObjectUtils.isEmpty(dto.getPstId())) {
                // 신규 등록
                vo.setPstId(UUID.randomUUID().toString());
                result = mapper.insertNotice(vo);
            } else {
                // 수정
                int exists = mapper.selectNoticeExists(dto.getPstId());
                if (exists == 0) {
                    throw new InvalidationException("해당 공지사항을 찾을 수 없습니다.");
                }
                result = mapper.updateNotice(vo);
            }

            log.debug("공지사항 저장 완료 - pstId: {}", vo.getPstId());

            return NoticeCommonResDto.builder()
                    .successCount(result)
                    .message("저장 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("공지사항 저장 실패", e);
            throw new InvalidationException("공지사항 저장에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 공지사항 삭제
     */
    public NoticeCommonResDto deleteNotice(String pstId) {
        log.debug("공지사항 삭제 시작 - pstId: {}", pstId);

        if (ObjectUtils.isEmpty(pstId)) {
            throw new InvalidationException("게시물ID를 입력해 주세요.");
        }

        try {
            int exists = mapper.selectNoticeExists(pstId);
            if (exists == 0) {
                throw new InvalidationException("해당 공지사항을 찾을 수 없습니다.");
            }

            // 읽음 상태를 먼저 지운다 — FK 가 없어 게시물만 지우면 고아 행이 남는다.
            notificationMapper.deleteNoticeReadStatusByPost(pstId);

            int result = mapper.deleteNotice(pstId);

            log.debug("공지사항 삭제 완료 - pstId: {}", pstId);

            return NoticeCommonResDto.builder()
                    .successCount(result)
                    .message("삭제 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("공지사항 삭제 실패", e);
            throw new InvalidationException("공지사항 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
