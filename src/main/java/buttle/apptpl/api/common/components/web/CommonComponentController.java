package buttle.apptpl.api.common.components.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.api.shared.dto.AuthorityListResDto;
import buttle.apptpl.api.shared.dto.CompanyListResDto;
import buttle.apptpl.api.common.components.dto.CommCdDto;
import buttle.apptpl.api.common.components.dto.OgnzDto;
import buttle.apptpl.api.common.components.dto.UserDto;
import buttle.apptpl.api.common.components.service.CommonComponentService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/common-component")
@Tag(name = "CommonUserController", description = "공통 콤포넌트 사용자 ")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class CommonComponentController {

    private final CommonComponentService commonComponentService;

    @GetMapping("/user")
    @Operation(summary = "사용자 조회", description = "사용자 ID 또는 이름으로 조회")
    @Parameter(name = "keyword", description = "사용자 ID 또는 이름")
    public List<UserDto> getUser(@RequestParam(required = false, name = "keyword" ) String keyword,
    @RequestParam(required = false, name = "authrt") String authrt){
        return commonComponentService.getUser(keyword, authrt);
    }

    @GetMapping("/organization")
    @Operation(summary = "부서 조회", description = "사용자가 소속된 부서 또는 지정된 조회")
    @Parameter(name = "keyword", description = "부서코드")
    public List<OgnzDto> getOrganization(@RequestParam(required = false, name = "keyword") String keyword
    , @RequestParam(required = false, name = "authrt") String authrt){
        // 전달된 부서코드가 없을 경우 사용자 세션에서 사용자 부서를 조회하여 반환한다.
        return commonComponentService.getOrganization(keyword, authrt);
    }

    @GetMapping("/code")
    @Operation(summary = "공통코드 조회", description = "공통코드 조회")
    @Parameter(name = "codeGroup", description = "코드그룹")
    public List<Map<String, Object>> getCode(@RequestParam(required = true, name = "codeGroup") String codeGroup){
        // 공통코드
        return commonComponentService.getCode(codeGroup);
    }

    @GetMapping("/company-code-list")
    @Operation(summary = "회원사별 코드목록 조회", description = "회원사 유형과 코드그룹으로 회원사별 코드목록 조회")
    @Parameter(name = "cmpyTypeCd", description = "회원사 유형", required = true)
    @Parameter(name = "cdGroup", description = "코드그룹", required = true)
    public List<CommCdDto> getCompanyCode(@RequestParam(required = true, name = "cmpyTypeCd") String cmpyTypeCd,
                                          @RequestParam(required = true, name = "cdGroup") String cdGroup){
        return commonComponentService.getCompanyCodeList(cmpyTypeCd, cdGroup);
    }

    @GetMapping("/companies")
    @Operation(summary = "회원사 목록 조회", description = "Selectbox용 회원사 목록 조회")
    public List<CompanyListResDto> getCompanyList() {
        log.info("회원사 목록 조회 (Selectbox용)");
        return commonComponentService.getCompanyList();
    }

    @GetMapping("/authorities")
    @Operation(summary = "권한 목록 조회", description = "Selectbox용 권한 목록 조회")
    public List<AuthorityListResDto> getAuthorityList() {
        log.info("권한 목록 조회 (Selectbox용)");
        return commonComponentService.getAuthorityList();
    }

}
