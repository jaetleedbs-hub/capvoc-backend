package buttle.apptpl.cmm.cd.web;

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
import buttle.apptpl.cmm.cd.dto.SYS5040001Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040002Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040003Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040004Dto;
import buttle.apptpl.cmm.cd.dto.SYS5040005Dto;
import buttle.apptpl.cmm.cd.service.CodeService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/cd")
@Tag(name = "CodeController", description = "시스템코드관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class CodeController {

    private final CodeService sys5040Service;

    @PostMapping("/r01")
    @Operation(summary = "시스템 대분류 코드 조회", description = "시스템 대분류 코드를 조회합니다.")
    public List<SYS5040002Dto> selectSysLCatCdList(@RequestBody SYS5040001Dto dto) {
        return sys5040Service.selectSysLCatCdList(dto);
    }

    @PostMapping("/r02")
    @Operation(summary = "시스템 대분류 코드 상세 조회", description = "시스템 대분류 코드 상세를 조회합니다.")
    public SYS5040002Dto selectSysLCatCdDtl(@RequestBody SYS5040001Dto dto) {
        return sys5040Service.selectSysLCatCdDtl(dto);
    }

    @PostMapping("/r03")
    @Operation(summary = "시스템 소분류 코드 조회", description = "시스템 소분류 코드를 조회합니다.")
    public List<SYS5040004Dto> selectSysSCatCdList(@RequestBody SYS5040003Dto dto) {
        return sys5040Service.selectSysSCatCdList(dto);
    }

    @PostMapping("/r04")
    @Operation(summary = "시스템 소분류 코드 상세 조회", description = "시스템 소분류 코드 상세를 조회합니다.")
    public SYS5040004Dto selectSysSCatCdDtl(@RequestBody SYS5040003Dto dto) {
        return sys5040Service.selectSysSCatCdDtl(dto);
    }
    
    @PostMapping("/r05")
    @Operation(summary = "시스템 소분류 코드 참고 정보 조회", description = "시스템 소분류 코드의 참고 정보를 조회합니다.")
    public List<SYS5040005Dto> selectSysSCatCdRefList(@RequestBody SYS5040003Dto dto) {
        return sys5040Service.selectSysSCatCdRefList(dto);
    }

    @PostMapping("/u01")
    @Operation(summary = "시스템 대분류 코드 저장", description = "시스템 대분류 코드를 저장합니다.")
    public Integer updateSysLCatCd(@RequestBody SYS5040002Dto dto) {
        return sys5040Service.updateSysLCatCd(dto);
    }

    @PostMapping("/u02")
    @Operation(summary = "시스템 소분류 코드 저장", description = "시스템 소분류 코드를 저장합니다.")
    public Integer updateSysSCatCd(@RequestBody SYS5040004Dto dto) {
        return sys5040Service.updateSysSCatCd(dto);
    }
    
    @PostMapping("/u03")
    @Operation(summary = "시스템 소분류 코드 참고 정보 저장", description = "시스템 소분류 코드의 참고 정보를 저장합니다.")
    public Integer updateSysSCatCdRefInfoList(@RequestBody List<SYS5040005Dto> dtoList) {
        return sys5040Service.updateSysSCatCdRefList(dtoList);
    }
} 