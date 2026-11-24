package buttle.apptpl.common.adapter.exception;

/**
 * 회원사에 <b>desk 서버가 지정되어 있지 않다</b>는 신호.
 *
 * <p><b>이것은 장애가 아니라 상태다.</b> Gateway 연계 전용 회원사(예: {@code micohelp})는
 * desk 배포 대상이 아니라 {@code cmpt_cmpy_inf.desk_svr_id} 가 비는 것이 정상이고,
 * 외부 신원 연계만 쓰는 회원사가 앞으로 늘어난다(V44 가 {@code gw_svr_id} 를 따로 둔 이유다).
 *
 * <p>종전에는 그런 회원사를 고르면 base URL 이 null 인 채로 WebClient 호출까지 가서
 * 네트워크 예외가 났고, {@code isServerUnavailable} 이 그것을 <i>"회원사 API 서버에 연결할 수
 * 없습니다"</i> 502 로 바꿨다. <b>정상 상태가 서버 장애로 보였고</b>, 그 모달이 오버레이를 깔아
 * 저장 버튼까지 막았다.
 *
 * <p>{@link CompanyApiException} 을 상속하는 것은 의도적이다. 각 서비스가 이미
 * {@code catch (CompanyApiException e) { throw e; }} 로 통과시키고 있고
 * ({@code 78fb381} 오류 계약 통일), {@code GlobalExceptionHandler.findCompanyApiException} 도
 * 원인 체인에서 이 타입을 그대로 찾아낸다. 별도 계층을 만들면 그 두 장치를 다시 손대야 한다.
 *
 * <p><b>읽기와 쓰기를 다르게 다룬다</b>(호출부: {@code CompanyApiAdapter}).
 * <ul>
 *   <li><b>읽기(GET)</b> — 이 예외를 던지고 핸들러가 <b>200 + 빈 본문</b>으로 답한다.
 *       "desk 쪽에 그 정보가 없다"는 참이고, 화면은 이미 <i>미등록 = null</i> 을 다룰 줄 안다
 *       ({@code BO202_S.loadBaseInfo} 의 저장 가드가 그 전제로 쓰여 있다).</li>
 *   <li><b>쓰기(POST/PUT/DELETE)</b> — 던지지 <b>않는다</b>. 조용한 성공은 오류보다 나쁘다.
 *       {@code CompanyApiException}(409)으로 "desk 서버가 지정되지 않아 저장할 수 없다"를
 *       그대로 알린다.</li>
 * </ul>
 */
public class CompanyServerNotAssignedException extends CompanyApiException {

    public CompanyServerNotAssignedException(String companyId, String endpoint) {
        super(companyId, endpoint, 200,
              "회원사에 desk 서버가 지정되어 있지 않습니다: " + companyId, null);
    }
}
