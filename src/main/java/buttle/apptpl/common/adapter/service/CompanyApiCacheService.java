package buttle.apptpl.common.adapter.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import buttle.apptpl.common.adapter.dto.CompanyApiInfo;
import buttle.apptpl.common.adapter.mapper.CompanyApiMapper;

/**
 * 회원사 API 정보 캐시 서비스
 * 
 * <p>회원사 API 정보를 캐시에서 조회하고 관리합니다.</p>
 * <ul>
 *   <li>Caffeine Cache 활용</li>
 *   <li>DB 조회 최소화</li>
 *   <li>캐시 갱신 기능 제공</li>
 * </ul>
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyApiCacheService {
    
    private final CompanyApiMapper companyApiMapper;
    private final Cache<String, CompanyApiInfo> companyApiCache;
    
    /**
     * 회원사 API 정보 조회 (캐시 우선)
     * 
     * @param companyId 회원사ID
     * @return 회원사 API 정보 (없으면 null)
     */
    public CompanyApiInfo getCompanyApiInfo(String companyId) {
        if (companyId == null) {
            return null;
        }
        
        try {
            // 1. 캐시에서 조회
            CompanyApiInfo cachedInfo = companyApiCache.getIfPresent(companyId);
            if (cachedInfo != null) {
                log.debug("캐시에서 회원사 API 정보 조회 - companyId: {}", companyId);
                return cachedInfo;
            }
            
            // 2. DB에서 조회
            log.debug("DB에서 회원사 API 정보 조회 - companyId: {}", companyId);
            CompanyApiInfo apiInfo = companyApiMapper.selectCompanyApiInfo(companyId);
            
            // 3. 캐시에 저장
            if (apiInfo != null) {
                companyApiCache.put(companyId, apiInfo);
                log.debug("회원사 API 정보 캐시 저장 - companyId: {}", companyId);
            }
            
            return apiInfo;
            
        } catch (Exception e) {
            log.error("회원사 API 정보 조회 실패 - companyId: {}", companyId, e);
            return null;
        }
    }
    
    /**
     * 회원사 API 정보 캐시 갱신
     * 
     * @param companyId 회원사ID
     */
    public void refreshCache(String companyId) {
        log.info("회원사 API 정보 캐시 갱신 - companyId: {}", companyId);
        
        try {
            // 1. 캐시에서 제거
            companyApiCache.invalidate(companyId);
            
            // 2. DB에서 재조회 및 캐시 저장
            CompanyApiInfo apiInfo = companyApiMapper.selectCompanyApiInfo(companyId);
            if (apiInfo != null) {
                companyApiCache.put(companyId, apiInfo);
                log.info("회원사 API 정보 캐시 갱신 완료 - companyId: {}", companyId);
            } else {
                log.warn("회원사 API 정보가 존재하지 않음 - companyId: {}", companyId);
            }
            
        } catch (Exception e) {
            log.error("회원사 API 정보 캐시 갱신 실패 - companyId: {}", companyId, e);
        }
    }
    
    /**
     * 전체 캐시 초기화
     */
    public void clearAllCache() {
        log.info("전체 회원사 API 정보 캐시 초기화");
        companyApiCache.invalidateAll();
    }
}
