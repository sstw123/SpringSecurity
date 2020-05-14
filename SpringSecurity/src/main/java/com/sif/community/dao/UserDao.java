package com.sif.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sif.community.model.UserDetailsVO;

public interface UserDao {
	
	public UserDetailsVO findByUsernameFromAuthorities(String username);

	public List<UserDetailsVO> selectAll();
	public UserDetailsVO findById(long id);
	public UserDetailsVO findByUsername(String username);
	@Select("SELECT username FROM tbl_users WHERE email = #{email}")
	public List<UserDetailsVO> findByEmail(String email);
	
	public void create_table(String create_table);
	
	public int insert(UserDetailsVO userVO);
	public int update_user(UserDetailsVO userVO);
	public int update_email(UserDetailsVO userVO);
	public int update_pw(UserDetailsVO userVO);
	
	

}
