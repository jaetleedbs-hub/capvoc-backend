package buttle.apptpl.api.common.components.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.i18n.LocaleSupport;
import buttle.apptpl.api.shared.dto.AuthorityListResDto;
import buttle.apptpl.api.shared.dto.CompanyListResDto;
import buttle.apptpl.api.shared.vo.BO402SAuthorityListResVo;
import buttle.apptpl.api.shared.vo.BO402SCompanyListResVo;
import buttle.apptpl.api.common.components.dto.CommCdDto;
import buttle.apptpl.api.common.components.dto.CurrentDatetimeDto;
import buttle.apptpl.api.common.components.dto.OgnzDto;
import buttle.apptpl.api.common.components.dto.UserDto;
import buttle.apptpl.api.common.components.mapper.CommonComponentMapper;
import buttle.apptpl.cmm.menu.vo.SYS1020002Vo;
import buttle.apptpl.common.config.caffeine.CacheType;
import buttle.apptpl.common.util.AuthOgnzUserUtils;
import buttle.apptpl.common.util.ModelConveter;

@Slf4j
@Service
@Component
@Transactional(readOnly = true)
public class CommonComponentService {

    @Autowired
    private CommonComponentMapper commonComponentMapper;

    @Autowired
    private ModelConveter modelConveter;

    @Autowired
    private LocaleSupport localeSupport;

    private final CacheManager cacheManager;
    private final Cache codeCache;
    private final Cache menuCache;
    private final Cache prgrmCache;

    public CommonComponentService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.codeCache = cacheManager.getCache(CacheType.CODE_INFO.getCacheNameWithCompany());
        this.menuCache = cacheManager.getCache(CacheType.MENU_AUTHRT_INFO.getCacheNameWithCompany());
        this.prgrmCache = cacheManager.getCache(CacheType.PRGRM_AUTHRT_INFO.getCacheNameWithCompany());
    }

    /**
     * 사용자 조회
     */
    
    public List<UserDto> getUser(String keyword, String authrt) {

        String userId = null;
        String authOgnzCd = null;
        // 권한 체크가 필요한 사용자 조회 시 사용자의 권한 코드를 확인하여 설정
        if(!"false".equals(authrt)) {
            authOgnzCd = AuthOgnzUserUtils.getAuthOgnzCd();
            userId = AuthOgnzUserUtils.getAuthUserId();
        }

        List<UserDto> userDtoList = commonComponentMapper.selectUserList(keyword, authOgnzCd, userId);
        return userDtoList;
    }

    /**
     * 부서 조회
     */
    
    public List<OgnzDto> getOrganization(String keyword, String authrt) {

        String authOgnzCd = null;
        // 권한 체크가 필요한 부서 조회 시 사용자의 권한 코드를 확인하여 설정
        if(!"false".equals(authrt)) {
            authOgnzCd = AuthOgnzUserUtils.getAuthOgnzCd();
        }

        List<OgnzDto> orgzDtoList = commonComponentMapper.selectOrganization(keyword, authOgnzCd);
        return orgzDtoList;
    }

    /**
     * 공통코드 조회 (참조코드 포함, 동일 레벨로 평탄화)
     */
    
    public List<Map<String, Object>> getCode(String codeGroup) {
        String locale = localeSupport.current();
        // ★ 캐시 키에 언어를 넣는다. 안 넣으면 한 사람의 언어로 만든 결과가 다른 언어 사용자에게 나간다.
        String cacheKey = locale + "|" + (codeGroup != null ? codeGroup : "ALL_CODES");
        if (codeCache.get(cacheKey) != null) {
            return codeCache.get(cacheKey, List.class);
        }

        List<CommCdDto> codeList = commonComponentMapper.selectCodeList(codeGroup, locale);

        // 참조코드 조회 후 cdGroup|cd 기준 그룹핑
        List<Map<String, String>> refList = commonComponentMapper.selectCodeRefList(codeGroup, locale);
        Map<String, Map<String, String>> refMap = new HashMap<>();
        if (refList != null) {
            for (Map<String, String> ref : refList) {
                String key = ref.get("cdGroup") + "|" + ref.get("cd");
                refMap.computeIfAbsent(key, k -> new HashMap<>())
                      .put(ref.get("rfrncCd"), ref.get("rfrncVl"));
            }
        }

        // 코드 + 참조코드를 동일 레벨로 merge (기존 key 중복 시 무시)
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (CommCdDto code : codeList) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("value", code.getValue());
            map.put("name", code.getName());
            map.put("cdGroup", code.getCdGroup());
            map.put("sortSn", code.getSortSn());

            String key = code.getCdGroup() + "|" + code.getValue();
            Map<String, String> refs = refMap.get(key);
            if (refs != null) {
                for (Map.Entry<String, String> entry : refs.entrySet()) {
                    map.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
            resultList.add(map);
        }

        if (!resultList.isEmpty()) {
            codeCache.put(cacheKey, resultList);
        }
        return resultList;
    }

    /**
     * 회원사별 코드목록 조회
     */
    
    public List<CommCdDto> getCompanyCodeList(String cmpyTypeCd, String cdGroup) {
        String cacheKey = cmpyTypeCd + "_" + cdGroup;
        if (codeCache.get(cacheKey) != null) {
            return codeCache.get(cacheKey, List.class);
        }

        List<CommCdDto> codeList = commonComponentMapper.selectCompanyCodeList(cmpyTypeCd, cdGroup);
        if (!codeList.isEmpty()) {
            codeCache.put(cacheKey, codeList);
        }
        return codeList;
    }




    
    public CurrentDatetimeDto getCurrentDatetime() {
        return commonComponentMapper.selectCurrentDatetime();
    }


    /**
     * 권한 코드에 따른 메뉴 목록 조회
     */
    
    public List<SYS1020002Vo> getUserAuthrtMenuList(String authrtCd, String userId) {

        List<SYS1020002Vo> menuList = commonComponentMapper.selectUserAuthrtMenuList(authrtCd);
        if( menuList.isEmpty()) {
            return Collections.emptyList();
        }

        // 확장 메뉴 추가 (중복 확인 후 추가)
        List<SYS1020002Vo> expansionMenuList = commonComponentMapper.selectUserExpansionMenuList(userId);
        if(!expansionMenuList.isEmpty()) {
            for(SYS1020002Vo expansionMenu : expansionMenuList) {
                // 이미 존재하는 메뉴인지 확인 후 추가
                boolean exists = menuList.stream().anyMatch(menu -> 
                    menu.getMenuId().equals(expansionMenu.getMenuId())
                );
                if(!exists) {
                    menuList.add(expansionMenu);
                }
            }
        }

        // 제외 메뉴 제거
        List<SYS1020002Vo> exceptMenuList = commonComponentMapper.selectUserExceptMenuList(userId);
        if(!exceptMenuList.isEmpty()) {
            menuList.removeIf(menu -> {
                return exceptMenuList.stream().anyMatch( exceptMenu->{
                    return menu.getMenuId().equals(exceptMenu.getMenuId());
                });
            });
        }
        return menuList;
    }


    /**
     * 권한 코드에 따른 프로그램 목록 조회
     */
    
    public List<String> getAuthrtPrgrmList(String authrtCd) {
        if( StringUtils.isEmpty(authrtCd)) {
            return Collections.emptyList();
        }

        String cacheKey = authrtCd;
        if (prgrmCache.get(cacheKey) != null) {
            return prgrmCache.get(cacheKey, List.class);
        }

        List<String> prgrmList = commonComponentMapper.selectUserAuthrtPrgrmList(authrtCd);
        prgrmCache.put(cacheKey, prgrmList);

        return prgrmList;
    }


    /**
     * 사용자 코드에 따른 프로그램 권한 목록 조회
     * @param userId
     * @return
     */
    
    public List<String> getUserExpansionPrgrmList(String userId) {

        String cacheKey = userId + "_expansion";
        if (prgrmCache.get(cacheKey) != null) {
            return prgrmCache.get(cacheKey, List.class);
        }

        List<String> prgrmList = commonComponentMapper.selectUserExpansionPrgrmList(userId);

        // 확장 프로그램 목록 캐시 저장
        prgrmCache.put(cacheKey, prgrmList);
        return prgrmList;
    }
    

    /**
     * 사용자 코드에 따른 프로그램 권한 목록 조회
     * @param userId
     * @return
     */
    
    public List<String> getUserExceptPrgrmList(String userId) {
        String cacheKey = userId + "_except";
        if (prgrmCache.get(cacheKey) != null) {
            return prgrmCache.get(cacheKey, List.class);
        }

        List<String> prgrmList = commonComponentMapper.selectUserExceptPrgrmList(userId);

        // 확장 프로그램 목록 캐시 저장
        prgrmCache.put(cacheKey, prgrmList);
        return prgrmList;
    }
    

    /**
     * 공통 프로그램 목록 조회
     */
    public List<String> getCommonPrgrmList() {

        String cacheKey = "common_prgrm";
        if (prgrmCache.get(cacheKey) != null) {
            return prgrmCache.get(cacheKey, List.class);
        }

        List<String> prgrmList = commonComponentMapper.selectCommonPrgrmList();
        prgrmCache.put(cacheKey, prgrmList);

        return prgrmList;
    }




    /**
     * 조직 계층 조회
     */
    public List<String> getHierarchyOgnzList(String ognzCd) {
        return commonComponentMapper.selectUserAuthrtOgnz(ognzCd);
    }


    /**
     * 조직 계층 사용자 목록 조회
     */
    public List<String> getHierarchyUserIdList(String ognzCd) {
        return commonComponentMapper.selectUserAuthrtUserId(ognzCd);
    }

    /**
     * 회원사 목록 조회 (Selectbox용)
     * 
     * @return 회원사 목록 (value, name)
     */
    public List<CompanyListResDto> getCompanyList() {
        log.info("회원사 목록 조회 (Selectbox용)");
        return modelConveter.mapList(commonComponentMapper.selectCompanyList(), CompanyListResDto.class);
    }

    /**
     * 권한 목록 조회 (Selectbox용)
     * 
     * @return 권한 목록 (value, name)
     */
    public List<AuthorityListResDto> getAuthorityList() {
        log.info("권한 목록 조회 (Selectbox용)");
        return modelConveter.mapList(commonComponentMapper.selectAuthorityList(), AuthorityListResDto.class);
    }

}
