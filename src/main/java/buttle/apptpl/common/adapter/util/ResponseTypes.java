package buttle.apptpl.common.adapter.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * CompanyApiResponse 타입 변환 유틸리티
 * 
 * <p>ObjectMapper의 TypeReference를 쉽게 생성할 수 있는 헬퍼 메서드를 제공합니다.</p>
 * 
 * @since 2026-01-19
 * @author ID-ABO 개발팀
 */
public class ResponseTypes {
    
    /**
     * CompanyApiResponse<List<T>> 타입 생성
     * 
     * <p>사용 예:
     * <pre>
     * objectMapper.convertValue(rawData, ResponseTypes.listOf(MemberDto.class))
     * </pre>
     * </p>
     * 
     * @param <T> List 요소 타입
     * @param elementType List 요소의 클래스 타입
     * @return TypeReference for CompanyApiResponse<List<T>>
     */
    public static <T> TypeReference<List<T>> listOf(Class<T> elementType) {
        return new TypeReference<List<T>>() {};
    }
    
    /**
     * CompanyApiResponse<T> 타입 생성
     * 
     * <p>사용 예:
     * <pre>
     * objectMapper.convertValue(rawData, ResponseTypes.of(MemberDto.class))
     * </pre>
     * </p>
     * 
     * @param <T> 응답 데이터 타입
     * @param dataType 응답 데이터의 클래스 타입
     * @return TypeReference for T
     */
    public static <T> TypeReference<T> of(Class<T> dataType) {
        return new TypeReference<T>() {};
    }
}
