# 개인정보 마스킹 처리 DTO 문서

## 개요
이 문서는 AICC Backend 프로젝트에서 개인정보 보호를 위해 마스킹 처리된 DTO(Data Transfer Object)들의 상세 정보를 정리한 문서입니다.

## 마스킹 유형 (MaskingType)
- `NAME`: 고객명 마스킹
- `PHONE_NUMBER`: 전화번호 마스킹  
- `ACCOUNT_NUMBER`: 계좌번호 마스킹
- `RRN`: 주민등록번호/실명확인번호 마스킹
- `EMAIL`: 이메일주소 마스킹
- `ADDRESS`: 상세주소 마스킹
- `PASSPORT`: 여권번호 마스킹
- `DRIVE`: 운전면허번호 마스킹
- `CARD`: 신용카드번호 마스킹

---

## 마스킹 처리된 DTO 목록

### 1. ASM (Assessment) 패키지

#### ASM2020001Dto - 콜평가 대상 DTO
- **컨트롤러**: `ASM2020Controller`
- **URL**: `/api/asm/ASM2020/r01`
- **METHOD**: `POST`
- **기능**: 콜평가 대상 조회
- **마스킹 필드**:
  - `dscsnTelNo` (전화번호) → `PHONE_NUMBER`
  - `custActno` (계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`

#### ASM2050001Dto - 콜평가 평가회차 목록 DTO
- **컨트롤러**: `ASM2050Controller`
- **URL**: `/api/asm/ASM2050/r01`
- **METHOD**: `POST`
- **기능**: 콜평가 평가회차 목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`

#### ASM2090002Dto - 콜평가 목록 DTO
- **컨트롤러**: `ASM2090Controller`
- **URL**: `/api/asm/ASM2090/r01`, `/api/asm/ASM2090/r02`
- **METHOD**: `POST`
- **기능**: 콜평가 목록 조회, 콜평가명 목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`

#### ASM3010002Dto - 캠페인 처리결과 DTO
- **컨트롤러**: `ASM3010Controller`
- **URL**: `/api/asm/ASM3010/r01`
- **METHOD**: `POST`
- **기능**: 캠페인 처리결과 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`

---

### 2. CMP (Campaign) 패키지

#### CMP2030001Dto - 캠페인 인수인계 사용자 조회 DTO
- **컨트롤러**: `CMP2030Controller`
- **URL**: `/api/cmp/CMP2030/r01`
- **METHOD**: `POST`
- **기능**: 캠페인 인수인계 사용자 조회
- **마스킹 필드**:
  - `custTelNo` (휴대전화) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

---

### 3. CNS (Consultation) 패키지

#### CNS0020001Dto - 통화예약 리스트 조회 요청 DTO
- **컨트롤러**: `CNS0020Controller`
- **URL**: `/api/cns/CNS0020/r01`
- **METHOD**: `POST`
- **기능**: 통화예약 리스트 조회
- **마스킹 필드**:
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS0020002Dto - 통화예약 리스트 조회 응답 DTO
- **컨트롤러**: `CNS0020Controller`
- **URL**: `/api/cns/CNS0020/r01`
- **METHOD**: `POST`
- **기능**: 통화예약 리스트 조회
- **마스킹 필드**:
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS0030001Dto - 콜백 리스트 조회 요청 DTO
- **컨트롤러**: `CNS0030Controller`
- **URL**: `/api/cns/CNS0030/r01`
- **METHOD**: `POST`
- **기능**: 콜백 리스트 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `callbakTelNo` (콜백전화번호) → `PHONE_NUMBER`

#### CNS0030002Dto - 콜백 리스트 조회 응답 DTO
- **컨트롤러**: `CNS0030Controller`
- **URL**: `/api/cns/CNS0030/r01`
- **METHOD**: `POST`
- **기능**: 콜백 리스트 조회
- **마스킹 필드**:
  - `callbakInclTelNo` (콜백인입전화번호) → `PHONE_NUMBER`
  - `callbakRsvtTelNo` (콜백예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS0040002Dto - 캠페인 콜리스트 조회 DTO
- **컨트롤러**: `CNS0040Controller`
- **URL**: `/api/cns/CNS0040/r01`
- **METHOD**: `POST`
- **기능**: 캠페인 콜리스트 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS0081001Dto - 문제행동고객 조회 요청 DTO
- **컨트롤러**: `CNS0081Controller`
- **URL**: `/api/cns/CNS0081/r01`
- **METHOD**: `POST`
- **기능**: 문제행동고객 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`

#### CNS0081002Dto - 문제행동고객 조회 응답 DTO
- **컨트롤러**: `CNS0081Controller`
- **URL**: `/api/cns/CNS0081/r01`
- **METHOD**: `POST`
- **기능**: 문제행동고객 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS0160001Dto - 상담메인 상담저장 요청 DTO
- **컨트롤러**: `CNS0160Controller`
- **URL**: `/api/cns/CNS0160/u01`
- **METHOD**: `POST`
- **기능**: 상담 저장
- **마스킹 필드**:
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`

#### CNS0160003Dto - 업무 이관 이력 저장 요청 DTO
- **컨트롤러**: `CNS0160Controller`
- **URL**: `/api/cns/CNS0160/u03`
- **METHOD**: `POST`
- **기능**: 업무 이관 이력 저장
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custBacntNo` (고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS0160004Dto - 통화 예약 저장 요청 DTO
- **컨트롤러**: `CNS0160Controller`
- **URL**: `/api/cns/CNS0160/u04`
- **METHOD**: `POST`
- **기능**: 통화 예약 저장
- **마스킹 필드**:
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS0160006Dto - 상담 이력 조회 응답 DTO
- **컨트롤러**: `CNS0160Controller`
- **URL**: `/api/cns/CNS0160/r06`
- **METHOD**: `POST`
- **기능**: 상담 이력 조회
- **마스킹 필드**:
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`

#### CNS0170002Dto - 상담 이력 조회 응답 DTO
- **컨트롤러**: `CNS0170Controller`
- **URL**: `/api/cns/CNS0170/r02`
- **METHOD**: `POST`
- **기능**: 상담 이력 조회
- **마스킹 필드**:
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`

#### CNS0200004Dto - SMS 발송 이력 저장 DTO
- **컨트롤러**: `CNS0200Controller`
- **URL**: `/api/cns/CNS0200/u04`
- **METHOD**: `POST`
- **기능**: SMS 발송 이력 저장
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `smsRcptnNo` (SMS수신번호) → `PHONE_NUMBER`

#### CNS1010001Dto - 캠페인대상목록 요청 DTO
- **컨트롤러**: `CNS1010Controller`
- **URL**: `/api/cns/CNS1010/r01`
- **METHOD**: `POST`
- **기능**: 캠페인대상목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`

#### CNS1010002Dto - 캠페인대상목록 응답 DTO
- **컨트롤러**: `CNS1010Controller`
- **URL**: `/api/cns/CNS1010/r01`
- **METHOD**: `POST`
- **기능**: 캠페인대상목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS1020001Dto - 콜백목록 요청 DTO
- **컨트롤러**: `CNS1020Controller`
- **URL**: `/api/cns/CNS1020/r01`
- **METHOD**: `POST`
- **기능**: 콜백목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `callbakTelNo` (콜백전화번호) → `PHONE_NUMBER`

#### CNS1020002Dto - 콜백목록 응답 DTO
- **컨트롤러**: `CNS1020Controller`
- **URL**: `/api/cns/CNS1020/r01`
- **METHOD**: `POST`
- **기능**: 콜백목록 조회
- **마스킹 필드**:
  - `callbakInclTelNo` (콜백인입전화번호) → `PHONE_NUMBER`
  - `callbakRsvtTelNo` (콜백예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS1030001Dto - 통화예약목록 요청 DTO
- **컨트롤러**: `CNS1030Controller`
- **URL**: `/api/cns/CNS1030/r01`
- **METHOD**: `POST`
- **기능**: 통화예약목록 조회
- **마스킹 필드**:
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS1030002Dto - 통화예약목록 응답 DTO
- **컨트롤러**: `CNS1030Controller`
- **URL**: `/api/cns/CNS1030/r01`
- **METHOD**: `POST`
- **기능**: 통화예약목록 조회
- **마스킹 필드**:
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS2010001Dto - 상담이력 요청 DTO
- **컨트롤러**: `CNS2010Controller`
- **URL**: `/api/cns/CNS2010/r01`
- **METHOD**: `POST`
- **기능**: 상담이력 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`

#### CNS2010002Dto - 상담이력 응답 DTO
- **컨트롤러**: `CNS2010Controller`
- **URL**: `/api/cns/CNS2010/r01`
- **METHOD**: `POST`
- **기능**: 상담이력 조회
- **마스킹 필드**:
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`

#### CNS2020001Dto - SMS발송이력 요청 DTO
- **컨트롤러**: `CNS2020Controller`
- **URL**: `/api/cns/CNS2020/r01`
- **METHOD**: `POST`
- **기능**: SMS발송이력 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `smsRcptnNo` (SMS수신번호) → `PHONE_NUMBER`

#### CNS2020002Dto - SMS발송이력 응답 DTO
- **컨트롤러**: `CNS2020Controller`
- **URL**: `/api/cns/CNS2020/r01`
- **METHOD**: `POST`
- **기능**: SMS발송이력 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `smsRcptnNo` (SMS수신번호) → `PHONE_NUMBER`

#### CNS3020001Dto - 문제행동소비자 목록 조회 요청 DTO
- **컨트롤러**: `CNS3020Controller`
- **URL**: `/api/cns/CNS3020/r01`
- **METHOD**: `POST`
- **기능**: 문제행동소비자 목록 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`

#### CNS3020002Dto - 문제행동소비자 목록 조회 응답 DTO
- **컨트롤러**: `CNS3020Controller`
- **URL**: `/api/cns/CNS3020/r01`
- **METHOD**: `POST`
- **기능**: 문제행동소비자 목록 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS3020003Dto - 문제행동소비자 저장 요청 DTO
- **컨트롤러**: `CNS3020Controller`
- **URL**: `/api/cns/CNS3020/u01`
- **METHOD**: `POST`
- **기능**: 문제행동소비자 저장
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS3030001Dto - 문제행동소비자이력 요청 DTO
- **컨트롤러**: `CNS3030Controller`
- **URL**: `/api/cns/CNS3030/r01`
- **METHOD**: `POST`
- **기능**: 문제행동소비자이력 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`

#### CNS3030002Dto - 문제행동소비자이력 응답 DTO
- **컨트롤러**: `CNS3030Controller`
- **URL**: `/api/cns/CNS3030/r01`
- **METHOD**: `POST`
- **기능**: 문제행동소비자이력 조회
- **마스킹 필드**:
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`

#### CNS3040001Dto - 고객정보조회 요청 DTO
- **컨트롤러**: `CNS3040Controller`
- **URL**: `/api/cns/CNS3040/r01`
- **METHOD**: `POST`
- **기능**: 고객정보조회
- **마스킹 필드**:
  - `rcno` (실명확인번호) → `RRN`
  - `csnm` (고객명) → `NAME`
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`

#### CNS3040002Dto - 고객정보조회 응답 DTO
- **컨트롤러**: `CNS3040Controller`
- **URL**: `/api/cns/CNS3040/r01`
- **METHOD**: `POST`
- **기능**: 고객정보조회
- **마스킹 필드**:
  - `csnm` (고객명) → `NAME`
  - `rcno` (실명확인번호) → `RRN`
  - `owhsTelno` (자택전화번호) → `PHONE_NUMBER`
  - `wrplTelno` (직장전화번호) → `PHONE_NUMBER`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`

#### CNS4010001Dto - 업무이관등록 요청 DTO
- **컨트롤러**: `CNS4010Controller`
- **URL**: `/api/cns/CNS4010/u01`
- **METHOD**: `POST`
- **기능**: 업무이관등록
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS4020003Dto - 업무이관 목록 DTO
- **컨트롤러**: `CNS4020Controller`
- **URL**: `/api/cns/CNS4020/r03`
- **METHOD**: `POST`
- **기능**: 업무이관 목록 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS5010001Dto - 고객상세조회 요청 DTO
- **컨트롤러**: `CNS5010Controller`
- **URL**: `/api/cns/CNS5010/r01`
- **METHOD**: `POST`
- **기능**: 고객상세조회
- **마스킹 필드**:
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`

#### CNS5010002Dto - 고객상세조회 응답 DTO
- **컨트롤러**: `CNS5010Controller`
- **URL**: `/api/cns/CNS5010/r01`
- **METHOD**: `POST`
- **기능**: 고객상세조회
- **마스킹 필드**:
  - `csnm` (고객명) → `NAME`
  - `owhsDtad` (자택상세주소) → `ADDRESS`
  - `wrplDtad` (직장상세주소) → `ADDRESS`
  - `emailAddr` (이메일주소) → `EMAIL`
  - `owhsTelno` (자택전화번호) → `PHONE_NUMBER`
  - `clphTelno` (휴대폰전화번호) → `PHONE_NUMBER`
  - `wrplTelno` (직장전화번호) → `PHONE_NUMBER`
  - `frcrAcno` (외화계좌번호) → `ACCOUNT_NUMBER`

#### CNS5050001Dto - 계좌목록조회 요청 DTO
- **컨트롤러**: `CNS5050Controller`
- **URL**: `/api/cns/CNS5050/r01`
- **METHOD**: `POST`
- **기능**: 계좌목록조회
- **마스킹 필드**:
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`

#### CNS5050002Dto - 계좌목록조회 응답 DTO
- **컨트롤러**: `CNS5050Controller`
- **URL**: `/api/cns/CNS5050/r01`
- **METHOD**: `POST`
- **기능**: 계좌목록조회
- **마스킹 필드**:
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`

#### CNS5050003Dto - 계좌목록조회 NEXT 응답 DTO
- **컨트롤러**: `CNS5050Controller`
- **URL**: `/api/cns/CNS5050/r01`
- **METHOD**: `POST`
- **기능**: 계좌목록조회 (다음 페이지)
- **마스킹 필드**:
  - `nextAcno` (다음계좌번호) → `ACCOUNT_NUMBER`

#### CNS5060003Dto - 캠페인정보 응답 DTO
- **컨트롤러**: `CNS5060Controller`
- **URL**: `/api/cns/CNS5060/r03`
- **METHOD**: `POST`
- **기능**: 캠페인정보 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `hcCustActno` (해피콜고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS5070002Dto - 상담저장 응답 DTO
- **컨트롤러**: `CNS5070Controller`
- **URL**: `/api/cns/CNS5070/u02`
- **METHOD**: `POST`
- **기능**: 상담저장
- **마스킹 필드**:
  - `dscsnTelNo` (상담전화번호) → `PHONE_NUMBER`
  - `custNm` (고객명) → `NAME`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`
  - `dscsnRsvtTelNo` (상담예약전화번호) → `PHONE_NUMBER`
  - `hcCustActno` (해피콜고객계좌번호) → `ACCOUNT_NUMBER`

#### CNS5070004Dto - 상담이력조회 응답 DTO
- **컨트롤러**: `CNS5070Controller`
- **URL**: `/api/cns/CNS5070/r04`
- **METHOD**: `POST`
- **기능**: 상담이력조회
- **마스킹 필드**:
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`
  - `csnm` (고객명) → `NAME`
  - `rsvcTelno` (예약콜전화번호) → `PHONE_NUMBER`
  - `hpclAcno` (해피콜계좌번호) → `ACCOUNT_NUMBER`

#### CNS5080001Dto - 상담이력조회 요청 DTO
- **컨트롤러**: `CNS5080Controller`
- **URL**: `/api/cns/CNS5080/r01`
- **METHOD**: `POST`
- **기능**: 상담이력조회
- **마스킹 필드**:
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`
  - `csnm` (고객명) → `NAME`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`

#### CNS5080002Dto - 상담이력조회 응답 DTO
- **컨트롤러**: `CNS5080Controller`
- **URL**: `/api/cns/CNS5080/r01`
- **METHOD**: `POST`
- **기능**: 상담이력조회
- **마스킹 필드**:
  - `csnm` (고객명) → `NAME`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`
  - `rsvcTelno` (예약콜전화번호) → `PHONE_NUMBER`
  - `rcno` (실명확인번호) → `RRN`
  - `hpclAcno` (해피콜계좌번호) → `ACCOUNT_NUMBER`

#### CNS5080004Dto - 상담이력조회 요청 DTO
- **컨트롤러**: `CNS5080Controller`
- **URL**: `/api/cns/CNS5080/r04`
- **METHOD**: `POST`
- **기능**: 상담이력조회
- **마스킹 필드**:
  - `acno` (계좌번호) → `ACCOUNT_NUMBER`
  - `csnm` (고객명) → `NAME`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`

#### CNS5080005Dto - 상담이력조회 응답 DTO
- **컨트롤러**: `CNS5080Controller`
- **URL**: `/api/cns/CNS5080/r05`
- **METHOD**: `POST`
- **기능**: 상담이력조회
- **마스킹 필드**:
  - `csnm` (고객명) → `NAME`
  - `wholTelno` (전체전화번호) → `PHONE_NUMBER`
  - `rsvcTelno` (예약콜전화번호) → `PHONE_NUMBER`
  - `rcno` (실명확인번호) → `RRN`
  - `hpclAcno` (해피콜계좌번호) → `ACCOUNT_NUMBER`

---

### 4. STS (Statistics) 패키지

#### STS0060001Dto - 상담목록조회 DTO
- **컨트롤러**: `STS0060Controller`
- **URL**: `/api/sts/STS0060/r01`
- **METHOD**: `POST`
- **기능**: 상담목록조회
- **마스킹 필드**:
  - `dscsnTelNo` (전화번호) → `PHONE_NUMBER`
  - `custActno` (계좌번호) → `ACCOUNT_NUMBER`
  - `custNm` (고객명) → `NAME`

#### STS0100002Dto - 유선상담이용고객 DTO
- **컨트롤러**: `STS0100Controller`
- **URL**: `/api/sts/STS0100/r02`
- **METHOD**: `POST`
- **기능**: 유선상담이용고객 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`

#### STS0110002Dto - 업무이관 응답 DTO
- **컨트롤러**: `STS0110Controller`
- **URL**: `/api/sts/STS0110/r02`
- **METHOD**: `POST`
- **기능**: 업무이관 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custTelNo` (고객전화번호) → `PHONE_NUMBER`
  - `custActno` (고객계좌번호) → `ACCOUNT_NUMBER`

#### STS1020002Dto - 캠페인 처리결과 응답 DTO
- **컨트롤러**: `STS1020Controller`
- **URL**: `/api/sts/STS1020/r01`, `/api/sts/STS1020/e01`
- **METHOD**: `POST`
- **기능**: 캠페인 처리결과 조회, 엑셀다운로드
- **마스킹 필드**:
  - `custNm` (사용자) → `NAME`

#### STS1030002Dto - 캠페인 처리결과 DTO
- **컨트롤러**: `STS1030Controller`
- **URL**: `/api/sts/STS1030/r01`
- **METHOD**: `POST`
- **기능**: 캠페인 처리결과 조회
- **마스킹 필드**:
  - `custNm` (고객명) → `NAME`
  - `custActno` (계좌번호) → `ACCOUNT_NUMBER`

---

## 주의사항

1. **마스킹 처리 범위**: 위 목록의 DTO들은 Controller에서 응답으로 반환되는 DTO들로, 개인정보 보호를 위해 마스킹 처리가 적용됩니다.

2. **시스템 사용자 정보**: `sys` 패키지의 DTO들은 시스템 사용자 정보를 다루므로 마스킹 대상에서 제외되었습니다.

3. **마스킹 적용 시점**: 마스킹은 응답 데이터가 클라이언트로 전송되기 전에 적용됩니다.

4. **개발 시 주의사항**: 새로운 DTO를 추가하거나 기존 DTO에 개인정보 필드를 추가할 때는 반드시 적절한 `@Masking` 어노테이션을 추가해야 합니다.

---

**문서 작성일**: 2025년 1월 9일  
**작성자**: AI Assistant  
**버전**: 1.0
