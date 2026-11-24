package buttle.apptpl.cmm.cmpymenu.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.MenuListReqDto;
import buttle.apptpl.api.shared.dto.MenuListResDto;
import buttle.apptpl.api.shared.dto.MenuSaveReqDto;
import buttle.apptpl.api.shared.dto.MenuSaveResDto;
import buttle.apptpl.cmm.cmpymenu.service.CmpyMenuService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import jakarta.validation.Valid;
import java.util.List;

/**
 * BO404S 메뉴 관리 Controller
 *
 * <p>회원사 유형별 메뉴를 조회하고 저장합니다.</p>
 *
 * @since 2026-01-20
 * @author AICC 개발팀
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/cmpy-menu")
@Tag(name = "CmpyMenuController", description = "메뉴 관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class CmpyMenuController {

    private final CmpyMenuService service;

    /**
     * 메뉴 조회
     *
     * @param cmpyTypeCd 회원사 유형 코드
     * @return 메뉴 목록
     */
    @GetMapping("/menus")
    @Operation(summary = "메뉴 조회", description = "회원사 유형에 따른 메뉴 트리 구조를 조회합니다.")
    public List<MenuListResDto> getMenuList(
            @Parameter(description = "회원사 유형 코드", required = true, example = "PARTNER") 
            @RequestParam(value = "cmpyTypeCd", required = true) String cmpyTypeCd
    ) {
        log.info("메뉴 조회 요청 - cmpyTypeCd: {}", cmpyTypeCd);

        MenuListReqDto dto = new MenuListReqDto(cmpyTypeCd);

        return service.getMenuList(dto);
    }

    /**
     * 메뉴 저장
     *
     * @param dto 저장 요청 DTO
     * @return 저장 결과
     */
    @PostMapping("/menus")
    @Operation(summary = "메뉴 저장", description = "메뉴 정보를 저장합니다 (등록/수정 통합 처리).")
    public MenuSaveResDto saveMenu(
            @Valid @RequestBody MenuSaveReqDto dto
    ) {
        log.info("메뉴 저장 요청 - menus count: {}", 
                dto.getMenus() != null ? dto.getMenus().size() : 0);

        return service.saveMenu(dto);
    }
}
