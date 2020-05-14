package com.sif.community.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.sif.community.dao.UserDao;
import com.sif.community.model.UserDetailsVO;
import com.sif.util.PbeEncryptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailSendService {
	
	private final JavaMailSender javaMailSender;
	private final UserDao userDao;
	
	public MailSendService(@Qualifier("gMailHandler") JavaMailSender javaMailSender, UserDao userDao) {
		super();
		this.javaMailSender = javaMailSender;
		this.userDao = userDao;
	}
	
	public boolean sendMail(String to_email, String subject, String content) {
		boolean ret = false;
		
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper mHelper = new MimeMessageHelper(message, "UTF-8");
		
		try {
			mHelper.setTo(to_email);
			mHelper.setSubject(subject);
			// setText의 2번째 매개변수 true : HTML 태그 적용
			mHelper.setText(content, true);
			
			javaMailSender.send(message);
			ret = true;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 회원가입 한 사용자에게 인증 email 발송
	 * username을 암호화시켜서 인증을 수행할 수 있는 링크를 email 본문에 작성하여 전송
	 * @param userVO
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public boolean send_join_auth_link(UserDetailsVO userVO) throws UnsupportedEncodingException {
		
		String username = userVO.getUsername();
		String email = userVO.getEmail();
		
		// 아이디, 이메일 암호화
		String encUsername = PbeEncryptor.encrypt(username);
		String encEmail = PbeEncryptor.encrypt(email);

		StringBuilder email_message = new StringBuilder();
		email_message.append("<p>회원가입을 환영합니다!<p>");
		email_message.append("<p>하단의 이메일 인증 링크를 클릭하세요.</p>");
		email_message.append("<a href='%s'>인증 링크</a>");

		// JASYPT를 사용하여 username과 email을 암호화하면 슬래시(/)가 포함되어있어 URL에 사용하기 힘들다
		// 따라서 URL을 통해 정상적으로 보낼 수 있도록 암호화된 문자열을 URLEncoder.encode() 등을 통해 인코딩하여 보낸다
		StringBuilder email_link = new StringBuilder();
		email_link.append("http://localhost:8080");
		email_link.append("/join/email-auth");
		email_link.append("?username=" + URLEncoder.encode(encUsername, "UTF-8"));
		email_link.append("&email=" + URLEncoder.encode(encEmail, "UTF-8"));
		
		String send_message = String.format(email_message.toString(), email_link.toString());
		
		String to_email = email;
		String subject = "회원가입 인증메일";
		String content = send_message;
		
		boolean ret = this.sendMail(to_email, subject, content);
		
		return ret;
	}

	public boolean send_new_pw_link(String username) throws UnsupportedEncodingException {
		UserDetailsVO userVO = userDao.findByUsername(username);
		String enc_username = PbeEncryptor.encrypt(userVO.getUsername());
		
		String email_content =
				"<p>OOO 사이트에서 비밀번호 재설정을 요청했습니다.</p>"
				+ "<p>본인이 맞으면 하단의 비밀번호 재설정 링크를 클릭하세요.</p>"
				+ "<a href='localhost:8080/sec/user/new-pw?link="
				+ URLEncoder.encode(enc_username, "UTF-8")
				+ "'>"
				+ "http://localhost:8080/sec/user/new-pw?link=" + URLEncoder.encode(enc_username, "UTF-8")
				+ "</a>"
				;
		
		String subject = "비밀번호 재설정 메일";
		return this.sendMail(userVO.getEmail(), subject, email_content);
	}

	public boolean send_auth_code(String to_email, String email_token) {
		
		StringBuilder email_content = new StringBuilder();
		email_content.append("<p>회원가입을 완료하려면 인증코드를 입력하세요.</p>");
		email_content.append("<p>인증코드 : " + email_token + "</p>");
		
		String subject = "이메일 변경 인증코드";
		
		boolean ret = this.sendMail(to_email, subject, email_content.toString());
		return ret;
	}

}
