package com.sif.community.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sif.community.dao.AdminDao;
import com.sif.community.dao.AuthoritiesDao;
import com.sif.community.dao.UserDao;
import com.sif.community.model.AuthorityVO;
import com.sif.community.model.UserDetailsVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminService {
	
	private final UserDao userDao;
	private final AdminDao adminDao;
	private final AuthoritiesDao authDao;
	
	// 관리자가 다른 유저 정보 수정하기
	// 유저 정보 form과 권한 정보 form 값을 가져와서
	// 유저 정보는 유저 테이블에, 권한은 권한 테이블에 저장하기
	@Transactional
	public int update_user_from_admin(UserDetailsVO userVO, String[] arrAuth) {
		UserDetailsVO dbUserVO = userDao.findByUsername(userVO.getUsername());
		
		// 기존의 유저정보에 form에서 입력받은 정보 새로 세팅하기
		// 계정활성여부, 닉네임, 이메일, 핸드폰, 생년, 생월, 생일
		dbUserVO.setEnabled(userVO.isEnabled());
		dbUserVO.setNickname(userVO.getNickname());
		dbUserVO.setEmail(userVO.getEmail());
		dbUserVO.setPhone(userVO.getPhone());
		
		dbUserVO.setYear(userVO.getYear());
		dbUserVO.setMonth(userVO.getMonth());
		dbUserVO.setDay(userVO.getDay());
		
		int ret = adminDao.update_user_from_admin(dbUserVO);
		
		if(ret > 0) {
			List<AuthorityVO> authList = new ArrayList<AuthorityVO>();
			for(String auth : arrAuth) {
				//input에서 받은 auth 값이 비어있으면(="") 무시함 
				if(!auth.isEmpty()) {
					AuthorityVO authVO = AuthorityVO.builder()
										.username(dbUserVO.getUsername())
										.authority(auth)
										.build();
					
					authList.add(authVO);
				}
			}
			authDao.delete(dbUserVO.getUsername());
			authDao.insert(authList);
		}
		return ret;
	}

}
