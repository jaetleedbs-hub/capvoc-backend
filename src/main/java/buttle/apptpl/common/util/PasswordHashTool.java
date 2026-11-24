package buttle.apptpl.common.util;

import buttle.apptpl.common.security.crypto.CustomPasswordEncoder;

/**
 * 비밀번호 해시를 만드는 명령행 도구.
 *
 * <pre>{@code
 *   ./gradlew bcrypt -Ptext='초기비밀번호'
 * }</pre>
 *
 * <p><b>언제 쓰나.</b>
 * <ul>
 *   <li>시드 SQL 에 넣을 초기 관리자 계정의 해시를 만들 때</li>
 *   <li>화면으로 들어갈 수 없는 상태에서 계정 비밀번호를 되돌려야 할 때
 *       (그 경우 {@code cdm_own.usr.pswd} 를 이 값으로 UPDATE 하고
 *        {@code lgn_try_rcn = 0}, {@code pswd_chg_dttm} 도 함께 손본다)</li>
 * </ul>
 *
 * <p>★ 같은 비밀번호를 넣어도 <b>매번 다른 값</b>이 나온다(솔트). 정상이다 —
 * 두 번 돌려 다르다고 해서 잘못된 것이 아니다.
 *
 * <p>⛔ 만들어진 해시를 <b>여러 환경에 돌려 쓰지 않는다.</b> 그러면 한 환경의 비밀번호를
 * 아는 사람이 모든 환경의 관리자가 된다. 환경마다 새로 만든다.
 */
public final class PasswordHashTool {

    private PasswordHashTool() {
    }

    public static void main(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            System.err.println("사용법: ./gradlew bcrypt -Ptext='<비밀번호>'");
            System.exit(1);
        }

        int strength = args.length >= 2 ? Integer.parseInt(args[1]) : 10;
        System.out.println(new CustomPasswordEncoder(strength).encode(args[0]));
    }
}
