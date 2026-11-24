package buttle.apptpl.cmm.user.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.cmm.user.dto.SYS0040001Dto;
import buttle.apptpl.cmm.user.dto.SYS0040002Dto;
import buttle.apptpl.cmm.user.dto.SYS0040003Dto;
import buttle.apptpl.cmm.user.service.UserSearchService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 사용자찾기 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/user/search")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@Tag(name = "UserSearchController", description = "사용자찾기")
public class UserSearchController {

    private final UserSearchService service;

    /**
     * 사용자 소속 조직 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "사용자 소속 조직 조회", description = "사용자 소속 조직 조회")
    public List<SYS0040002Dto> selectUserBelongOrgList(@RequestBody SYS0040001Dto dto) {
        return service.selectUserBelongOrgList(dto);
    }

    /**
     * 조직별 사용자 정보 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "조직별 사용자 정보 조회", description = "조직별 사용자 정보 조회")
    public List<SYS0040003Dto> selectOrgTypeUserInfoList(@RequestBody SYS0040001Dto dto) {
        return service.selectOrgTypeUserInfoList(dto);
    }
} 