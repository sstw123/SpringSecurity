package com.sif.community.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDetailsVO implements UserDetails{
	
	private static final long serialVersionUID = -2326256706527714266L;
	
	private long id;
	private String username;
	private String password;
	private boolean enabled;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	
	private String nickname;
	private String email;
	private String phone;
	
	private String year;
	private String month;
	private String day;

}
