package com.sif.community.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

import com.sif.community.model.UserDetailsVO;
import com.sif.community.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value="/user")
@Controller
public class UserController {
	
	private final UserService userSvc;
	
	@RequestMapping(value = "/login", method=RequestMethod.GET)
	public String login() {
		return "auth/login";
	}
	
	@RequestMapping(value = "/mypage", method=RequestMethod.GET)
	public String mypage(Model model) {
		// 현재 SecurityContextHolder의 아이디,비밀번호 인증 토큰에서 Principal(유저정보), Authorities(권한) 가져오기
		UsernamePasswordAuthenticationToken upaToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		UserDetailsVO loginVO = (UserDetailsVO) upaToken.getPrincipal();
		loginVO.setAuthorities(upaToken.getAuthorities());
		
		model.addAttribute("loginVO", loginVO);
		
		return "user/mypage";
	}
	
	/**
	 * mypage에서 정보 수정 후 저장 시 form에 입력된 데이터가 userVO에 담겨서 온다
	 * @param userVO
	 * @return
	 */
	@RequestMapping(value = "/mypage", method=RequestMethod.POST)
	public String mypage(UserDetailsVO userVO) {
		/*
		 * 아래 코드는 Security Session 정보가 저장된 메모리(Principal)에 직접 접근하여 수정하는 방법이다
		 * 코드는 쉬워지지만 보안에 굉장히 위험할 수 있으므로 principal을 수정하는 방법은 사용하지 않는다
		// 현재 로그인 된 사용자 정보 VO로 가져오기
		UsernamePasswordAuthenticationToken upaToken = (UsernamePasswordAuthenticationToken) principal;
		UserDetailsVO loginVO = (UserDetailsVO) upaToken.getPrincipal();
		
		// form에서 입력받은 값 저장 후 update 메소드로 VO 보내기
		loginVO.setNickname(userVO.getNickname());
		loginVO.setEmail(userVO.getEmail());
		loginVO.setPhone(userVO.getPhone());
		loginVO.setYear(userVO.getYear());
		loginVO.setMonth(userVO.getMonth());
		loginVO.setDay(userVO.getDay());
		*/
		
		// 유저 정보는 수정될 때마다 SecurityContextHolder의 토큰을 갱신해주어야 한다
		int ret= userSvc.update_user(userVO);
		
		return "redirect:/user/mypage";
	}
	
	// mypage 비밀번호 변경 시 먼저 비밀번호 검사 화면 보여주기
	@RequestMapping(value="/check-pw", method=RequestMethod.GET)
	public String check_pw() {
		return "user/check_pw";
	}
	
	// 비밀번호 검사
	@RequestMapping(value="/check-pw", method=RequestMethod.POST)
	public String check_pw(String password) {
		boolean ret = userSvc.check_pw(password);
		
		String page = "";
		if(ret) {
			page = "user/change_pw";
		} else {
			page = "user/check_pw_fail";
		}
		return page;
	}
	
	// 새로운 비밀번호로 변경
	@ResponseBody
	@RequestMapping(value="/change-pw", method=RequestMethod.POST)
	public boolean change_pw(@RequestParam(value = "password", required = false)String password, @RequestParam(value = "re_password", required = false)String re_password) {
		userSvc.change_pw(password, re_password);
		return true;
	}
	
	// 이메일 변경 인증코드 발송
	@ResponseBody
	@RequestMapping(value="/change-email", method=RequestMethod.POST)
	public String change_email(HttpSession httpSession, @RequestParam(value = "email", required = false)String email) {
		httpSession.setAttribute("email", email);
		String ret = userSvc.change_email_step1(email);
		return ret;
	}
	
	// 이메일 인증코드 검증
	@ResponseBody
	@RequestMapping(value="/change-email-auth", method=RequestMethod.POST)
	public byte change_email_auth(@RequestParam(value = "enc_auth_code", required = false)String enc_auth_code,
									@RequestParam(value = "auth_code", required = false)String auth_code,
									HttpSession httpSession,
									SessionStatus status) {
		
		String email = httpSession.getAttribute("email").toString();
		byte ret = userSvc.change_email_step2(enc_auth_code, auth_code, email);
		
		return ret;
	}
	
	// 아이디 찾기 화면
	@RequestMapping(value="/find-id", method=RequestMethod.GET)
	public String find_my_ids() {
		return "user/find_id_pw";
	}
	
	// 아이디 찾기
	// ajax로 요청할 메소드
	@ResponseBody
	@RequestMapping(value="/find-id", method=RequestMethod.POST)
	public List<String> find_my_ids(UserDetailsVO userVO) {
		List<UserDetailsVO> userList = userSvc.find_my_ids(userVO);
		List<String> usernameList = null;
		if(userList == null || userList.size() < 1) {
			// 이메일로 검색한 결과가 없으면 null 반환
		} else {
			// 이메일로 검색한 결과가 있으면 usernameList에 아이디 저장
			usernameList = new ArrayList<>();
			
			for(UserDetailsVO vo : userList) {
				String username = vo.getUsername();
				String first = "";
				String after = "";
				
				// 앞의 2글자만 보이고 남은 글자 * 만들기
				// 아이디가 2글자 이하인 경우 다 보여주기
				if(username.length() <= 1) {
					first = username;
				} else if(username.length() > 1){
					first = username.substring(0,2);
					for(int i = 0 ; i < vo.getUsername().substring(2).length() ; i++) {
						after += "*";
					} 
				}
				usernameList.add(first + after);
			}
		}
		return usernameList;
	}
	
	// 비밀번호 찾기
	// ajax로 요청할 메소드
	@ResponseBody
	@RequestMapping(value="/find-pw", method=RequestMethod.POST)
	public byte find_my_pw(UserDetailsVO userVO) {
		// userVO에는 username, password만 담겨있다
		byte ret = userSvc.find_my_pw(userVO);
		return ret;
	}
	
	@RequestMapping(value="/new-pw", method=RequestMethod.GET)
	public String new_pw(@RequestParam(value = "link", required = false) String enc_username) {
		return "user/new_pw";
	}
	
	@RequestMapping(value="/new-pw", method=RequestMethod.POST)
	public String new_pw(@RequestParam(value = "link", required = false) String enc_username, String password, String re_password) {
		// userVO에는 암호화 된 username, password만 담겨있다
		int ret = userSvc.new_pw(enc_username, password, re_password);
		return "redirect:/";
	}
	
}
