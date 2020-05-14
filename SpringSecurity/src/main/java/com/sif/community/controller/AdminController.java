package com.sif.community.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sif.community.model.UserDetailsVO;
import com.sif.community.service.AdminService;
import com.sif.community.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value="/admin")
@Controller
public class AdminController {
	
	private final UserService userSvc;
	private final AdminService adminSvc;
	
	//@ResponseBody
	@RequestMapping(value="", method=RequestMethod.GET)
	public String admin() {
		return "admin/admin_main";
	}
	
	@RequestMapping(value="/userlist", method=RequestMethod.GET)
	public String user_list(Model model) {
		List<UserDetailsVO> userList = userSvc.selectAll();
		
		model.addAttribute("USER_LIST", userList);
		return "admin/user_list";
	}
	
	@RequestMapping(value="/user_detail/{username}", method=RequestMethod.GET)
	public String user_detail_view(@PathVariable("username")String username, Model model) {
		UserDetailsVO userVO = userSvc.findByUsername(username);
		model.addAttribute("USER_VO", userVO);
		return "admin/user_detail";
	}
	
	@RequestMapping(value="/user_detail", method=RequestMethod.POST)
	public String mypage(UserDetailsVO userVO, String[] auth, Model model) {
		System.out.println(userVO.toString());
		int ret = adminSvc.update_user_from_admin(userVO, auth);
		
		return "redirect:/admin/user_detail/" + userVO.getUsername();
	}

}
