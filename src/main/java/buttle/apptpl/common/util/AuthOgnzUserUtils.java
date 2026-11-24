package buttle.apptpl.common.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.util.SecurityUtils;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.exception.InvalidationException;
import buttle.apptpl.api.common.components.service.CommonComponentService;

@Slf4j
@Component
public class AuthOgnzUserUtils {
    
    private static CommonComponentService staticCommonComponentService;
    
    @Autowired
    private CommonComponentService commonComponentService;

    @PostConstruct
    public void init() {
        AuthOgnzUserUtils.staticCommonComponentService = this.commonComponentService;
        log.debug("AuthOrgzUserUtils 초기화 완료: {}", (staticCommonComponentService != null ? "성공" : "실패"));
    }

    private static final String OGNZ_INQ_RANG_CD_ALL = "01";
    private static final String OGNZ_INQ_RANG_CD_SUB = "02";
    private static final String OGNZ_INQ_RANG_CD_SELF = "03";

    private static final String MESSAGE_AUTH_OGNZ_CD = "권한이 없는 조직입니다.";
    private static final String MESSAGE_AUTH_USER_ID = "권한이 없는 사용자입니다.";


    public static String getAuthOgnzCd() {
        return getAuthOgnzCd("");
    }


    /**
     * 조직 조회 권한 체크
     * @param ognzCd
     * @return
     */
    public static String getAuthOgnzCd(String ognzCd) {

        // 사용자 세션이 없는 경우 전달받은 parameter 그대로 반환
        // anonymous 사용자 처리는 filter에서 처리하므로 여기서는 처리하지 않는다.
        SecUser secUser = SecurityUtils.getCurrentUser();
        if( secUser == null || !secUser.getEnabled() || StringUtils.isEmpty(secUser.getOgnzInqRangCd())) {
            return ognzCd;
        }
        
        String ognzInqRangCd = secUser.getOgnzInqRangCd();

        // 조직 조회 범위가 전체 조직 조회 가능한 경우
        if(OGNZ_INQ_RANG_CD_ALL.equals( ognzInqRangCd)) {
            return ognzCd;
        }

        if(OGNZ_INQ_RANG_CD_SELF.equals( ognzInqRangCd)) {
            if(StringUtils.isEmpty(ognzCd)) {
                return secUser.getOgnzCd();
            }

            if(!ognzCd.equals(secUser.getOgnzCd())) {
                throw new InvalidationException(MESSAGE_AUTH_OGNZ_CD);
            }
            return ognzCd;
        }

        // 조직 조회 범위가 하위 조직 조회 가능한 경우
        if(OGNZ_INQ_RANG_CD_SUB.equals( ognzInqRangCd)) {
            if(StringUtils.isEmpty(ognzCd)) {
                return secUser.getOgnzCd();
            }

            // 나의 조회 가능한 조직 계층 조회
            List<String> hierarchyOgnzList = staticCommonComponentService.getHierarchyOgnzList(secUser.getOgnzCd());

            // 나의 조회 가능한 조직 계층에 전달받은 조직코드가 포함되어 있는 경우
            if(hierarchyOgnzList.contains(ognzCd)) {
                return ognzCd;
            }

            throw new InvalidationException(MESSAGE_AUTH_OGNZ_CD);
        }

        throw new InvalidationException(MESSAGE_AUTH_OGNZ_CD);
    }

    public static String getAuthUserId() {
        return getAuthUserId("");
    }

    /**
     * 사용자 조회 권한 체크
     * @param userId 사용자 ID
     * @return 사용자 ID
     */
    public static String getAuthUserId(String userId) {

        // 사용자 세션이 없는 경우 전달받은 parameter 그대로 반환
        // anonymous 사용자 처리는 filter에서 처리하므로 여기서는 처리하지 않는다.
        SecUser secUser = SecurityUtils.getCurrentUser();
        if( secUser == null || !secUser.getEnabled() || StringUtils.isEmpty(secUser.getOgnzInqRangCd())) {
            return userId;
        }
        
        String ognzInqRangCd = secUser.getOgnzInqRangCd();

        // 조직 조회 범위가 전체 조직 조회 가능한 경우
        if(OGNZ_INQ_RANG_CD_ALL.equals( ognzInqRangCd)) {
            return userId;
        }

        if(OGNZ_INQ_RANG_CD_SELF.equals( ognzInqRangCd)) {
            if(StringUtils.isEmpty(userId)) {
                return secUser.getUserId();
            }

            if(!userId.equals(secUser.getUserId())) {
                throw new InvalidationException(MESSAGE_AUTH_USER_ID);
            }
            return userId;
        }

        // 조직 조회 범위가 하위 조직 조회 가능한 경우
        if(OGNZ_INQ_RANG_CD_SUB.equals( ognzInqRangCd)) {
            if(StringUtils.isEmpty(userId)) {
                return userId;
            }

            // 나의 조회 가능한 조직 계층 조회
            List<String> hierarchyUserList = staticCommonComponentService.getHierarchyUserIdList(secUser.getOgnzCd());

            // 나의 조회 가능한 조직 계층에 전달받은 조직코드가 포함되어 있는 경우
            if(hierarchyUserList.contains(userId)) {
                return userId;
            }

            throw new InvalidationException(MESSAGE_AUTH_USER_ID);
        }

        throw new InvalidationException(MESSAGE_AUTH_USER_ID);        
    }
}

