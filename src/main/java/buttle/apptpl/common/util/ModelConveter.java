package buttle.apptpl.common.util;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component // Spring 컨테이너에 의해 싱글톤 빈으로 관리
public class ModelConveter {

    private final ModelMapper modelMapper; // final로 선언하여 불변성 및 필수 의존성 명확화

    // 생성자 주입: ModelMapper 빈을 주입받음 (권장)
    public ModelConveter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * 단일 객체를 다른 타입의 객체로 매핑합니다.
     *
     * @param source         원본 객체 (VO, Entity 등)
     * @param destinationType 변환될 대상 객체의 Class 타입 (DTO 등)
     * @param <S>            원본 객체의 타입
     * @param <D>            대상 객체의 타입
     * @return 변환된 대상 객체
     */
    public <S, D> D map(S source, Class<D> destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }

    /**
     * 객체 리스트를 다른 타입의 객체 리스트로 매핑합니다.
     *
     * @param sourceList      원본 객체 리스트 (List<VO>, List<Entity> 등)
     * @param destinationType 변환될 대상 객체의 Class 타입 (DTO 등)
     * @param <S>             원본 객체 리스트의 요소 타입
     * @param <D>             대상 객체 리스트의 요소 타입
     * @return 변환된 대상 객체 리스트
     */
    public <S, D> List<D> mapList(List<S> sourceList, Class<D> destinationType) {
        if (sourceList == null || sourceList.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        return sourceList.stream()
                         .map(element -> modelMapper.map(element, destinationType))
                         .collect(Collectors.toList());
    }

    // ModelMapper의 update 메서드를 래핑할 수도 있습니다.
    /**
     * 원본 객체의 필드를 대상 객체로 복사하여 업데이트합니다.
     *
     * @param source 원본 객체
     * @param destination 대상 객체 (업데이트될 객체)
     * @param <S> 원본 객체의 타입
     * @param <D> 대상 객체의 타입
     */
    public <S, D> void map(S source, D destination) {
        if (source != null && destination != null) {
            modelMapper.map(source, destination);
        }
    }
}