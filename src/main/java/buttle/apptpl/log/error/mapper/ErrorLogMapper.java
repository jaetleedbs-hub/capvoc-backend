package buttle.apptpl.log.error.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.log.error.vo.SYS6020001Vo;
import buttle.apptpl.log.error.vo.SYS6020002Vo;
import buttle.apptpl.log.error.vo.SYS6020003Vo;

/**
 * SYS6020 오류로그 Mapper
 *
 * <p>admin <b>자기 스키마</b>({@code logt_error_log})를 읽는다 —
 * 형제 화면(SYS6040 프로그램접속이력)과 같은 자리다.
 * 「admin 로그는 admin 스키마, 통합은 배치로」가 이 저장소의 결정이고,
 * 다른 서비스(desk·connect·search·chat)의 행은 <b>배치가 이 표로 끌어온 뒤</b> 여기서 함께 보인다.
 */
@Mapper
public interface ErrorLogMapper {

    /**
     * 오류로그 목록.
     *
     * <p>⚠ {@code err_cn} 은 <b>싣지 않는다.</b> 사유는 4,000자까지 가고 실측 최대가 35,306자였다 —
     * 목록 한 페이지에 그것을 20개 담으면 응답이 수백 KB 가 된다. 상세에서만 연다.
     */
    Page<SYS6020002Vo> selectErrorLogList(SYS6020001Vo param, Pageable pageable);

    /** 오류로그 상세 — 사유 전문과 원본 추적 칸을 포함한다. */
    SYS6020003Vo selectErrorLog(@Param("logSid") Integer logSid);
}
