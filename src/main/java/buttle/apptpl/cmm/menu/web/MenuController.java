package buttle.apptpl.cmm.menu.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.cmm.menu.dto.MenuByAuthLevelResDto;
import buttle.apptpl.cmm.menu.dto.SYS1010001Dto;
import buttle.apptpl.cmm.menu.dto.SYS1010002Dto;
import buttle.apptpl.cmm.menu.service.MenuService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 메뉴관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/menu")
@Tag(name = "MenuController", description = "메뉴관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class MenuController {

    private final MenuService service;

    /**
     * 메뉴 목록 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "메뉴 목록 조회", description = "메뉴 목록 조회")
    public List<SYS1010002Dto> selectMenuList(@RequestBody SYS1010001Dto dto) {
        return service.selectMenuList(dto);
    }

    /**
     * 권한등급별 메뉴 조회 (상담사 콘솔 → admin, 서버 간 호출)
     */
    @GetMapping("/by-auth-level")
    @Operation(summary = "권한등급별 메뉴 조회", description = "권한등급/회원사유형별 메뉴 목록을 조회합니다")
    public List<MenuByAuthLevelResDto> selectMenuByAuthLevel(
            @RequestParam(required = false) String authLvl,
            @RequestParam(required = false) String cmpyTypeCd) {
        return service.selectMenuByAuthLevel(authLvl, cmpyTypeCd);
    }

    /**
     * 메뉴 저장 (등록/수정)
     */
    @PostMapping("/u01")
    @Operation(summary = "메뉴 저장", description = "메뉴 저장 (등록/수정)")
    public Integer updateMenuList(@RequestBody List<SYS1010002Dto> dtoList) {
        return service.updateMenuList(dtoList);
    }

    /**
     * 메뉴 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "메뉴 삭제", description = "메뉴 삭제")
    public Integer deleteMenuList(@RequestBody List<SYS1010002Dto> dtoList) {
        return service.deleteMenuList(dtoList);
    }
} 