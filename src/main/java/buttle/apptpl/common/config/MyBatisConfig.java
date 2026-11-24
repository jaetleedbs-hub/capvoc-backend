package buttle.apptpl.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import buttle.apptpl.common.mybatis.MybatisParameterInterceptor;
import buttle.apptpl.common.mybatis.MybatisPrepareInterceptor;
import buttle.apptpl.common.mybatis.MybatisResultInterceptor;
import buttle.apptpl.common.mybatis.PageCustomObjectFactory;
import buttle.apptpl.common.mybatis.PageObjectWrapperFactory;

@Configuration
@MapperScan(basePackages = {
    "buttle.apptpl.**.mapper",
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        buttle.apptpl.common.jwt.JwtRepository.class
    })
}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mainDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // Interceptor 등록
        sessionFactory.setPlugins(
            new MybatisParameterInterceptor(),
            new MybatisPrepareInterceptor(),
            new MybatisResultInterceptor()
        );
        
        // Configuration 설정
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        
        // Custom ObjectFactory 설정 (Page 타입 처리용)
        configuration.setObjectFactory(new PageCustomObjectFactory());
        
        // Custom ObjectWrapperFactory 설정 (Page 타입 처리용)
        configuration.setObjectWrapperFactory(new PageObjectWrapperFactory());
        
        // 기타 설정
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        
        // 매퍼 간 참조를 위한 설정
        configuration.setLazyLoadingEnabled(false);
        configuration.setAggressiveLazyLoading(false);
        
        sessionFactory.setConfiguration(configuration);
        
        // 매퍼 XML 로딩(admin) — 경로 구분자 수정(buttle/apptpl)
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] mappers = resolver.getResources("classpath*:buttle/apptpl/**/mapper/*.xml");
        sessionFactory.setMapperLocations(mappers);
        
        return sessionFactory.getObject();
    }
} 