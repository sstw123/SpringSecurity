package com.sif.community.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sif.community.dao.AuthoritiesDao;
import com.sif.community.dao.UserDao;
import com.sif.community.model.AuthorityVO;
import com.sif.community.model.UserDetailsVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserDao userDao;
	private final AuthoritiesDao authDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserDetailsVO userDetailsVO = userDao.findByUsername(username);
		if(userDetailsVO == null) {
			return null;
		}
		
		userDetailsVO.setAuthorities(this.getAuthoritiesCS(username));
		
		return userDetailsVO;
	}
	
	private Collection<GrantedAuthority> getAuthoritiesCS(String username) {
		List<AuthorityVO> authList = authDao.findByUsername(username);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for(AuthorityVO vo : authList) {
			SimpleGrantedAuthority sAuth = new SimpleGrantedAuthority(vo.getAuthority());
			authorities.add(sAuth);
		}
		
		return authorities;
	}
	
}
