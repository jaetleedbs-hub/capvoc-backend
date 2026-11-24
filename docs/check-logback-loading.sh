#!/bin/bash
################################################################################
# Logback-spring.xml 로드 상태 진단 스크립트
################################################################################
# 사용법: bash check-logback-loading.sh
################################################################################

set +e  # 에러가 나도 계속 진행

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "   $1"
}

################################################################################
# 1. 환경 정보 수집
################################################################################

print_header "1. 환경 정보"

# CATALINA_HOME 확인
if [ -n "$CATALINA_HOME" ]; then
    print_success "CATALINA_HOME: $CATALINA_HOME"
    TOMCAT_HOME="$CATALINA_HOME"
else
    print_warning "CATALINA_HOME 환경변수 없음"
    # Tomcat 프로세스에서 추출 시도
    TOMCAT_PID=$(pgrep -f "catalina" | head -1)
    if [ -n "$TOMCAT_PID" ]; then
        TOMCAT_HOME=$(sudo cat /proc/$TOMCAT_PID/environ 2>/dev/null | tr '\0' '\n' | grep CATALINA_HOME | cut -d= -f2)
        if [ -n "$TOMCAT_HOME" ]; then
            print_info "프로세스에서 발견: $TOMCAT_HOME"
        else
            TOMCAT_HOME="/opt/tomcat"
            print_warning "기본값 사용: $TOMCAT_HOME"
        fi
    else
        TOMCAT_HOME="/opt/tomcat"
        print_warning "기본값 사용: $TOMCAT_HOME"
    fi
fi

# CATALINA_BASE 확인
if [ -n "$CATALINA_BASE" ]; then
    print_success "CATALINA_BASE: $CATALINA_BASE"
else
    print_warning "CATALINA_BASE 환경변수 없음 (CATALINA_HOME 사용)"
fi

# Spring Profile 확인
PROFILE=$(ps aux | grep tomcat | grep -o "spring.profiles.active=[^ ]*" | cut -d= -f2 | head -1)
if [ -n "$PROFILE" ]; then
    print_success "Spring Profile: $PROFILE"
else
    print_warning "활성 프로파일 확인 불가"
fi

################################################################################
# 2. WAR 파일 내 logback-spring.xml 확인
################################################################################

print_header "2. WAR 파일 내 설정 파일 확인"

WEBAPP_DIR="$TOMCAT_HOME/webapps/ROOT/WEB-INF/classes"

if [ -d "$WEBAPP_DIR" ]; then
    print_success "배포 디렉토리 존재: $WEBAPP_DIR"
    
    # logback-spring.xml 확인
    if [ -f "$WEBAPP_DIR/logback-spring.xml" ]; then
        print_success "logback-spring.xml 존재"
        print_info "크기: $(du -h $WEBAPP_DIR/logback-spring.xml | cut -f1)"
        print_info "수정일: $(stat -c %y $WEBAPP_DIR/logback-spring.xml 2>/dev/null || stat -f %Sm $WEBAPP_DIR/logback-spring.xml 2>/dev/null)"
    else
        print_error "logback-spring.xml 없음!"
        print_info "→ WAR 빌드 시 누락되었거나 배포 안 됨"
    fi
    
    # 충돌 파일 확인
    echo ""
    print_info "Logback 설정 파일 목록:"
    LOGBACK_FILES=$(find "$WEBAPP_DIR" -name "logback*.xml" 2>/dev/null)
    if [ -n "$LOGBACK_FILES" ]; then
        echo "$LOGBACK_FILES" | while read -r file; do
            basename_file=$(basename "$file")
            if [ "$basename_file" = "logback.xml" ]; then
                print_warning "  - $basename_file (logback-spring.xml보다 우선순위 높음!)"
            else
                print_info "  - $basename_file"
            fi
        done
    else
        print_error "  Logback 설정 파일 없음!"
    fi
else
    print_error "배포 디렉토리 없음: $WEBAPP_DIR"
    print_info "→ 애플리케이션이 배포되지 않았거나 경로가 다름"
fi

################################################################################
# 3. 로그 파일 생성 확인
################################################################################

print_header "3. 로그 파일 생성 확인"

LOG_DIR="$TOMCAT_HOME/logs"

if [ -d "$LOG_DIR" ]; then
    print_success "로그 디렉토리 존재: $LOG_DIR"
    
    # application.log 확인
    if [ -f "$LOG_DIR/application.log" ]; then
        FILE_SIZE=$(du -h "$LOG_DIR/application.log" | cut -f1)
        FILE_LINES=$(wc -l < "$LOG_DIR/application.log")
        print_success "application.log 존재 (크기: $FILE_SIZE, 줄: $FILE_LINES)"
        
        # 최근 로그 미리보기
        print_info "최근 로그 (마지막 3줄):"
        tail -3 "$LOG_DIR/application.log" | while read -r line; do
            print_info "  $line"
        done
    else
        print_error "application.log 없음!"
        print_info "→ logback-spring.xml이 로드되지 않았거나 FILE Appender 문제"
    fi
    
    echo ""
    
    # error.log 확인
    if [ -f "$LOG_DIR/error.log" ]; then
        FILE_SIZE=$(du -h "$LOG_DIR/error.log" | cut -f1)
        FILE_LINES=$(wc -l < "$LOG_DIR/error.log")
        print_success "error.log 존재 (크기: $FILE_SIZE, 줄: $FILE_LINES)"
    else
        print_warning "error.log 없음 (에러가 없으면 정상)"
    fi
    
    echo ""
    
    # 백업 파일 확인
    BACKUP_COUNT=$(find "$LOG_DIR" -name "application.log.*.gz" 2>/dev/null | wc -l)
    if [ "$BACKUP_COUNT" -gt 0 ]; then
        print_success "로그 롤링 작동 중 (백업 파일 $BACKUP_COUNT개)"
    else
        print_info "백업 파일 없음 (아직 롤링 안 됨 또는 최근 시작)"
    fi
else
    print_error "로그 디렉토리 없음: $LOG_DIR"
fi

################################################################################
# 4. Tomcat 로그에서 Logback 로드 메시지 확인
################################################################################

print_header "4. Tomcat 로그에서 Logback 로드 확인"

CATALINA_OUT="$TOMCAT_HOME/logs/catalina.out"

if [ -f "$CATALINA_OUT" ]; then
    print_success "catalina.out 존재"
    
    # Logback 로드 메시지 찾기
    LOGBACK_MSG=$(grep -i "logback\|LoggingSystem" "$CATALINA_OUT" | tail -5)
    
    if [ -n "$LOGBACK_MSG" ]; then
        print_success "Logback 관련 로그 발견:"
        echo "$LOGBACK_MSG" | while read -r line; do
            print_info "  $line"
        done
    else
        print_warning "Logback 관련 로그 없음"
        print_info "→ 디버그 모드가 꺼져있거나 로그가 오래됨"
    fi
    
    echo ""
    
    # 에러 메시지 확인
    ERROR_MSG=$(grep -i "error.*logback\|exception.*logback\|failed.*log" "$CATALINA_OUT" | tail -5)
    
    if [ -n "$ERROR_MSG" ]; then
        print_error "Logback 관련 에러 발견:"
        echo "$ERROR_MSG" | while read -r line; do
            print_error "  $line"
        done
    else
        print_success "Logback 관련 에러 없음"
    fi
else
    print_error "catalina.out 없음: $CATALINA_OUT"
fi

################################################################################
# 5. 권한 확인
################################################################################

print_header "5. 파일 권한 확인"

if [ -f "$WEBAPP_DIR/logback-spring.xml" ]; then
    PERMISSIONS=$(stat -c "%a %U:%G" "$WEBAPP_DIR/logback-spring.xml" 2>/dev/null || stat -f "%Lp %Su:%Sg" "$WEBAPP_DIR/logback-spring.xml" 2>/dev/null)
    print_info "logback-spring.xml 권한: $PERMISSIONS"
    
    # 읽기 권한 확인
    if [ -r "$WEBAPP_DIR/logback-spring.xml" ]; then
        print_success "읽기 권한 있음"
    else
        print_error "읽기 권한 없음!"
    fi
fi

if [ -d "$LOG_DIR" ]; then
    PERMISSIONS=$(stat -c "%a %U:%G" "$LOG_DIR" 2>/dev/null || stat -f "%Lp %Su:%Sg" "$LOG_DIR" 2>/dev/null)
    print_info "logs 디렉토리 권한: $PERMISSIONS"
    
    # 쓰기 권한 확인
    if [ -w "$LOG_DIR" ]; then
        print_success "쓰기 권한 있음"
    else
        print_error "쓰기 권한 없음! 로그 파일 생성 불가"
    fi
fi

################################################################################
# 6. 최종 진단
################################################################################

print_header "6. 최종 진단"

HAS_CONFIG=false
HAS_LOG=false
HAS_ERROR=false

# 설정 파일 존재 여부
if [ -f "$WEBAPP_DIR/logback-spring.xml" ]; then
    HAS_CONFIG=true
fi

# 로그 파일 생성 여부
if [ -f "$LOG_DIR/application.log" ]; then
    HAS_LOG=true
fi

# 에러 여부
if [ -n "$ERROR_MSG" ]; then
    HAS_ERROR=true
fi

echo ""

if [ "$HAS_CONFIG" = true ] && [ "$HAS_LOG" = true ] && [ "$HAS_ERROR" = false ]; then
    print_success "logback-spring.xml이 정상적으로 로드되고 작동 중입니다!"
    echo ""
    print_info "확인 사항:"
    print_info "  ✅ 설정 파일 존재"
    print_info "  ✅ 로그 파일 생성됨"
    print_info "  ✅ 에러 없음"
elif [ "$HAS_CONFIG" = false ]; then
    print_error "logback-spring.xml이 WAR 파일에 없습니다!"
    echo ""
    print_info "해결 방법:"
    print_info "  1. src/main/resources/logback-spring.xml 파일 확인"
    print_info "  2. ./gradlew clean bootWar 재빌드"
    print_info "  3. WAR 파일 재배포"
elif [ "$HAS_LOG" = false ]; then
    print_error "로그 파일이 생성되지 않았습니다!"
    echo ""
    print_info "원인 분석:"
    if [ "$HAS_ERROR" = true ]; then
        print_info "  → Logback 설정 에러 (위 에러 로그 확인)"
    else
        print_info "  → logback-spring.xml이 로드되지 않았거나"
        print_info "  → FILE Appender 설정 문제"
        print_info "  → 로그 디렉토리 권한 문제"
    fi
    echo ""
    print_info "해결 방법:"
    print_info "  1. catalina.out에서 상세 에러 확인"
    print_info "  2. logback-spring.xml에 debug=\"true\" 추가"
    print_info "  3. 로그 디렉토리 권한 확인: ls -ld $LOG_DIR"
else
    print_warning "부분적으로 작동 중이거나 확인 필요"
    echo ""
    print_info "상세 확인:"
    [ "$HAS_CONFIG" = true ] && print_info "  ✅ 설정 파일 존재" || print_info "  ❌ 설정 파일 없음"
    [ "$HAS_LOG" = true ] && print_info "  ✅ 로그 파일 생성" || print_info "  ❌ 로그 파일 없음"
    [ "$HAS_ERROR" = false ] && print_info "  ✅ 에러 없음" || print_info "  ⚠️  에러 있음"
fi

echo ""
print_header "진단 완료"
echo ""

