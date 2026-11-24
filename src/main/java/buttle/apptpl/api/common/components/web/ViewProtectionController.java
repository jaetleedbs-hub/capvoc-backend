package buttle.apptpl.api.common.components.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 뷰 이름과 충돌하는 URL 경로에 대한 직접 접근을 차단하는 컨트롤러
 * 
 * Spring MVC에서 뷰 이름(jsonView, error, redirect 등)과 동일한 URL로 
 * 직접 접근할 때 발생하는 순환 참조 오류를 방지합니다.
 */
@Controller
@Slf4j
public class ViewProtectionController {

    /**
     * jsonView 경로에 대한 직접 접근 차단
     * 
     * jsonView는 JSON 응답을 위한 뷰 이름으로 사용되며,
     * 직접 URL로 접근하면 순환 참조 오류가 발생합니다.
     */
    @RequestMapping(value = "/jsonView", method = {RequestMethod.GET, RequestMethod.POST})
    public void blockJsonViewAccess(HttpServletResponse response) {
        log.warn("Blocked direct access to /jsonView path to prevent circular view reference");
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }

    /**
     * redirect 경로에 대한 직접 접근 차단
     */
    @RequestMapping(value = "/redirect", method = {RequestMethod.GET, RequestMethod.POST})
    public void blockRedirectAccess(HttpServletResponse response) {
        log.warn("Blocked direct access to /redirect path to prevent circular view reference");
        response.setStatus(HttpStatus.NOT_FOUND.value());
    }
}
