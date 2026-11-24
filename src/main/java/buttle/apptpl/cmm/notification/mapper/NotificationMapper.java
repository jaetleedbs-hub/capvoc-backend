package buttle.apptpl.cmm.notification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import buttle.apptpl.cmm.notification.dto.NoticeDto;
import buttle.apptpl.cmm.notification.dto.NotificationsRequestDto;
import buttle.apptpl.cmm.notification.dto.ServiceAnswerDto;
import buttle.apptpl.cmm.notification.dto.UmsSedngInfo;

@Mapper
public interface NotificationMapper {

    /**
     * 공지사항 조회
     * 
     * @param noticeDto 공지사항 정보
     * @return admin 폴링 조회 결과
     */
    List<NoticeDto> selectNotice(NotificationsRequestDto notificationsRequestDto);

    /**
     * 공지사항 읽음 상태 등록 (첫 읽음 또는 재읽음 자동 처리)
     * 
     * @param noticeDto 공지사항 정보
     */
    void insertNoticeReadStatus(NoticeDto noticeDto);

    /**
     * 공지사항 읽음 상태 갱신 (최종열람일시, 열람횟수)
     * 
     * @param noticeDto 공지사항 정보
     */
    void updateNoticeReadStatus(NoticeDto noticeDto);

    /**
     * 공지사항 화면 표시 상태 갱신
     * 
     * @param noticeDto 공지사항 정보
     */
    void updateNoticeViewStatus(NoticeDto noticeDto);



    /**
     * 서비스 답변 조회
     * 
     * @param serviceAnswerDto 서비스 답변 정보
     * @return 서비스 답변 정보 목록
     */
    List<ServiceAnswerDto> selectServiceAnswer(NotificationsRequestDto notificationsRequestDto);


    /**
     * 서비스 답변 읽음 상태 업데이트
     * 
     * @param serviceAnswerDto 서비스 답변 정보
     */
    void updateServiceAnswerReadStatus(ServiceAnswerDto serviceAnswerDto);


    /**
     * 서비스 답변 화면 표시 상태 업데이트
     * 
     * @param serviceAnswerDto 서비스 답변 정보
     */
    void updateServiceAnswerViewStatus(ServiceAnswerDto serviceAnswerDto);






    /**
     * admin 폴링 어드민 알림 제목/내용 정보 조회
     * 
     * @param umsSedngInfo UMS 발송 정보
     * @return UMS 발송 정보 목록
     */
    List<UmsSedngInfo> selectUmsSedngInfo(NotificationsRequestDto notificationsRequestDto);


    /**
     * UMS 읽음 상태 업데이트
     * 
     * @param umsSedngInfo UMS 발송 정보
     */
    void updateUmsReadStatus(UmsSedngInfo umsSedngInfo);



    /**
     * 공지사항 읽음 상태 조회
     * 
     * @param noticeDto 공지사항 정보
     * @return 공지사항 읽음 상태
     */
    Integer selectExistsNoticeReadStatus(NoticeDto noticeDto);

    /**
     * 게시물의 읽음 상태를 전부 삭제한다 — 게시물 삭제와 함께 호출한다.
     *
     * <p>`cmpt_bbs_pst` 와 `cmpt_bbs_read_sts` 사이에 FK 가 없어 게시물을 지워도
     * 읽음 행이 남는다(고아). 건수 질의는 게시물과 조인하므로 집계는 틀어지지 않지만,
     * 지운 게시물의 사용자별 열람 이력이 계속 쌓인다.
     *
     * @param pstId 게시물 ID
     */
    void deleteNoticeReadStatusByPost(String pstId);



}
