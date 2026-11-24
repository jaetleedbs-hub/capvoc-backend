package buttle.apptpl.common.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 「지금 이 요청의 언어는 무엇인가」를 정하는 <b>유일한</b> 자리.
 *
 * <p><b>왜 한 곳인가.</b> 코드표·메시지·FAQ 분류처럼 언어별 행을 가진 표는 전부
 * {@code locale} 컬럼으로 갈린다. 그 조건을 각 SQL 이 {@code 'ko'} 로 박아 두면 —
 * 실제로 그렇게 돼 있었다 — 언어가 하나일 때는 아무 문제가 없다가, <b>두 번째 언어의 행을
 * 넣는 순간</b> 조회가 두 행을 돌려주고 화면은 그중 아무거나 보여 준다.
 * 이 템플릿을 만들며 {@code en} 시드를 넣자 한국어 화면의 「사용여부」가 <b>Use</b> 로 떴다.
 *
 * <p>언어는 <b>데이터</b>이지 코드가 아니다. 판정은 여기서 한 번 하고, SQL 은 그 값을 받는다.
 *
 * <p><b>정하는 순서</b>
 * <ol>
 *   <li>HTTP 요청이 있으면 그 요청의 로케일(Spring 의 LocaleResolver — 기본은 Accept-Language)</li>
 *   <li>그 언어가 <b>이 시스템이 지원하는 목록</b>({@code app.supported-locales})에 없으면 기본값</li>
 *   <li>요청이 없으면(배치·부팅·스케줄러) <b>설정의 기본값</b>({@code app.default-locale})</li>
 * </ol>
 *
 * <p>⛔ JVM 기본 로케일({@code Locale.getDefault()})에는 절대 기대지 않는다.
 * 그것은 <b>서버가 깔린 OS 의 설정</b>이라 배포 기계마다 다르고, 아무도 그것을 관리하지 않는다.
 */
@Component
public class LocaleSupport {

    private final String defaultLocale;
    private final List<String> supported;

    public LocaleSupport(
            @Value("${app.default-locale:ko}") String defaultLocale,
            @Value("${app.supported-locales:ko,en}") String supportedCsv) {
        this.defaultLocale = defaultLocale.trim();
        this.supported = Arrays.stream(supportedCsv.split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    /** 지금 요청의 언어 코드({@code ko}·{@code en} …). 요청이 없으면 기본값. */
    public String current() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return defaultLocale;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String lang = locale == null ? "" : locale.getLanguage();
        return supported.contains(lang) ? lang : defaultLocale;
    }

    public String defaultLocale() {
        return defaultLocale;
    }

    public List<String> supported() {
        return supported;
    }
}
