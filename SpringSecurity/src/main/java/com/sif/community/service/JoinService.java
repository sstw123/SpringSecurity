package com.sif.community.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sif.community.dao.AuthoritiesDao;
import com.sif.community.dao.UserDao;
import com.sif.community.model.AuthorityVO;
import com.sif.community.model.UserDetailsVO;
import com.sif.util.PbeEncryptor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JoinService {
	
	private final UserDao userDao;
	private final AuthoritiesDao authDao;
	private final MailSendService mailSvc;
	private final UserService userSvc;
	private final PasswordEncoder bcryptEncoder;
	
	/**
	 * @since 2020-05-11
	 * @author me
	 * email 인증이 되지 않은 상태로 DB에 저장하기
	 * 1. userVO를 파라미터로 받아서 비밀번호 암호화 후 유저 테이블에 저장
	 * 2. authorities 테이블에 ROLE_UNAUTH로 저장
	 * 이후 이메일 인증 링크를 클릭하면 role 정보를 ROLE_USER로 설정한다
	 * @param userVO
	 * @param day 
	 * @param month 
	 * @param year 
	 * @return
	 */
	// 두 개 이상의 쿼리 => @Transactional
	@Transactional
	public int insert(UserDetailsVO userVO, int year, int month, int day) {
		// 이미 DB에 있는 아이디면 아무 일도 하지 않음
		if(userSvc.findByUsername(userVO.getUsername()) != null) {
			return 0;
		}
		
		// 이메일 인증 전, 활성화 되지 않은 사용자로 세팅하기
		userVO.setEnabled(true);
		// 비밀번호 암호화하기
		String encPW = bcryptEncoder.encode(userVO.getPassword());
		userVO.setPassword(encPW);
		
		int ret = userDao.insert(userVO);
		
		List<AuthorityVO> authList = new ArrayList<>();
		authList.add(AuthorityVO.builder().username(userVO.getUsername()).authority("ROLE_UNAUTH").build());
		authDao.insert(authList);
		
		// 인증메일 발송
		try {
			mailSvc.send_join_auth_link(userVO);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = 0;
		}
		
		return ret;
	}
	
	// 회원가입 이메일 링크 클릭 시 인증 메소드
	@Transactional
	public byte email_link_auth(String username, String email) {
		byte ret = 0;
		// 아이디, 이메일 복호화
		String plainUsername = PbeEncryptor.decrypt(username);
		String plainEmail = PbeEncryptor.decrypt(email);
		
		UserDetailsVO userVO = userDao.findByUsername(plainUsername);
		
		// 1. DB의 유저 테이블에 username이 없으면 아무 일도 하지 않음
		if(userVO == null) {
			ret = 2;
		} else if(authDao.findByUsername(plainUsername).stream()
				.filter(o -> o.getAuthority().equals("ROLE_UNAUTH")).findFirst().isPresent()) {
			// 2. DB의 유저 테이블에 username이 있고, 해당 유저의 권한 테이블에 ROLE_UNAUTH가 있는 경우
			// 2-1. 링크의 이메일 복호화 값 == DB 유저 테이블의 이메일 값이면 권한 업데이트
			if(plainEmail.equalsIgnoreCase(userVO.getEmail())) {
				// 현재 권한 테이블에서 username으로 검색한 튜플 전부 삭제 후 ROLE_USER 권한 새로 추가
				authDao.delete(userVO.getUsername());
				List<AuthorityVO> authList = new ArrayList<>();
				authList.add(AuthorityVO.builder().username(userVO.getUsername()).authority("ROLE_USER").build());
				authDao.insert(authList);
				ret = 1;
			} else {
				// 2-2. 링크의 이메일 복호화 값 != DB 유저 테이블의 이메일 값이면 잘못된 요청 페이지로 보내기
				ret = 3;
			}
		} else {
			// 3. DB의 유저 테이블에 username이 있지만 권한 중 ROLE_UNAUTH가 없는 경우 아무 일도 하지 않음
			ret = 4;
		}
		
		return ret;
	}

}
