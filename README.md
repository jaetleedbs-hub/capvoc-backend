# CAPVOC 백엔드 (capvoc-backend)

회원사·사용자·권한·공지·발송 설정을 다루는 관리자 백엔드. Spring Boot · Gradle.

```
capvoc-backend/
├── src/main/java/buttle/
├── src/main/resources/
├── src/test/java/buttle/
└── build.gradle
```

---

## 처음 체크아웃했다면 — 환경설정

> 이 절은 **사람과 에이전트 모두** 이것만 보고 기동할 수 있게 쓰여 있다.
> 막히는 자리가 있으면 여기를 고친다 — 다음 사람이 같은 데서 막힌다.

### 1. `.env` 를 만든다

이 저장소는 **`.env` 없이는 기동하지 않는다.** 시크릿이 들어가므로 커밋되지 않고
(`.gitignore`), 체크아웃 직후에 없는 것이 정상이다.

```bash
cp .env.example .env
```

`.env.example` 이 **채워야 할 키 목록**이다. 각 키에는 「비워 두면 무엇이 안 되는가」가
주석으로 달려 있다.

### 2. 값을 얻는다

시크릿은 이 저장소에 없다. **비공개 저장소 하나가 정본**이고 거기서 받는다.

# ```bash
# git clone http://172.20.30.210:28083/chatcall-helpdesk/dev-env.git /tmp/dev-env
# cp /tmp/dev-env/capvoc-backend.env .env
# ```

# 권한이 없으면 `chatcall-helpdesk/dev-env` 의 **Reporter 이상**을 요청한다.
# **값을 채팅·메일·zip 으로 주고받지 않는다** — 한 번 파일로 나가면 회수할 수 없다.

# `docs` 저장소도 받았다면 `../../../docs/ops/scripts/sync-dev-env.sh` 가 전 서비스를 한 번에 처리한다.

| 종류 | |
|---|---|
| **개인 값** — `MAIL_OVERRIDE_TO` | 정본은 **일부러 비워 두었다.** 자기 주소를 직접 넣는다 |
| **발송 설정** — `SMS_DEFAULT_SNDR_TELNO` · `SMS_DEFAULT_SNDR_NM` | 사전등록된 발신번호여야 한다. 비우면 SMS 발송이 거절된다 |

> `JASYPT_ENCRYPTOR_PASSWORD` 는 **미설정 시 기동을 거부한다**(W2-3). 가장 먼저 채운다.
> `APPTPL_AES_KEY` 는 desk·connect·gateway 와 같은 값이어야 한다.

### 3. ★ 비워 두면 안 되는 것 — 개발용 안전망

| 키 | 비워 두면 |
|---|---|
| `MAIL_OVERRIDE_TO` | 값이 있으면 **원래 수신자를 버리고** 전부 이 주소로만 보낸다. 비워 두면 발송 화면 시험이 **DB 의 실제 사용자에게 그대로 나간다** |

시험 발송은 지정된 번호·주소로만 한다. **발송 API 를 상태 확인용으로 부르지 않는다** —
GET 이어도 실제로 나간다.

### 4. 기동한다

```bash
# JDK 17 이어야 한다. 이 PC 의 기본은 JDK 25 인데 gradle 8.10 이 지원하지 않는다
export JAVA_HOME="<JDK 17 경로>"

./gradlew bootRun          # :28030 · 프로파일 local (application.yml 기본값)
```

`.env` 는 `build.gradle` 의 로더가 `bootRun` 환경변수로 자동 주입한다.

> **`/actuator/health` 가 `DOWN` 인 것은 기동 실패가 아니다.** 로컬에서 메일 비밀번호를
> 안 채우면 `MailHealthIndicator` 가 DOWN 을 만들 뿐, 애플리케이션은 정상이다.

### 5. 값이 맞는지 확인한다

```bash
./gradlew test
```

`.env.example` 이 코드보다 낡으면 **`EnvExampleCompletenessTest` 가 빌드를 실패시킨다.**

> **새 환경변수를 읽기 시작했다면 `.env.example` 에도 추가한다.** 잊어도 시험이 잡는다.

### 짝이 되는 것

| | 포트 | 비고 |
|---|---|---|
| capvoc-frontend | 18031 | `vite` 로 띄운다. **`--port` 를 붙이지 않는다** — `--port 18030` 을 주면 백엔드 포트를 뺏고, 그 vite 의 `/api` 프록시가 자기 자신을 가리켜 **500** 이 된다 |
| capvoc-backend | 18080 | capvoc 대시보드가 공지사항을 여기서 가져온다. admin 이 없으면 *"시스템 공지사항 목록 조회에 실패했습니다"* 로 깨진다 |

---

## 빌드·배포

```bash
./gradlew build      # 빌드
./gradlew bootWar    # 배포 산출물
```

---

## 규약

착수 전에 **`docs/CLAUDE.md`** 를 읽는다. 여기에 복사하지 않는다.
