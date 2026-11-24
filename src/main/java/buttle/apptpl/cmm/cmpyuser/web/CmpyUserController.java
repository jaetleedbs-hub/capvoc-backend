package buttle.apptpl.cmm.cmpyuser.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import buttle.apptpl.api.shared.dto.UserSearchReqDto;
import buttle.apptpl.api.shared.dto.UserListResDto;
import buttle.apptpl.api.shared.dto.UserDetailResDto;
import buttle.apptpl.api.shared.dto.UserSaveReqDto;
import buttle.apptpl.api.shared.dto.AuthrtResDto;
import buttle.apptpl.cmm.cmpyuser.service.CmpyUserService;
import buttle.apptpl.common.exception.vo.ErrorResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BO401_S 사용자 관리 Controller
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cmm/cmpy-user")
@Tag(name = "CmpyUserController", description = "사용자 관리")
@ApiResponse(responseCode = "200", description = "성공")
@ApiResponse(responseCode = "400", description = "실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class CmpyUserController {
    
    private final CmpyUserService service;
    
    /**
     * 사용자 목록 조회
     */
    @GetMapping("/users")
    @Operation(summary = "사용자 목록 조회", description = "검색 조건에 따른 사용자 목록을 조회합니다")
    public Page<UserListResDto> getUserList(@ModelAttribute UserSearchReqDto dto, @ParameterObject Pageable pageable) {
        return service.getUserList(dto, pageable);
    }
    
    /**
     * 사용자 상세 조회
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "사용자 상세 조회", description = "선택된 사용자의 상세 정보를 조회합니다")
    public UserDetailResDto getUserDetail(@PathVariable("id") String userId) {
        return service.getUserDetail(userId);
    }
    
    /**
     * 사용자 정보 저장 (신규 등록 또는 수정)
     */
    @PostMapping("/users/save")
    @Operation(summary = "사용자 정보 저장", description = "사용자 정보를 신규 등록하거나 수정합니다")
    public ResponseEntity<Map<String, Object>> saveUser(@RequestBody UserSaveReqDto dto) {
        int result = service.saveUser(dto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("message", "저장 완료되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그인 정보 초기화
     */
    @PostMapping("/users/login/reset")
    @Operation(summary = "로그인 정보 초기화", description = "사용자의 로그인 상태 및 시도 횟수를 초기화합니다")
    public ResponseEntity<Map<String, Object>> resetUserLogin(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        int result = service.resetUserLogin(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("message", "로그인 초기화가 완료되었습니다.");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 비밀번호 초기화
     */
    @PostMapping("/users/password/reset")
    @Operation(summary = "비밀번호 초기화", description = "사용자의 비밀번호를 임시 비밀번호로 초기화합니다")
    public ResponseEntity<Map<String, Object>> resetUserPassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String tempPassword = service.resetUserPassword(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("tempPassword", tempPassword);
        response.put("message", "비밀번호가 초기화되었습니다. 임시 비밀번호: " + tempPassword);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 권한(사용자등급) 목록 조회
     */
    @GetMapping("/authrt")
    @Operation(summary = "권한 목록 조회", description = "사용자등급(권한) 목록을 조회합니다")
    public List<AuthrtResDto> getAuthrtList() {
        return service.getAuthrtList();
    }
}
