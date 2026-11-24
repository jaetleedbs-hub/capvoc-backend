package buttle.apptpl.cmm.ognz.web;

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
import buttle.apptpl.cmm.ognz.dto.SYS0020001Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0020002Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0020003Dto;
import buttle.apptpl.cmm.ognz.service.OrganizationSearchService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 조직찾기 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/ognz/search")
@Tag(name = "OrganizationSearchController", description = "조직찾기")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class OrganizationSearchController {

    private final OrganizationSearchService service;

    /**
     * 조직 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "조직 조회", description = "조직 조회")
    public List<SYS0020002Dto> selectOrgList(@RequestBody SYS0020001Dto dto) {
        return service.selectOrgList(dto);
    }

}