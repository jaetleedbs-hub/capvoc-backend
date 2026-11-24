package buttle.apptpl.cmm.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.cmm.notification.dto.NoticeDto;
import buttle.apptpl.cmm.notification.dto.NotificationsDto;
import buttle.apptpl.cmm.notification.dto.NotificationsRequestDto;
import buttle.apptpl.cmm.notification.dto.ServiceAnswerDto;
import buttle.apptpl.cmm.notification.dto.UmsSedngInfo;
import buttle.apptpl.cmm.notification.mapper.NotificationMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationMapper notificationsMapper;

    /**
     * 알림 조회
     * 
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @return 알림 조회 결과
     */
    public NotificationsDto selectNotifications(NotificationsRequestDto notificationsRequestDto) {

        NotificationsDto notificationsDto = new NotificationsDto();
        
        // 공지사항 조회
        notificationsDto.setSystemNotice(selectNotice(notificationsRequestDto));

        // 서비스 답변 조회
        notificationsDto.setServiceAnswer(selectServiceAnswer(notificationsRequestDto));

        // admin UMS 조회
        notificationsDto.setAdminMessage(selectAdminMessage(notificationsRequestDto));

        return notificationsDto;
    }

    /**
     * 공지사항 조회
     * 
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @return 공지사항 조회 결과
     */
    public List<NoticeDto> selectNotice(NotificationsRequestDto notificationsRequestDto) {
        return notificationsMapper.selectNotice(notificationsRequestDto);
    }


    /**
     * 서비스 답변 조회
     * 
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @return 서비스 답변 조회 결과
     */
    public List<ServiceAnswerDto> selectServiceAnswer(NotificationsRequestDto notificationsRequestDto) {
        return notificationsMapper.selectServiceAnswer(notificationsRequestDto);
    }


    /**
     * admin UMS 조회
     * 
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @return admin UMS 조회 결과
     */
    public List<UmsSedngInfo> selectAdminMessage(NotificationsRequestDto notificationsRequestDto) {
        return notificationsMapper.selectUmsSedngInfo(notificationsRequestDto);
    }



    /**
     * 공지사항 읽음 처리
     * 
     * @param bbsId 게시판 ID
     * @param pstId 게시물 ID
     */
    public void markNoticeAsRead(NoticeDto noticeDto) {
        notificationsMapper.insertNoticeReadStatus(noticeDto);
    }

    /**
     * 공지사항 화면 표시 처리
     * 
     * @param bbsId 게시판 ID
     * @param pstId 게시물 ID
     */
    public void markNoticeAsView(NoticeDto noticeDto) {

        Integer exists = notificationsMapper.selectExistsNoticeReadStatus(noticeDto);
        if(exists == null || exists == 0) {
            notificationsMapper.insertNoticeReadStatus(noticeDto);
        } else {
            notificationsMapper.updateNoticeViewStatus(noticeDto);
        }

    }



    /**
     * 서비스 답변 읽음 처리
     * 
     * @param qstnId 문의 ID
     * @param cmpyId 회사 ID
     */
    public void markServiceAnswerAsRead(ServiceAnswerDto serviceAnswerDto) {
        notificationsMapper.updateServiceAnswerReadStatus(serviceAnswerDto);
    }

    /**
     * 서비스 답변 화면 표시 처리
     * 
     * @param qstnId 문의 ID
     * @param cmpyId 회사 ID
     */
    public void markServiceAnswerAsView(ServiceAnswerDto serviceAnswerDto) {
        notificationsMapper.updateServiceAnswerViewStatus(serviceAnswerDto);
    }



    /**
     * UMS 읽음 처리
     * 
     * @param umsId UMS ID
     */
    public void markAdminMessageAsRead(UmsSedngInfo umsSedngInfo) {
        umsSedngInfo.setViewUseYn("Y");
        markAdminMessageAsView(umsSedngInfo);
    }

    /**
     * 어드민 메시지 화면 표시 처리
     * 
     * @param umsId UMS ID
     * @param umsSn UMS 순번
     * @param cmpyId 회사 ID
     * @param memberId 회원 ID
     * @param viewUseYn 화면 표시 여부
     */
    public void markAdminMessageAsView(UmsSedngInfo umsSedngInfo) {
        if( "Y".equals(umsSedngInfo.getViewUseYn()) ) {
            umsSedngInfo.setSendRsltCd("9998");
        } else {
            umsSedngInfo.setSendRsltCd("9999");
        }
        notificationsMapper.updateUmsReadStatus(umsSedngInfo);
    }
}
