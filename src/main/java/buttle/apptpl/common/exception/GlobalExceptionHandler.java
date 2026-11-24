package buttle.apptpl.common.exception;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.config.EnvConfig;
import buttle.apptpl.common.framework.exception.UnknownException;
import micoai.corebase.exception.CommunicationException;
import micoai.corebase.exception.DoubleLoginException;
import micoai.corebase.exception.ExceptionMessage;
import micoai.corebase.exception.ForbiddenException;
import micoai.corebase.exception.InvalidationException;
import micoai.corebase.exception.InvalidationsException;
import micoai.corebase.exception.LoggableException;
import micoai.corebase.exception.UnauthorizedException;
import micoai.corebase.util.CoreUtils;
import buttle.apptpl.common.adapter.exception.CompanyApiException;
import buttle.apptpl.common.adapter.exception.CompanyServerNotAssignedException;
import buttle.apptpl.common.exception.vo.ErrorResponse;
import buttle.apptpl.common.exception.vo.ErrorResponseItem;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final String JSON_VIEW_NAME = "jsonView";

	/**
	 * 장애 장부. <b>이 핸들러가 admin 의 유일한 writer 다.</b>
	 *
	 * <p>2026-09-10 이전에는 writer 가 아예 없어 {@code logt_error_log} 가
	 * <b>0행</b>이었다 — 표는 있는데 쓰는 사람이 없었다. 접속로그는 4,206행으로 돌고 있었으니
	 * 「로그를 안 남기는 앱」이 아니라 <b>「오류만 안 남기는 앱」</b>이었다.
	 *
	 * <p>{@code @Autowired} 필드 주입을 쓴다 — 이 클래스의 {@code config} 와 같은 방식이다.
	 * 생성자를 새로 들이면 이 파일의 관례가 둘이 된다.
	 */
	@Autowired
	private micoai.corebase.failure.FailureRecorder failureRecorder;

	private static final Class<?>[] EXCEPT_THROWABLES = {
			BadCredentialsException.class,
			ClientAbortException.class,
			DoubleLoginException.class,
			ForbiddenException.class,
			HttpRequestMethodNotSupportedException.class,
	        InvalidationException.class,
	        InvalidationsException.class,
	        SocketException.class,
	        UnauthorizedException.class,
	        UnknownException.class
	    };

	@Autowired
	private EnvConfig config;

	private ModelAndView sendError(ErrorResponse error, HttpStatusCode status, HttpServletRequest request) {
		boolean ajax = CoreUtils.webutils.isAjax(request);

		ModelAndView mv = new ModelAndView();
		mv.setViewName(JSON_VIEW_NAME);
		mv.addObject(error);

		mv.setStatus(status);
		return mv;
	}

	@ExceptionHandler(LoggableException.class)
	public ModelAndView handleLoggableException(LoggableException e, HttpServletRequest request) {
		final ErrorResponse error = ErrorResponse.builder()
				.error("Exception")
				.status(e.getExceptionMessage().getStatus())
				.message(e.getMessage())
				.errors(new ArrayList<ErrorResponseItem>())
				.build();

		return sendError(error, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(InvalidationException.class)
	public ModelAndView handleInvalidationException(InvalidationException e, HttpServletRequest request) {
		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(e.getExceptionMessage().getStatus())
				.message(e.getMessage())
				.errors(new ArrayList<ErrorResponseItem>())
				.build();

		return sendError(error, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(InvalidationsException.class)
	public ModelAndView handleInvalidationsException(InvalidationsException e, HttpServletRequest request) {

		List<ErrorResponseItem> list = new ArrayList<>();
		for (ExceptionMessage msg : e.getExceptionMessages()) {
			ErrorResponseItem item = ErrorResponseItem.builder()
					.field(msg.getField())
					.message(msg.getMessage())
					.build();
			list.add(item);
		}

		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.BAD_REQUEST.value())
				.message("errors")
				.errors(list)
				.build();

		return sendError(error, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(CommunicationException.class)
	public ModelAndView handleBatchException(CommunicationException e, HttpServletRequest request) {

		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.BAD_REQUEST.value())
				.message(e.getMessage())
				.errors(new ArrayList<ErrorResponseItem>())
				.build();

		return sendError(error, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ModelAndView handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.NOT_FOUND.value())
				.message("지원하지 않는 기능입니다.")
				.errors(new ArrayList<ErrorResponseItem>())
				.build();

		return sendError(error, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ModelAndView handleAuthenticationException(UnauthorizedException e, HttpServletRequest request) {

		final ErrorResponse error = ErrorResponse.builder()
				.error("Unauthorized")
				.status(HttpStatus.UNAUTHORIZED.value())
				.message("로그인을 하세요.")
				.errors(new ArrayList<>())
				.build();

		return sendError(error, HttpStatus.UNAUTHORIZED, request);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ModelAndView handleForbiddenException(ForbiddenException e, HttpServletRequest request) {

		final ErrorResponse error = ErrorResponse.builder()
				.error("Forbidden")
				.status(HttpStatus.FORBIDDEN.value())
				.message("권한이 없습니다.")
				.errors(new ArrayList<>())
				.build();

		return sendError(error, HttpStatus.FORBIDDEN, request);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ModelAndView handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {

		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.UNAUTHORIZED.value())
				.message("아이디 또는 비밀번호가 올바르지 않습니다.")
				.errors(new ArrayList<>())
				.build();

		return sendError(error, HttpStatus.UNAUTHORIZED, request);
	}

	/**
	 * 회원사 API 호출 예외 처리
	 */
	/**
	 * 회원사에 desk 서버가 <b>지정되지 않은</b> 경우 — 오류가 아니다.
	 *
	 * <p>Gateway 연계 전용 회원사는 desk 배포 대상이 아니라 {@code desk_svr_id} 가 비는 것이
	 * 정상이다. 종전에는 이것이 502 <i>"회원사 API 서버에 연결할 수 없습니다"</i> 로 나가
	 * <b>정상 상태가 장애로 보였고</b>, 그 모달의 오버레이가 저장 버튼까지 막았다.
	 *
	 * <p><b>200 + 빈 본문</b>으로 답한다. "desk 쪽에 그 정보가 없다"가 참이고,
	 * 화면은 이미 <i>미등록 = null</i> 을 다룰 줄 안다({@code BO202_S.loadBaseInfo} 의
	 * 저장 가드가 그 전제로 쓰여 있다). {@code {}} 가 아니라 <b>빈 본문</b>이어야 한다 —
	 * {@code {}} 는 truthy 라 그 가드가 "기본정보가 있다"로 잘못 판정한다.
	 *
	 * <p>쓰기는 여기 오지 않는다. 어댑터가 409 {@link CompanyApiException} 으로 거절한다 —
	 * 조용한 성공은 오류보다 나쁘다.
	 */
	@ExceptionHandler(CompanyServerNotAssignedException.class)
	public ResponseEntity<Void> handleCompanyServerNotAssigned(CompanyServerNotAssignedException e) {
		log.info("회원사에 desk 서버 미지정 — 빈 결과로 응답한다. companyId: {}, endpoint: {}",
				e.getCompanyId(), e.getEndpoint());
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(CompanyApiException.class)
	public ModelAndView handleCompanyApiException(CompanyApiException e, HttpServletRequest request) {
		log.error("회원사 API 호출 오류 - companyId: {}, endpoint: {}, statusCode: {}, responseBody: {}",
				e.getCompanyId(), e.getEndpoint(), e.getStatusCode(), e.getResponseBody());

		// 응답 본문에서 실제 오류 메시지 추출
		String errorMessage = e.getResponseBody();
		if (errorMessage == null || errorMessage.isEmpty()) {
			errorMessage = "회원사 시스템 연동 중 오류가 발생했습니다.";
		}

		final ErrorResponse error = ErrorResponse.builder()
				.error("CompanyApiError")
				.status(e.getStatusCode())
				.message(errorMessage)
				.errors(new ArrayList<>())
				.build();

		// 상태 코드에 따라 적절한 HTTP 상태 반환
		HttpStatus httpStatus;
		if (e.getStatusCode() >= 500) {
			httpStatus = HttpStatus.BAD_GATEWAY;
		} else if (e.getStatusCode() >= 400) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return sendError(error, httpStatus, request);
	}

	/**
	 * 요청 본문을 <b>읽지 못했을 때</b> — 400 으로 돌려준다.
	 *
	 * <h3>왜 필요했나</h3>
	 * 이 핸들러가 없어서 계약 위반이 전부 <b>500</b> 으로 나갔다. 실측(dev 2026-08-20):
	 * {@code totHour:"9"} · 빈 {@code buckets} · {@code companyId} 오타 모두 500 이었다.
	 * 500 은 <i>"서버가 잘못했다"</i> 인데 실제로는 <b>보낸 쪽이 계약을 어긴 것</b>이라,
	 * 호출자가 자기 잘못을 응답만 보고 알 수 없었다. connect 에도 같은 결함이 있었다(§10.104).
	 *
	 * <h3>메시지를 그대로 흘리지 않는다</h3>
	 * Jackson 예외 본문에는 클래스 전체이름·패키지 경로가 들어 있다.
	 * {@code IllegalArgumentException}(계약이 스스로 던진 것)일 때만 그 문구를 쓰고,
	 * 나머지는 일반 문구로 덮는다 — 내부 구조를 응답에 싣지 않는다.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ModelAndView handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
		String message = "요청 본문 형식이 올바르지 않습니다.";

		//  계약이 스스로 던진 것(@JsonAnySetter 등)은 어떤 필드가 틀렸는지까지 알려 준다.
		//  그 문구는 우리가 쓴 것이라 내부 구조가 새지 않는다.
		Throwable cause = e.getCause();
		while (cause != null) {
			if (cause instanceof IllegalArgumentException) {
				message = cause.getMessage();
				break;
			}
			cause = cause.getCause();
		}

		log.warn("요청 본문 파싱 실패 - uri: {}, 사유: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());

		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.BAD_REQUEST.value())
				.message(message)
				.errors(new ArrayList<ErrorResponseItem>())
				.build();

		return sendError(error, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * {@code @Valid} 검증 위반 — 400 + <b>필드별</b> 사유.
	 *
	 * <p>필드명을 담는 것이 요점이다. {@code errors} 가 비어 있으면 호출자는 무엇을 고쳐야 할지
	 * 모른 채 재시도만 반복한다 — 그 재시도는 서버 입장에서 <b>구분되지 않는 부하</b>다.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ModelAndView handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
		List<ErrorResponseItem> list = new ArrayList<>();
		for (FieldError fe : e.getBindingResult().getFieldErrors()) {
			list.add(ErrorResponseItem.builder()
					.field(fe.getField())
					.message(fe.getDefaultMessage())
					.build());
		}

		log.warn("요청 검증 실패 - uri: {}, 위반: {}건", request.getRequestURI(), list.size());

		final ErrorResponse error = ErrorResponse.builder()
				.error("Invalid")
				.status(HttpStatus.BAD_REQUEST.value())
				.message(list.isEmpty() ? "요청 값이 올바르지 않습니다." : list.get(0).getMessage())
				.errors(list)
				.build();

		return sendError(error, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * 매핑되지 않은 경로 요청. <b>클라이언트 오류이므로 404 로 내려보낸다.</b>
	 *
	 * <p>아래 {@code exception(Exception)} 이 받으면 <b>500 「시스템 오류」</b>가 되고,
	 * 단순 오탈자·크롤러 요청이 <b>서버 장애로 기록된다.</b>
	 *
	 * <h3>왜 이제 와서 고치나 — 안 보이던 것이 보였다</h3>
	 * 2026-09-10 에 장애 장부(FailureRecorder) writer 를 붙이자 <b>첫 네 행이 전부 이것</b>이었다 —
	 * {@code /} · {@code /robots.txt} · {@code /api/no-such-endpoint} 가 500 으로 나가
	 * {@code EXCEPTION}·{@code ERROR} 로 남았다. 그전에는 파일 로그에만 있어 <b>아무도 안 봤다.</b>
	 * 「4xx 는 담지 않는다」는 기준이 첫날부터 깨지는 자리이고, <b>사람 없이도</b> 쌓인다.
	 *
	 * <p>desk({@code mcomponent.config.exception.GlobalExceptionHandler}) 와 connect 가
	 * 이미 같은 고침을 갖고 있다. <b>관용구를 새로 정하지 않고 그대로 따른다.</b>
	 */
	@ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
	public ModelAndView handleNoResourceFoundException(
			org.springframework.web.servlet.resource.NoResourceFoundException e,
			HttpServletRequest request) {
		log.debug("존재하지 않는 경로: {}", e.getResourcePath());

		final ErrorResponse error = ErrorResponse.builder()
				.error("NotFound")
				.status(HttpStatus.NOT_FOUND.value())
				.message("요청한 경로를 찾을 수 없습니다.")
				.errors(new ArrayList<>())
				.build();

		return sendError(error, HttpStatus.NOT_FOUND, request);
	}

	// 반환형이 Object 인 이유: 대부분은 ModelAndView 지만, desk 서버 미지정만은
	//  ResponseEntity(200 빈 본문)로 돌려줘야 한다. Spring 이 런타임에 둘 다 해석한다.
	@ExceptionHandler(Exception.class)
	public Object exception(Exception e, HttpServletRequest request) throws IOException {

		// 회원사 API 예외가 RuntimeException 등으로 감싸진 경우 원인 체인에서 추출해 전용 처리
		// (연결/타임아웃 계열은 "서버 상태 점검" 메시지가 사용자에게 그대로 전달되도록 함)
		CompanyApiException companyApiException = findCompanyApiException(e);
		if (companyApiException instanceof CompanyServerNotAssignedException) {
			// 어딘가에서 RuntimeException 등으로 <감싸진> 경우에도 같은 판정을 해야 한다.
			//  여기서 놓치면 502 로 나가 위 전용 핸들러가 무의미해진다.
			return handleCompanyServerNotAssigned((CompanyServerNotAssignedException) companyApiException);
		}
		if (companyApiException != null) {
			return handleCompanyApiException(companyApiException, request);
		}

		/*
		 * [장애기록] 여기 오는 것만 남긴다 — 즉 «5xx·미상» 이다.
		 *   4xx(잘못된 요청·없는 경로·인가 거부)는 위쪽 전용 핸들러들이 먼저 잡아 여기까지 오지 않는다.
		 *   corebase 2.1.34 가 정한 등급 기준(4xx WARN · 5xx/미상 ERROR)과 «같은 선»이라
		 *   파일 로그와 DB 가 어긋나지 않는다. 전부 담으면 그 표가 곧 가장 커지고 아무도 안 본다.
		 *
		 * ⚠ 기록이 실패해도 호출자를 깨뜨리지 않는다 — FailureRecorder 가 예외를 받아 WARN 만 남긴다.
		 *   장애를 적으려다 장애를 만들지 않는다.
		 */
		failureRecorder.record(micoai.corebase.failure.FailKind.EXCEPTION, subjectOf(request),
				"admin 처리 중 예외 - " + e.getClass().getName(), e);

		final ErrorResponse error = ErrorResponse.builder()
				.error("Exception")
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message("시스템 오류로 작업을 중단하였습니다.")
				.errors(new ArrayList<>())
				.build();

		return sendError(error, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * 「무엇에 대해」 실패했나 — 나중에 <b>찾을 수 있는</b> 값이어야 한다.
	 *
	 * <p>요청 경로를 쓴다. 스택은 「왜」를 말하지만 「어디서」를 <b>검색하게</b> 해 주지 않는다.
	 *
	 * <p>⛔ 쿼리스트링은 <b>붙이지 않는다</b> — 거기에 검색어·연락처 같은 것이 실린다.
	 * 오늘 이 팀이 확인한 대로 지금 표에 개인정보는 0건인데, <b>「지금 안 담겼다」는 「안 담긴다」가 아니다.</b>
	 *
	 * <p>⚠ 200자를 넘으면 {@code FailureRecorder} 가 <b>자르지 않고 거부</b>한다
	 * (잘린 subject 는 「찾을 수 없는 기록」이 되므로). 그래서 여기서 줄여 둔다.
	 */
	private String subjectOf(HttpServletRequest request) {
		String uri = request != null ? request.getRequestURI() : null;
		if (uri == null || uri.isBlank()) {
			return "admin:unknown";
		}
		String s = "admin:" + uri;
		return s.length() > 200 ? s.substring(0, 200) : s;
	}

	/**
	 * 예외의 원인(cause) 체인을 따라가며 CompanyApiException을 탐색한다.
	 *
	 * @param throwable 최상위 예외
	 * @return 발견된 CompanyApiException, 없으면 null
	 */
	private CompanyApiException findCompanyApiException(Throwable throwable) {
		Throwable t = throwable;
		while (t != null) {
			if (t instanceof CompanyApiException) {
				return (CompanyApiException) t;
			}
			t = t.getCause();
		}
		return null;
	}



}
