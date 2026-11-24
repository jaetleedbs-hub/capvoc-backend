package buttle.apptpl.cmm.menu.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.cmm.menu.dto.SYS1020001Dto;
import buttle.apptpl.cmm.menu.dto.SYS1020002Dto;
import buttle.apptpl.cmm.menu.service.MenuSearchService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 메뉴찾기 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/menu/search")
@Tag(name = "MenuSearchController", description = "메뉴찾기")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class MenuSearchController {

    private final MenuSearchService service;

    /**
     * 메뉴 조회
     * @param dto 메뉴찾기 요청 DTO
     * @return 메뉴찾기 응답 DTO 리스트
     */
    @PostMapping("/r01")
    @Operation(summary = "메뉴 조회", description = "메뉴 조회")
    public List<SYS1020002Dto> selectMenuList(@RequestBody SYS1020001Dto dto) {
        return service.selectMenuList(dto);
    }
} 