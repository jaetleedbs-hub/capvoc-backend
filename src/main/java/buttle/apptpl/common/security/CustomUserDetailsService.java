package buttle.apptpl.common.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import buttle.apptpl.common.framework.common.vo.CustomUserDetails;
import buttle.apptpl.common.exception.CustomAuthenticationException;
import buttle.apptpl.common.model.cmpt.entity.CmptUserEntity;
import buttle.apptpl.common.model.cmpt.repository.CmptUserRepo;

/**
 * 로그인할 때, Spring security가 사용한다.
 * 사용자 정보를 조회하고, Spring security가 이를 이용해
 * 아이디/비번을 비교 판단한다.
 *
 * @author patrick
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

	private final CmptUserRepo cmptUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<CmptUserEntity> optionalUserEntity = cmptUserRepo.findById(username);
		if (optionalUserEntity.isEmpty()) {
			throw new UsernameNotFoundException("존재하지 않는 사용자입니다");
		}

		CmptUserEntity userEntity = optionalUserEntity.get();
		if( !"Y".equals(userEntity.getUseYn()) ) {
			throw new CustomAuthenticationException("사용할 수 없는 사용자입니다.");
		}

		
    	List<GrantedAuthority> authorityList = new ArrayList<>();
    	authorityList.add(new SimpleGrantedAuthority(userEntity.getAuthrtCd()));

    	return CustomUserDetails.builder()
			.username(userEntity.getUserId())
			.password(userEntity.getPswd())
			.authorities(authorityList)
			.userEntity(userEntity)
			.enabled(true)
			.build();
    }

    /**
     * 로그인 성공 시 레거시 해시를 BCrypt로 교체 저장한다 [W2-2].
     *
     * <p>{@code DaoAuthenticationProvider}가 인증 성공 후 {@code upgradeEncoding()}이 true일 때만
     * 호출한다. 즉 <b>비밀번호가 실제로 일치한 경우에만</b> 실행되므로 여기서 원문을 재검증하지 않는다.
     */
    @Override
    @Transactional
    public UserDetails updatePassword(UserDetails user, String newEncodedPassword) {
        cmptUserRepo.findById(user.getUsername()).ifPresent(entity -> {
            entity.setPswd(newEncodedPassword);
            cmptUserRepo.save(entity);
            log.info("레거시 비밀번호를 BCrypt로 이행했습니다 - userId: {}", user.getUsername());
        });

        if (!(user instanceof CustomUserDetails details)) {
            return user;
        }

        // [W2-2] 중요: principal 이 들고 있는 엔티티의 비밀번호도 함께 갱신한다.
        //
        // LoginSuccessHandler 는 인증 직후 userDetails.getUserEntity() 를 꺼내 lgn_ip 등을 세팅하고,
        // 그 엔티티가 다시 저장된다. 이때 엔티티에 남아 있던 <b>옛 해시</b>가 그대로 실려 나가
        // 방금 이행한 BCrypt 해시를 덮어쓴다(실측: 이행 UPDATE 200ms 뒤 SHA-512 로 되돌아감).
        // 따라서 DB 저장만으로는 부족하고, 메모리상의 엔티티도 새 해시로 맞춰야 이행이 유지된다.
        CmptUserEntity carriedEntity = details.getUserEntity();
        if (carriedEntity != null) {
            carriedEntity.setPswd(newEncodedPassword);
        }

        return CustomUserDetails.builder()
            .username(details.getUsername())
            .password(newEncodedPassword)
            .authorities(new ArrayList<>(details.getAuthorities()))
            .userEntity(carriedEntity)
            .enabled(true)
            .build();
    }
}

