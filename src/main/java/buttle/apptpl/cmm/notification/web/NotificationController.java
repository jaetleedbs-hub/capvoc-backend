package buttle.apptpl.cmm.notification.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.cmm.notification.dto.NoticeDto;
import buttle.apptpl.cmm.notification.dto.NotificationsDto;
import buttle.apptpl.cmm.notification.dto.NotificationsRequestDto;
import buttle.apptpl.cmm.notification.dto.ServiceAnswerDto;
import buttle.apptpl.cmm.notification.dto.UmsSedngInfo;
import buttle.apptpl.cmm.notification.service.NotificationService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/notification")
@Tag(name = "NotificationController", description = "알림")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class NotificationController {
    
    private final NotificationService notificationsService;

    @PostMapping("/all")
    @Operation(summary = "알림 조회", description = "알림 조회")    
    public ResponseEntity<NotificationsDto> selectNotifications( @RequestBody NotificationsRequestDto notificationsRequestDto) {
        return ResponseEntity.ok(notificationsService.selectNotifications(notificationsRequestDto));
    }


    @PostMapping("/notice/list")
    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록 조회")
    public ResponseEntity<List<NoticeDto>> selectNoticeList(@RequestBody NotificationsRequestDto notificationsRequestDto) {
        return ResponseEntity.ok(notificationsService.selectNotice(notificationsRequestDto));
    }


    @PostMapping("/service-answer/list")
    @Operation(summary = "서비스 답변 목록 조회", description = "서비스 답변 목록 조회")
    public ResponseEntity<List<ServiceAnswerDto>> selectServiceAnswerList(@RequestBody NotificationsRequestDto notificationsRequestDto) {
        return ResponseEntity.ok(notificationsService.selectServiceAnswer(notificationsRequestDto));
    }


    @PostMapping("/admin-message/list")
    @Operation(summary = "어드민 메시지 목록 조회", description = "어드민 메시지 목록 조회")
    public ResponseEntity<List<UmsSedngInfo>> selectAdminMessageList(@RequestBody NotificationsRequestDto notificationsRequestDto) {
        return ResponseEntity.ok(notificationsService.selectAdminMessage(notificationsRequestDto));
    }




    @PostMapping("/notice/read")
    @Operation(summary = "공지사항 읽음 처리", description = "공지사항 읽음 처리")
    public ResponseEntity<Void> markNoticeAsRead(@RequestBody NoticeDto noticeDto) {
        notificationsService.markNoticeAsRead(noticeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notice/view")
    @Operation(summary = "공지사항 화면 표시 처리", description = "공지사항 화면 표시 처리")
    public ResponseEntity<Void> markNoticeAsView(@RequestBody NoticeDto noticeDto) {
        notificationsService.markNoticeAsView(noticeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/service-answer/read")
    @Operation(summary = "서비스 답변 읽음 처리", description = "서비스 답변 읽음 처리")
    public ResponseEntity<Void> markServiceAnswerAsRead(@RequestBody ServiceAnswerDto serviceAnswerDto) {
        notificationsService.markServiceAnswerAsRead(serviceAnswerDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/service-answer/view")
    @Operation(summary = "서비스 답변 화면 표시 처리", description = "서비스 답변 화면 표시 처리")
    public ResponseEntity<Void> markServiceAnswerAsView(
        @RequestBody ServiceAnswerDto serviceAnswerDto) {
        notificationsService.markServiceAnswerAsView(serviceAnswerDto);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/admin-message/read")
    @Operation(summary = "어드민 메시지 읽음 처리", description = "어드민 메시지 읽음 처리")
    public ResponseEntity<Void> markUmsAsRead(@RequestBody UmsSedngInfo umsSedngInfo) {
        notificationsService.markAdminMessageAsRead(umsSedngInfo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin-message/view")
    @Operation(summary = "어드민 메시지 화면 표시 처리", description = "어드민 메시지 화면 표시 처리")
    public ResponseEntity<Void> markAdminMessageAsView(@RequestBody UmsSedngInfo umsSedngInfo) {
        notificationsService.markAdminMessageAsView(umsSedngInfo);
        return ResponseEntity.ok().build();
    }


    
}