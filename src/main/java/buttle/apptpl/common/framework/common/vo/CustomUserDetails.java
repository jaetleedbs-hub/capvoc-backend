package buttle.apptpl.common.framework.common.vo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

	/**
	 *
	 */
	private static final long serialVersionUID = 5851826667744820831L;
	private String username;
	private String password;
	private List<GrantedAuthority> authorities;
	private boolean enabled = true;
	private CmptUserEntity userEntity;
	private SecUser secUser;

	// public CustomUserDetails() {

	// }

	// public CustomUserDetails(String username, String password, boolean enabled, Collection<GrantedAuthority> authorities, SecUser secUser) {
	// 	this.username = username;
	// 	this.password = password;
	// 	this.enabled = enabled;
	// 	this.secUser = secUser;
	// 	this.authorities = new ArrayList<>();
	// 	for (GrantedAuthority authority : authorities) {
	// 		this.authorities.add(authority);
	// 	}
	// 	this.secUser.setRoles(authorities);
	// }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}


	// ── 아래 세 플래그가 항상 true 인 것은 <구멍이 아니라 설계>다 ──────────────────
	//
	//  계정 상태 판정은 이 VO 가 아니라 CustomUserDetailsChecker 가 한다.
	//  SecurityConfig:255 가 DaoAuthenticationProvider.setPostAuthenticationChecks 로
	//  그 체커를 물려 두었고, 체커는 만료·휴면(3개월)·잠금·초기비밀번호·비밀번호만료·
	//  중복로그인을 <전부> 검사해 CustomAuthenticationException 을 던진다.
	//
	//  ★ 왜 여기에 적어 두는가 — 이 세 줄만 보면 「잠금이 꺼져 있다」로 읽힌다.
	//    2026-09-05 에 실제로 두 번 그렇게 읽혔고, 「배선하자」는 제안까지 나왔다.
	//    배선하면 판정이 두 곳이 되고, 그 둘이 갈리는 날 어느 쪽이 이기는지 알 수 없게 된다.
	//
	//  ⚠ 배선한다면 <부호>를 반드시 뒤집어야 한다. LoginAttemptUtils 쪽은
	//    isAccountLocked(=잠기면 true)이고 여기 계약은 isAccountNonLocked(=잠기지 않으면 true)로
	//    정반대다. 그대로 이으면 잠긴 계정만 통과한다.
	//    (그 함정 때문에 LoginAttemptUtils 의 옛 이름 isAccountNonLocked 를 개명했다.)

	@Override
	public boolean isAccountNonExpired() {
		return true;   // 실판정: CustomUserDetailsChecker → LoginAttemptUtils.isAccountExpired
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;   // 실판정: CustomUserDetailsChecker → LoginAttemptUtils.isAccountLocked
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;   // 실판정: CustomUserDetailsChecker → LoginAttemptUtils.isCredentialsExpired
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
