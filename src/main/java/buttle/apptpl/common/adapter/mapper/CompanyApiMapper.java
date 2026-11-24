package buttle.apptpl.common.adapter.mapper;

import org.apache.ibatis.annotations.Mapper;
import buttle.apptpl.common.adapter.dto.CompanyApiInfo;

/**
 * 회원사 API 정보 Mapper
 * 
 * @since 2026-01-15
 * @author ID-ABO 개발팀
 */
@Mapper
public interface CompanyApiMapper {
    
    /**
     * 회원사 API 정보 조회
     * 
     * @param companyId 회원사ID
     * @return 회원사 API 정보
     */
    CompanyApiInfo selectCompanyApiInfo(String companyId);
}
