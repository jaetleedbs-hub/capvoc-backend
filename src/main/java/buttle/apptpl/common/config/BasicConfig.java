package buttle.apptpl.common.config;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import kong.unirest.Unirest;
import buttle.apptpl.common.framework.interceptor.CoreInterceptor;
import micoai.corebase.bind.converter.BigDecimalConverter;
import micoai.corebase.bind.converter.BooleanConverter;
import micoai.corebase.bind.converter.DateConverter;
import micoai.corebase.bind.converter.DoubleConverter;
import micoai.corebase.bind.converter.FloatConverter;
import micoai.corebase.bind.converter.IntegerConverter;
import micoai.corebase.bind.converter.LocalDateTimeConverter;
import micoai.corebase.bind.converter.LongConverter;
import micoai.corebase.bind.converter.ShortConverter;
import micoai.corebase.bind.deserializer.BigDecimalDeserializer;
import micoai.corebase.bind.deserializer.BooleanDeserializer;
import micoai.corebase.bind.deserializer.DateDeserializer;
import micoai.corebase.bind.deserializer.DoubleDeserializer;
import micoai.corebase.bind.deserializer.FloatDeserializer;
import micoai.corebase.bind.deserializer.IntegerDeserializer;
import micoai.corebase.bind.deserializer.LocalDateTimeDeserializer;
import micoai.corebase.bind.deserializer.LongDeserializer;
import micoai.corebase.bind.deserializer.ShortDeserializer;
import micoai.corebase.bind.resolver.BigDecimalArgumentResolver;
import micoai.corebase.bind.resolver.BooleanArgumentResolver;
import micoai.corebase.bind.resolver.DateArgumentResolver;
import micoai.corebase.bind.resolver.DoubleArgumentResolver;
import micoai.corebase.bind.resolver.FloatArgumentResolver;
import micoai.corebase.bind.resolver.IntegerArgumentResolver;
import micoai.corebase.bind.resolver.LocalDateTimeArgumentResolver;
import micoai.corebase.bind.resolver.LongArgumentResolver;
import micoai.corebase.bind.resolver.ShortArgumentResolver;
import micoai.corebase.view.ExcelView;
import micoai.corebase.view.ReportExcelView;

@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class BasicConfig implements SchedulingConfigurer, WebMvcConfigurer {

	@Autowired
	private CoreInterceptor fwCoreInterceptor;

	// @Autowired
	// private SvcInterceptor svcInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(fwCoreInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/static/**", "/**/*.png", "/**/*.ico", "/**/*.jpg", "/**/*.gif");
		// registry.addInterceptor(svcInterceptor)
		// 		.addPathPatterns("/**")
		// 		.excludePathPatterns("/static/**", "/**/*.png", "/**/*.ico", "/**/*.jpg", "/**/*.gif");

//		registry.addInterceptor(localeChangeInterceptor())
//				.addPathPatterns("/**");
	}


	@Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10); //## 조정해 주세요.
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }

	@Bean("excelView")
	ExcelView excelView() {
		return new ExcelView();
	}

	@Bean("reportExcelView")
	ReportExcelView reportExcelView() {
		return new ReportExcelView();
	}

	@Bean("jsonView")
	MappingJackson2JsonView jsonView() {
		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
		jsonView.setExtractValueFromSingleKeyModel(true);
		jsonView.setPrettyPrint(true);
		return jsonView;
	}

	/**
	 * 다국어 변경 interceptor
	 * 다국어를 사용할 경우, 아래 둘 중 하나만 주석을 풀어서 사용한다.
	 */
//	@Bean
//	HandlerInterceptor localeChangeInterceptor() {
//		return new FwLocaleChangeInterceptor(fwInterface);
//	}
//
//	@Bean
//	HandlerInterceptor localeChangeHostInterceptor() {
//		return new FwLocaleChangeInterceptor(fwInterface);
//	}

	@PostConstruct
	public void postConstruct() {
		ObjectMapper mapper = objectMapper();
		Unirest.config().setObjectMapper(new kong.unirest.ObjectMapper() {
			@Override
			public <T> T readValue(String value, Class<T> valueType) {
				try {
                    return mapper.readValue(value, valueType);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
			}

			@Override
			public String writeValue(Object value) {
				try {
					return mapper.writeValueAsString(value);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

    public ObjectMapper objectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        SimpleModule module = new SimpleModule("fwDeserializer");

        module.addDeserializer(Date.class, new DateDeserializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addDeserializer(Boolean.class, new BooleanDeserializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        module.addDeserializer(Double.class, new DoubleDeserializer());
        module.addDeserializer(Float.class, new FloatDeserializer());
        module.addDeserializer(Integer.class, new IntegerDeserializer());
        module.addDeserializer(Long.class, new LongDeserializer());
        module.addDeserializer(Short.class, new ShortDeserializer());

        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }


	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new BooleanConverter());
		registry.addConverter(new BigDecimalConverter());
		registry.addConverter(new DateConverter());
		registry.addConverter(new LocalDateTimeConverter());
		registry.addConverter(new DoubleConverter());
		registry.addConverter(new FloatConverter());
		registry.addConverter(new IntegerConverter());
		registry.addConverter(new LongConverter());
		registry.addConverter(new ShortConverter());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new BigDecimalArgumentResolver());
		argumentResolvers.add(new BooleanArgumentResolver());
		argumentResolvers.add(new DateArgumentResolver());
		argumentResolvers.add(new LocalDateTimeArgumentResolver());
		argumentResolvers.add(new DoubleArgumentResolver());
		argumentResolvers.add(new FloatArgumentResolver());
		argumentResolvers.add(new IntegerArgumentResolver());
		argumentResolvers.add(new LongArgumentResolver());
		argumentResolvers.add(new ShortArgumentResolver());
	}

	@Bean
	HttpMessageConverter<?> jsonConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper());
		converter.setDefaultCharset(StandardCharsets.UTF_8);

		return converter;
	} 

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jsonConverter());
	}
	
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // 필요에 따라 매핑 전략 설정 (예: STRICT, STANDARD, LOOSE)
        // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }	
}
