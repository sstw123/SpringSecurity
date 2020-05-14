package com.sif.community.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.sif.community.model.AuthorityVO;

public interface AuthoritiesDao {
	
	// 아이디를 기준으로 권한 SELECT
	@Select("SELECT * FROM authorities WHERE username = #{username}")
	public List<AuthorityVO> findByUsername(String username);
	
	// 권한 테이블 INSERT
	public int insert(List<AuthorityVO> authList);
	
	// 아이디를 기준으로 권한 전부 삭제
	@Delete("DELETE FROM authorities WHERE username = #{username}")
	public int delete(String username);

}
