package buttle.apptpl.cmm.saleday.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import buttle.apptpl.cmm.saleday.vo.SYS5030001Vo;
import buttle.apptpl.cmm.saleday.vo.SYS5030002Vo;

@Mapper
public interface BusinessDayMapper {
   
    Page<SYS5030002Vo> selectBusinessDayList(SYS5030001Vo param, Pageable pageable);

    Integer selectBusinessDay(SYS5030001Vo param);
} 