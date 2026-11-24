package buttle.apptpl.common.polling.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import buttle.apptpl.common.polling.dto.PollingDto;
import buttle.apptpl.common.polling.mapper.PollingMapper;

@Service
@RequiredArgsConstructor
@Transactional
public class PollingService {

    private final PollingMapper pollingMapper;

    /**
     * admin 폴링 조회
     * 
     * @param userId 사용자 ID
     * @return admin 폴링 조회 결과
     */
    public PollingDto selectPolling(String cmpyId, String memberId) {

        PollingDto pollingDto = new PollingDto();

        int systemNotice = pollingMapper.selectPollingNotice(memberId);
        if( systemNotice > 0 ) {
            pollingDto.setIsEntryData(true);
            pollingDto.setSystemNotice(true);
        }

        Integer serviceAnswer = pollingMapper.selectPollingServiceAnswer(cmpyId, memberId);
        if( serviceAnswer != null && serviceAnswer > 0 ) {
            pollingDto.setIsEntryData(true);
            pollingDto.setServiceAnswer(true);
        }

        Integer umsSedngInfo = pollingMapper.selectUmsSedngInfo(memberId);
        if( umsSedngInfo != null && umsSedngInfo > 0 ) {
            pollingDto.setIsEntryData(true);
            pollingDto.setUmsSedngInfo(true);
        }

        return pollingDto;
    }


}
