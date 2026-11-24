package buttle.apptpl.cmm.saleday.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import buttle.apptpl.cmm.saleday.dto.SYS5030001Dto;
import buttle.apptpl.cmm.saleday.dto.SYS5030002Dto;
import buttle.apptpl.cmm.saleday.dto.SYS5030003Dto;
import buttle.apptpl.cmm.saleday.service.BusinessDayService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/sale-day")
@Tag(name = "BusinessDayController", description = "영업일관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class BusinessDayController {

    private final BusinessDayService service;

    @PostMapping("/r01")
    @Operation(summary = "영업일 조회", description = "영업일 조회")
    public Page<SYS5030002Dto> selectBusinessDayList(@RequestBody SYS5030001Dto dto, Pageable pageable) {
        return service.selectBusinessDayList(dto, pageable);
    }

    @PostMapping("/u01")
    @Operation(summary = "영업일 저장", description = "영업일 저장")
    public Integer updateBusinessDayList(@RequestBody List<SYS5030002Dto> dtoList) {
        return service.updateBusinessDayList(dtoList);
    }

    @PostMapping("/u02")
    @Operation(summary = "영업일 생성", description = "영업일 생성")
    public SYS5030003Dto createBusinessDay(@RequestBody SYS5030001Dto dto) {
        return service.createBusinessDay(dto);
    }
} 