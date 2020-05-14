<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<c:set var="rootPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/include_head.jspf" %>
	<style>
		.forms {
			width: 80%;
			display: flex;
			justify-content: center;
			margin: 0 auto;
			margin-top: 50px;
		}
		h3 {
			text-align: center;
		}
		.form_item p {
			padding: 0.5rem 0;
		}
		.result-id {
			display: none;
			padding: 10px;
			background-color: rgba(0,0,0,0.3)
		}
		.result-header {
			text-align: center;
		}
		.forms form {
			padding: 0 50px;
		}
		.form_item {
			margin-bottom: 10px;
		}
		.form_item label {
			color: var(--label-text-color);
			font-weight: 700;
		}
		.form_item input {
			box-sizing: border-box;
			display: block;
			width: 100%;
			background-color: var(--input-bg-color);
			/* border: var(--border-width-input) solid var(--border-color-input); */
			border: none;
			line-height: 1.5;
		}
		.btn_box {
			display: flex;
			justify-content: center;
		}
		.btn_box button {
			width: 100%;
			margin-top: 20px;
			padding: 10px;
			border: none;
			background-color: var(--button-bg-color);
			color: var(--button-color);
			cursor: pointer;
		}
		.btn_box button:hover {
			background-color: var(--button-hover-bg-color);
		}
		.forms form:first-child {
			border-right: 2px solid gray;
		}
	</style>
	<script>
		$(function() {
			let enable_btn_find_id = true
			let enable_btn_find_pw = true
			
			function isEmail(email) {
				let regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{1,6})+$/
				return regex.test(email)
			}
			
			// ----------------------------------------------------------
			
			$("#find_id_form").submit(function(e) {
				e.preventDefault()
			})
			
			$(document).on("click", "#btn-find-id", function(e) {
				if(!enable_btn_find_id) return false
				
				let email = $("#id_email")
				
				if(email.val() == "") {
					alert("이메일을 입력하세요.")
					email.focus()
					$(".result-id").css("display", "none")
					return false
				} else if( !isEmail(email.val()) ) {
					alert("이메일을 정확히 입력하세요.")
					email.focus()
					$(".result-id").css("display", "none")
					return false
				}
				
				// 유효성 검사 통과 시
				// 서버 부하를 줄이기 위해 ajax 완료될 때까지 버튼 기능 끄기
				enable_btn_find_id = false
				
				$.ajax({
					url : "${rootPath}/user/find-id",
					type : "POST",
					data : $("#find_id_form").serialize(),
					success : function(result) {
						if(result == "") {
							// 백엔드에서 null을 jsp와 렌더링하면 ""를 받게된다
							// result가 비어있으면
							alert("해당 이메일로 가입된 아이디가 없습니다.")
						} else {
							// result가 들어있으면
							$(".result-id").css("display", "block")
							result.forEach(function(username){
								$(".result-id").append("<p>" + username + "</p>")
							})
						}
					},
					error : function() {
						alert("서버 통신 오류")
					}
				}).always(function() {
					enable_btn_find_id = true
				})
			})
			
			$(document).on("click", "#btn-find-pw", function() {
				if(!enable_btn_find_pw) return false
				
				let username = $("#pw_username")
				let email = $("#pw_email")
				
				// 유효성 검사
				if(username.val() == "") {
					alert("아이디를 입력하세요.")
					username.focus()
					return false
				} else if ( !isEmail(email.val()) ) {
					alert("이메일을 정확히 입력하세요.")
					email.focus()
					return false
				}
				
				// 유효성 검사 통과 시
				// 서버 부하를 줄이기 위해 ajax 완료될 때까지 버튼 기능 끄기
				enable_btn_find_pw = false
				
				$.ajax({
					url : "${rootPath}/user/find-pw",
					type : "POST",
					data : $("#find_pw_form").serialize(),
					success : function(result) {
						if(result == 1) {
							alert("등록되지 않은 아이디입니다.")
						} else if (result == 2) {
							alert("등록된 email이 다릅니다.\n정확히 입력하세요.")
						} else if (result == 3) {
							alert("비밀번호 재설정 링크가 메일로 발송되었습니다.")
							username.val("")
							email.val("")
						} else if (result == 4) {
							alert("메일 발송에 실패했습니다.\n다시 시도하세요.")
						} else {
							alert("문제가 발생했습니다.\n다시 시도하세요.")
						}
					},
					error : function() {
						alert("서버 통신 오류")
					}
				}).always(function() {
					enable_btn_find_pw = true
				})
			})
			
		})
	</script>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/include_nav.jspf" %>
	<h2>ID/PW 찾기</h2>
	<section class="forms">
		<form:form id="find_id_form" action="${rootPath}/user/find-id" method="POST" autocomplete="off">
			<h3>ID 찾기</h3>
			<div class="form_item">
				<p>회원가입 시 등록한 메일을 입력하세요</p>
			</div>
			
			<input type="hidden"/>
			<div class="form_item">
				<input id="id_email" name="email" type="email" placeholder="Email 입력"/>
			</div>
			
			<div class="form_item result-id">
				<p class="result-header">가입된 ID</p>
			</div>
			
			<div class="form_item btn_box">
				<button id="btn-find-id" type="button">ID 찾기</button>
			</div>
		</form:form>
		
		<form:form id="find_pw_form" action="${rootPath}/user/find-pw" method="POST" autocomplete="off">
			<h3>비밀번호 찾기</h3>
			<div class="form_item">
				<p>회원가입 시 등록한 ID와 메일을 입력하세요</p>
			</div>
			
			<div class="form_item">
				<input id="pw_username" name="username" placeholder="ID 입력"/>
			</div>
			
			<div class="form_item">
				<input id="pw_email" name="email" type="email" placeholder="Email 입력"/>
			</div>
			
			<div class="form_item btn_box">
				<button id="btn-find-pw" type="button">비밀번호 찾기</button>
			</div>
		</form:form>
	</section>
</body>
</html>