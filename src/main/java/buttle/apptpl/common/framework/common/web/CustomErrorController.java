package buttle.apptpl.common.framework.common.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.exception.UnknownException;
import micoai.corebase.exception.ForbiddenException;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.exception.UnauthorizedException;
import micoai.corebase.util.CoreUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * "404 Not found" 등, Controller로 오지 않고, Spring에서 처리되는 오류를 출력하는 Controller
 *
 * @author patrick
 *
 */

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
	@RequestMapping("/error")
	public ResponseEntity<Object>  handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

		if (status != null) {
			int statusCode = Integer.valueOf(status.toString());
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				log.info("===============================================================");
				log.info("NOT FOUND: " + uri);
				log.info("===============================================================");
				if (CoreUtils.string.endsWith(uri, ".do")) {
					throw new InvalidationException("지원하지 않는 기능입니다.");
				}
				else {
					return new ResponseEntity<Object>("Not found", HttpStatus.NOT_FOUND);
				}
			}
			else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				throw new ForbiddenException();
			}
			else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
				throw new UnauthorizedException();
			}
			else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
				throw new InvalidationException("지원하지 않는 기능입니다.(Http-Method)");
			}
		}
		throw new UnknownException();
	}
}
