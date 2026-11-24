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
import buttle.apptpl.cmm.ognz.dto.SYS0010001Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0010002Dto;
import buttle.apptpl.cmm.ognz.dto.SYS0010003Dto;
import buttle.apptpl.cmm.ognz.service.OrganizationService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.List;

/**
 * 조직관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/ognz")
@Tag(name = "OrganizationController", description = "조직관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class OrganizationController {

    private final OrganizationService service;

    /**
     * 조직 조회
     */
    @PostMapping("/r01")
    @Operation(summary = "조직 조회", description = "조직 조회")
    public List<SYS0010002Dto> selectOrgList(@RequestBody SYS0010001Dto dto) {
        return service.selectOrgList(dto);
    }

    /**
     * 조직 상세 정보 조회
     */
    @PostMapping("/r02")
    @Operation(summary = "조직 상세 정보 조회", description = "조직 상세 정보 조회")
    public SYS0010003Dto selectOrgDtlInfo(@RequestBody SYS0010001Dto dto) {
        return service.selectOrgDtlInfo(dto);
    }

    /**
     * 조직 저장
     */
    @PostMapping("/u01")
    @Operation(summary = "조직 저장", description = "조직 저장")
    public SYS0010001Dto updateOrg(@RequestBody SYS0010003Dto dto) {
        return service.updateOrg(dto);
    }

    /**
     * 조직 삭제
     */
    @PostMapping("/d01")
    @Operation(summary = "조직 삭제", description = "조직 삭제")
    public SYS0010001Dto deleteOrg(@RequestBody SYS0010001Dto dto) {
    	return service.deleteOrg(dto);
    }




}