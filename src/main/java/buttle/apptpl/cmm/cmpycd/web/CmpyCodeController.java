package buttle.apptpl.cmm.cmpycd.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.api.shared.dto.*;
import buttle.apptpl.cmm.cmpycd.service.CmpyCodeService;

import java.util.List;

/**
 * BO403_S 회원사 공통 코드 관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/cmpy-cd")
@Tag(name = "CmpyCodeController", description = "회원사 공통 코드 관리")
public class CmpyCodeController {

    private final CmpyCodeService service;

    @GetMapping("/codes/groups")
    @Operation(summary = "코드그룹조회")
    public List<CodeGroupListResDto> getCodeGroupList(
            @Parameter(name = "cmpyTypeCd", description = "회원사유형코드", example = "CMPY01")
            @RequestParam(name = "cmpyTypeCd", required = true) String cmpyTypeCd,
            @Parameter(name = "cdGroup", description = "그룹코드", example = "CMPY01")
            @RequestParam(name = "cdGroup", required = false)  String cdGroup) {
        
        CodeGroupSearchReqDto dto = CodeGroupSearchReqDto.builder()
                .cmpyTypeCd(cmpyTypeCd)
                .cdGroup(cdGroup)
                .build();
        
        return service.getCodeGroupList(dto);
    }

    @GetMapping("/codes")
    @Operation(summary = "코드조회")
    public List<CodeListResDto> getCodeList(
        @Parameter(name = "cmpyTypeCd", description = "회원사유형코드", example = "CMPY01")
        @RequestParam(name = "cmpyTypeCd", required = true) String cmpyTypeCd,
            @Parameter(name = "cdGroup", description = "그룹코드", example = "CMPY01")
            @RequestParam(name = "cdGroup", required = false)  String cdGroup
        ) {
        
        CodeSearchReqDto dto = CodeSearchReqDto.builder()
                .cmpyTypeCd(cmpyTypeCd)
                .cdGroup(cdGroup)
                .build();
        
        return service.getCodeList(dto);
    }

    @PostMapping("/codes/groups")
    @Operation(summary = "코드그룹저장")
    public CodeCommonSaveResDto saveCodeGroup(@RequestBody CodeGroupSaveReqDto dto) {
        return service.saveCodeGroup(dto);
    }

    @PostMapping("/codes")
    @Operation(summary = "코드저장")
    public CodeCommonSaveResDto saveCode(@RequestBody CodeSaveReqDto dto) {
        return service.saveCode(dto);
    }
}
