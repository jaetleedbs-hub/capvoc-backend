package buttle.apptpl.common.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import buttle.apptpl.common.framework.common.vo.CustomUserDetails;
import buttle.apptpl.common.framework.common.vo.SecUser;
import micoai.corebase.util.CoreUtils.webutils;
import buttle.apptpl.sup.bookmark.service.BookmarkService;
import buttle.apptpl.api.common.components.dto.LoginUserDto;
import buttle.apptpl.api.common.components.service.CommonComponentService;
import buttle.apptpl.api.common.components.service.CommonUserAuthService;
import buttle.apptpl.cmm.syscrtr.dto.SYS5010001Dto;
import buttle.apptpl.cmm.syscrtr.service.SystemCriterionService;
import buttle.apptpl.common.jwt.JwtProvider;
import buttle.apptpl.common.jwt.JwtRepository;
import buttle.apptpl.common.jwt.TokenDto;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.security.bruteforce.LoginAttemptUtils;
import buttle.apptpl.common.security.vo.LoginSuccessVo;


// SecurityConfig.java에서 successHandler 설정
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler  {

    @Autowired
	private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtRepository jwtRepository;

    @Autowired
    private CommonUserAuthService commonUserAuthService;

    @Autowired
    private CommonComponentService commonComponentService;

    @Autowired
    private BookmarkService com2070Service;

    @Autowired
    private SystemCriterionService sys5010Service;
    

    @Autowired
    private LoginAttemptUtils loginAttemptUtils;

	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();

        // CmptUserEntity userEntity
    	CmptUserEntity userEntity = userDetails.getUserEntity();

        userEntity.setLgnIp(webutils.getRemoteIp(request));
        
        // 로그인 시에는 캐시에 저장된 사용자 정보를 사용하지 않도록 캐시를 먼저 삭제한다.
        commonUserAuthService.clearSessionUserCache(userEntity.getUserId());




        // 토큰 발급 전 사용자 정보 조회
        SecUser secUser = commonUserAuthService.getSecUser(userEntity.getUserId());

        userDetails.setSecUser(secUser);

        // JWT 토큰 발급 (새로운 Access Token과 Refresh Token 생성)
        TokenDto tokenDto = jwtProvider.generateToken(secUser);

        // JWT 토큰 발급
        jwtProvider.addTokenToResponse(response, tokenDto);

        // --- Refresh Token 복제 방지 로직 시작 ---
        jwtRepository.saveRefreshToken(tokenDto.getRefreshToken(), secUser); // 새로 발급된 Refresh Token 저장


        // 로그인 성공 처리 (로그인이력 등)
        loginAttemptUtils.loginSucceeded(userEntity);

        // 로그인 사용자 정보 조회
        LoginUserDto loginUserDto = commonUserAuthService.getLoginUserInfo(secUser);

        // 로그인 성공 응답
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
		LoginSuccessVo vo = LoginSuccessVo.builder()
				.user(loginUserDto)
                .codeList(commonComponentService.getCode("")) // 모든 코드 조회
				.redirectUrl("/")
				.menuList(commonComponentService.getUserAuthrtMenuList(secUser.getAuthrtCd(), secUser.getUserId()))
                .bookmarkList(com2070Service.selectBookmarkList())
                .frontendSysBaseInfo(getFrontendSysBaseInfoList())
				.build();
		objectMapper.writeValue(response.getWriter(), vo);
    }



    /**
     * 시스템 기준 정보 조회
     * @return 시스템 기준 정보 목록
     */
    private Map<String, String> getFrontendSysBaseInfoList() {
        SYS5010001Dto dto = SYS5010001Dto.builder()
            .sysCrtrTypeCd("FRONTEND")
            .build();
            
        Map<String, String> map = new HashMap<>();
        sys5010Service.selectBaseInfoList(dto).stream().forEach(item -> {
            map.put(item.getSysCrtrCd(), item.getSysCrtrCdVl());
        });
        return map;
    }


}
