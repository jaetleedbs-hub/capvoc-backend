package buttle.apptpl.api.shared.dto;

import java.util.List;

import lombok.Data;

/**
 * APP 메시지 발송 대상 DTO
 * 
 * @author ID-ABO 개발팀
 * @since 2026-01-16
 */
@Data
public class AppSendTargetDto {

    /** UMS 일련번호 */
    private Integer umsSn;

    /** 회원사ID */
    private String cmpyId;
    
    /** 회원ID */
    private String mbrId;
    
    /** 회원전화번호 */
    private String mbrTelno;
    
    /** 회원이메일주소 */
    private String mbrEmlAddr;
    
    /** 회원권한코드 */
    private String mbrAuthrtCd;
    
    /** 발송결과코드 */
    private String sendRsltCd;

}

