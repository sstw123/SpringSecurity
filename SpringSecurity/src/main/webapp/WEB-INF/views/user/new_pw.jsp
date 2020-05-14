<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="rootPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/include_head.jspf" %>
	<style>
		#find_form {
			width: 420px;
			height: 445px;
			margin: 0 auto;
			text-align: center;
		}
		.label {
			padding: 0.5rem 0px;
		}
		#btn_confirm {
			margin: 0.8rem;
			padding: 0.5rem 1rem;
			border: none;
			background-color: var(--button-bg-color);
			cursor: pointer;
			color: white;
		}
		#btn_confirm:hover {
			background-color: var(--button-hover-bg-color);
		}
	</style>
	<script>
		$(function() {
			$(document).on("click", "#btn_confirm", function() {
				let password = $("#password")
				let re_password = $("#re_password")
				
				if (password.val() == "") {
					alert("비밀번호를 입력하세요.")
					password.focus()
					return false
				} else if (re_password.val() == "") {
					alert("비밀번호 확인을 입력하세요.")
					re_password.focus()
					return false
				} else if (password.val() != re_password.val()) {
					alert("비밀번호가 다릅니다.\n다시 확인하세요.")
					re_password.focus()
					return false
				}
				
				$("#find_form").submit()
			})
		})
	</script>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/include_nav.jspf" %>
	<h2>비밀번호 재설정</h2>
	<form:form id="find_form" modelAttribute="userVO" autocomplete="off">
		<p>새로운 비밀번호를 입력하세요</p><br/>
		<div class="label">
			<label for="password">새 비밀번호</label>
		</div>
		
		<div>
			<input id="password" name="password" type="password"/>
		</div>
		
		<div class="label">
			<label for="re_password">새 비밀번호 확인</label>
		</div>
		
		<div>
			<input id="re_password" name="re_password" type="password"/>
		</div>
		
		<button id="btn_confirm" type="button">확인</button>
	</form:form>
</body>
</html>