<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="rootPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/include_head.jspf" %>
	<style>
		#check_pw_form {
			width: 420px;
			height: 445px;
			margin: 0 auto;
			text-align: center;
		}
		.label {
			padding: 0.5rem 0px;
		}
		.btn_confirm {
			padding: 0.5rem 1rem;
			border: none;
			background-color: var(--button-bg-color);
			cursor: pointer;
			color: white;
		}
		.btn_confirm:hover {
			background-color: var(--button-hover-bg-color);
		}
	</style>
	<script>
		$(function() {
			$("#password").focus()
		})
	</script>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/include_nav.jspf" %>
	<h2>비밀번호 확인</h2>
	<form:form id="check_pw_form" action="${rootPath}/user/check-pw" autocomplete="off">
		<div class="label">
			<label for="password">현재 비밀번호를 입력하세요</label>
		</div>
		
		<div>
			<input id="password" name="password" autocomplete="off" type="password"/>
			<button class="btn_confirm">확인</button>
		</div>
	</form:form>
</body>
</html>