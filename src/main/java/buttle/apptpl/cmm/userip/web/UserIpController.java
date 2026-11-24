package buttle.apptpl.cmm.userip.web;

import java.util.List;

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
import buttle.apptpl.cmm.userip.dto.SYS0080001Dto;
import buttle.apptpl.cmm.userip.dto.SYS0080002Dto;
import buttle.apptpl.cmm.userip.service.UserIpService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

/**
 * 사용자IP관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/user-ip")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@Tag(name = "UserIpController", description = "사용자IP관리")
public class UserIpController {

    private final UserIpService service;

    /**
     * 사용자 IP별 내선정보 조회
     * @param dto 사용자IP관리 요청 DTO
     * @return 사용자IP관리 응답 DTO 리스트
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 IP별 내선정보 조회", description = "사용자 IP별 내선정보 조회")
    public List<SYS0080002Dto> selectUserIpTypeExtInfoList(@RequestBody SYS0080001Dto dto) {
        return service.selectUserIpTypeExtInfoList(dto);
    }

    /**
     * 사용자 IP별 내선정보 저장
     * @param dto 사용자IP관리 요청 DTO
     * @return 사용자IP관리 응답 DTO
     */
    @PostMapping("/u01")
    @Operation(summary = "사용자 IP별 내선정보 저장", description = "사용자 IP별 내선정보 저장")
    public Integer updateUserIpTypeExtInfoList(@RequestBody List<SYS0080002Dto> dtoList) {
        return service.updateUserIpTypeExtInfoList(dtoList);
    }

    /**
     * 사용자 IP별 내선정보 삭제
     * @param dtoList 삭제할 사용자IP관리 DTO 리스트
     * @return 삭제된 행 수
     */
    @PostMapping("/d01")
    @Operation(summary = "사용자 IP별 내선정보 삭제", description = "사용자 IP별 내선정보 삭제")
    public Integer deleteUserIpTypeExtInfoList(@RequestBody List<SYS0080002Dto> dtoList) {
        return service.deleteUserIpTypeExtInfoList(dtoList);
    }
} 