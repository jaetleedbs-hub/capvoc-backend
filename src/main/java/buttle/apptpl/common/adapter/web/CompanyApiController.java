package buttle.apptpl.common.adapter.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import buttle.apptpl.common.adapter.dto.CompanyApiInfo;
import buttle.apptpl.common.adapter.service.CompanyApiCacheService;

/**
 * 회원사 API 관리 Controller
 * 
 * <p>회원사 API 정보 조회 및 캐시 관리 API를 제공합니다.</p>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/company-api")
@RequiredArgsConstructor
public class CompanyApiController {
    
    private final CompanyApiCacheService companyApiCacheService;
    
    /**
     * 회원사 API 정보 조회
     * 
     * @param companyId 회원사ID
     * @return 회원사 API 정보
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyApiInfo> getCompanyApiInfo(@PathVariable String companyId) {
        log.info("회원사 API 정보 조회 요청 - companyId: {}", companyId);
        
        CompanyApiInfo apiInfo = companyApiCacheService.getCompanyApiInfo(companyId);
        
        if (apiInfo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(apiInfo);
    }
    
    /**
     * 회원사 API 정보 캐시 갱신
     * 
     * @param companyId 회원사ID
     * @return 성공 메시지
     */
    @PostMapping("/{companyId}/refresh-cache")
    public ResponseEntity<String> refreshCache(@PathVariable String companyId) {
        log.info("회원사 API 정보 캐시 갱신 요청 - companyId: {}", companyId);
        
        companyApiCacheService.refreshCache(companyId);
        
        return ResponseEntity.ok("캐시 갱신 완료");
    }
    
    /**
     * 전체 캐시 초기화
     * 
     * @return 성공 메시지
     */
    @PostMapping("/clear-all-cache")
    public ResponseEntity<String> clearAllCache() {
        log.info("전체 회원사 API 정보 캐시 초기화 요청");
        
        companyApiCacheService.clearAllCache();
        
        return ResponseEntity.ok("전체 캐시 초기화 완료");
    }
}
