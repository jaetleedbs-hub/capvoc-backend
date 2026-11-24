package buttle.apptpl.cmm.loginuser.web;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.api.common.components.service.CommonComponentService;
import buttle.apptpl.cmm.loginuser.dto.COM1100001Dto;
import buttle.apptpl.cmm.loginuser.dto.COM1100002Dto;
import buttle.apptpl.cmm.loginuser.service.LoginUserInfoService;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;
import buttle.apptpl.common.exception.vo.ErrorResponse;
import buttle.apptpl.common.framework.common.vo.SecUser;

/**
 * 로그인사용자정보 Controller
 */
    @RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/login-user")
@Tag(name = "LoginUserInfoController", description = "로그인사용자정보")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class LoginUserInfoController {

    private final LoginUserInfoService service;

    private final CommonComponentService commonComponentService;

    /**
     * 내 메뉴 조회 — 메인 화면 기동·새로고침 시 메뉴를 다시 가져오기 위한 것.
     *
     * 로그인 응답(LoginSuccessHandler)과 <같은 서비스 호출>이라 응답 모양이 동일하다.
     * 종전에는 메뉴가 로그인 응답에만 실려 와서, 메뉴를 바꾸면 재로그인 전에는
     * 화면에 반영되지 않았다(사용자 지적 2026-09-02).
     *
     * 인가: 등록표에 http_method='common'(공통 프로그램)으로 등록한다 — r01 과 같다.
     * 자기 권한(authrt_cd)으로 걸러진 메뉴만 돌려주므로 권한 상승 여지가 없다.
     */
    @GetMapping("/menus")
    @Operation(summary = "내 메뉴 조회", description = "로그인 사용자의 권한 메뉴 트리(로그인 응답의 menuList 와 동일)")
    public List<SYS1020002Vo> selectMyMenus() {
        SecUser secUser = SecurityUtils.getCurrentUser();
        return commonComponentService.getUserAuthrtMenuList(secUser.getAuthrtCd(), secUser.getUserId());
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping("/r01")
    @Operation(summary = "사용자 프로필 조회")
    public COM1100001Dto selectUserProfile() {
        COM1100001Dto dto = new COM1100001Dto();
        dto.setUserId(SecurityUtils.getCurrentUserId());
        return service.selectUserProfile(dto);
    }

    /**
     * 사용자 이미지 변경 (base64 데이터)
     */
    @PostMapping("/u01")
    @Operation(summary = "사용자 이미지 변경", description = "사용자 이미지를 base64 데이터로 변경")
    public COM1100001Dto updateUserImageBase64(@RequestBody COM1100002Dto dto) {
        return service.updateUserImageBase64(dto);
    }
} 