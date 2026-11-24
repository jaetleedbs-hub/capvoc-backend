package buttle.apptpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class })
// admin은 자체 framework(buttle.apptpl.common.framework) 빈을 사용. corebase는 정적 util + tenant(auto-config)로만 활용
// → corebase 프레임워크 @Component/@Service 빈은 스캔하지 않음(중복/불필요 빈 및 미충족 의존성 회피)
@ComponentScan(basePackages = {"buttle.apptpl.common.framework", "buttle.apptpl"})
@EnableJpaAuditing
@EnableTransactionManagement
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class AppTemplateApplication {

    public static void main(String[] args) {
        // application.yml에서 기본 프로파일(pc)이 설정되어 있어 별도 설정 불필요
        SpringApplication.run(AppTemplateApplication.class, args);
    }

}
