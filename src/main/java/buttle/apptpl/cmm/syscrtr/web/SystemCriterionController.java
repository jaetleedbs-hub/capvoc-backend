package buttle.apptpl.cmm.syscrtr.web;

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
import buttle.apptpl.cmm.syscrtr.dto.SYS5010001Dto;
import buttle.apptpl.cmm.syscrtr.dto.SYS5010002Dto;
import buttle.apptpl.cmm.syscrtr.service.SystemCriterionService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/sys-crtr")
@Tag(name = "SystemCriterionController", description = "기준정보관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class SystemCriterionController {

    private final SystemCriterionService service;

    /**
     * 기준 정보 조회
     * @param dto 조회 조건
     * @return 기준 정보 목록
     */
    @PostMapping("/r01")
    @Operation(summary = "기준 정보 조회", description = "기준 정보 조회")
    public List<SYS5010002Dto> selectBaseInfoList(@RequestBody SYS5010001Dto dto) {
        return service.selectBaseInfoList(dto);
    }

    /**
     * 기준 정보 상세 조회
     * @param dto 조회 조건
     * @return 기준 정보 상세
     */
    @PostMapping("/r02")
    @Operation(summary = "기준 정보 상세 조회", description = "기준 정보 상세 조회")
    public SYS5010002Dto selectBaseInfoDetail(@RequestBody SYS5010001Dto dto) {
        return service.selectBaseInfoDetail(dto);
    }

    /**
     * 기준 정보 엑셀 출력
     * @param dto 조회 조건
     * @return 엑셀 데이터
     */
    @PostMapping("/e01")
    @Operation(summary = "기준 정보 엑셀 출력", description = "기준 정보 엑셀 출력")
    public SYS5010001Dto exportExcelBaseInfoList(@RequestBody SYS5010001Dto dto) {
        return service.exportExcelBaseInfoList(dto);
    }

    /**
     * 기준 정보 저장
     * @param dto 저장할 데이터
     * @return 저장 결과
     */
    @PostMapping("/u01")
    @Operation(summary = "기준 정보 저장", description = "기준 정보 저장")
    public SYS5010001Dto updateBaseInfo(@RequestBody SYS5010002Dto dto) {
        return service.updateBaseInfo(dto);
    }

    /**
     * 기준 정보 삭제
     * @param dto 삭제할 데이터
     * @return 삭제 결과
     */
    @PostMapping("/d01")
    @Operation(summary = "기준 정보 삭제", description = "기준 정보 삭제")
    public SYS5010001Dto deleteBaseInfo(@RequestBody SYS5010002Dto dto) {
        return service.deleteBaseInfo(dto);
    }
} 