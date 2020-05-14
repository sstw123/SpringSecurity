package com.sif.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sif.community.dao.AuthoritiesDao;
import com.sif.community.model.AuthorityVO;
import com.sif.community.model.UserDetailsVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationProviderImpl implements AuthenticationProvider {
	
	// UserDetailsService는 반드시 @Qualifier를 이용해 선언해주어야 한다
	@Autowired
	@Qualifier("userDetailsServiceImpl")
	private UserDetailsService userDetailsSvc;
	
	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Autowired
	private AuthoritiesDao authDao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// spring에서 로그인 페이지로 설정한 페이지의 form에서 전송한 메시지는 authentication에 저장되어 여기로 온다
		
		// authentication으로부터 로그인 폼에서 보낸 username과 password 추출
		// 아이디 = principal, 비밀번호 = credentials에 담겨져서 온다
		String username = authentication.getPrincipal().toString();
		String password = authentication.getCredentials().toString();
		
		// Service -> Dao를 통해서 DB로부터 사용자 정보 가져오기
		UserDetailsVO userDetails = (UserDetailsVO) userDetailsSvc.loadUserByUsername(username);
		if(userDetails == null) {
			throw new UsernameNotFoundException("등록되지 않은 사용자입니다.");
		}
		
		if( !bcryptEncoder.matches(password, userDetails.getPassword()) ) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}
		
		/*
		// 사용자 권한이 ROLE_UNAUTH 상태라면
		if(authDao.findByUsername(username).stream().filter(o -> o.getAuthority().equals("ROLE_UNAUTH")).findFirst().isPresent()) {
			throw new InsufficientAuthenticationException("이메일 인증을 완료해야 합니다.");
		}
		*/
		
		// UserDetailsService에서 보내준 사용자 정보를 Controller로 보내기
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		return token;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}
	
}
