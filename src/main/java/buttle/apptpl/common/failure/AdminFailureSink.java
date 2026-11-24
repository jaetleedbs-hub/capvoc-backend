package buttle.apptpl.common.failure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import micoai.corebase.failure.FailureRecord;
import micoai.corebase.failure.FailureSink;

/**
 * admin 의 장애를 <b>admin 자기 스키마</b>에 쓴다.
 *
 * <h3>왜 corebase 기본 sink 를 안 쓰나</h3>
 * corebase 기본 sink 는 {@code micoai.corebase.common.service.LogService} 로 쓰는데,
 * <b>admin 은 corebase 프레임워크 빈을 일부러 스캔하지 않는다</b>
 * ({@code AppTemplateApplication} 의 주석: <i>「중복/불필요 빈 및 미충족 의존성 회피」</i>).
 *
 * <p>2026-09-10 에 그대로 붙여 보고 <b>되돌렸다</b> — 컴파일은 통과했는데 컨텍스트가 안 떴고
 * ({@code NoSuchBeanDefinitionException: LogService}) 그 저장소의 시험이 잡았다.
 * 그래서 corebase 가 <b>규칙과 저장을 갈랐다</b>({@code FailureSink}). 이 클래스가 그 「저장」쪽이다.
 *
 * <h3>⛔ 여기서 하지 않는 것</h3>
 * <ul>
 *   <li><b>값을 다시 손보지 않는다.</b> 자름·검증·기본값은 {@code FailureRecorder} 가
 *       <b>한 곳에서</b> 끝냈다. 구현마다 다시 자르면 앱에 따라 길이가 달라져
 *       <b>표를 못 믿게 된다</b></li>
 *   <li><b>예외를 삼키지 않는다.</b> 쓰기가 실패하면 그대로 던진다 —
 *       {@code FailureRecorder} 가 받아 WARN 을 남긴다. 여기서 삼키면 「기록됐다」가 거짓이 되고,
 *       그것이 이 표가 없애려는 바로 그 병이다</li>
 *   <li><b>시각을 바꾸지 않는다.</b> {@code Instant} 를 그대로 넘긴다 —
 *       {@code Date} 로 되돌리면 2.1.37 이 타입으로 닫은 것을 다시 연다</li>
 * </ul>
 *
 * <p>{@code @ConditionalOnMissingBean} 덕에 이 빈이 있으면 corebase 기본 sink 는 등록되지 않는다.
 *
 * @since 2026-09-10
 */
@Configuration
@RequiredArgsConstructor
public class AdminFailureSink {

    private final buttle.apptpl.common.failure.mapper.AdminFailureLogMapper mapper;

    /**
     * ⚠ 메서드 이름을 클래스 이름과 <b>다르게</b> 둔다.
     *
     * <p>처음에 {@code adminFailureSink()} 로 지었더니 <b>기동이 죽었다</b> —
     * {@code @Configuration} 클래스의 기본 빈 이름도 {@code adminFailureSink} 라
     * <i>「factory-bean reference points back to the same bean definition」</i> 이 났다.
     * 컴파일은 통과하고 시험에서 컨텍스트가 안 뜨는 것으로 드러났다(2026-09-10).
     */
    @Bean
    public FailureSink failureSink() {
        return this::write;
    }

    /** 받은 값을 <b>그대로</b> 자기 스키마에 넣는다. */
    private void write(FailureRecord r) {
        mapper.insertFailure(
                r.svcCd(),
                r.svrId(),
                r.cmpyId(),
                r.failKindCd(),
                r.subject(),
                r.reason(),
                r.errCd(),
                r.sevCd(),
                r.occurredAt());
    }
}
