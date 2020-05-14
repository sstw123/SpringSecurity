<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="rootPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/include_head.jspf" %>
	<style>
		#body {
			width: 800px;
			display: flex; 
			margin: 0 auto;
		}
		
		#body nav {
			width: 20%;
			text-align: center;
			border-right: 1px solid gray;
			padding: 20px 0;
			margin: 5px;
		}
		
		#body nav ul {
			list-style: none;
		}
		
		#body nav a {
			display: block;
			text-decoration: none;
			color: black;
			padding: 0.5rem 1rem;
		}
		
		#body nav a:hover {
			background-color: #eee;
			color: black;
		}
		
		#body article {
			width: 60%;
			margin-left: 1rem;
		}
		
		
	</style>
	<script>
		$(function() {
			
			$(document).on("click", "#user_list", function() {
				$.get("${rootPath}/admin/userlist", function(result) {
					$("#admin_content").html(result)
				})
			})
			
			$(document).on("click", "tr.tr_user", function() {
				let username = $(this).attr("data-id")
				$.get("${rootPath}/admin/user_detail/" + username, function(result) {
					$("#admin_content").html(result)
				})
			})
			
			$(document).on("click", "#btn_edit", function() {
				let formdata = $("form").serialize()
				formdata += "&username=" + $("#btn_edit").attr("data-id")
				$.post("${rootPath}/admin/user_detail", formdata, function(result) {
					$("#admin_content").html(result)
					alert("변경사항이 저장되었습니다.")
				})
			})
			
			$(document).on("click","#btn_add_auth",function(){
				let auth_input = "<div class='new_auth_item'>"
								+ "<label class='label'>새 권한</label>"
								+ "<input class='data' name='auth'/>"
								+ "</div>"
				//auth_input.append($("<p/>", {"text":"제거","class":"auth_delete"}))
				$("div#auth_box").append(auth_input)
			})
			
		})
	</script>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/include_nav.jspf" %>
	<h2>관리자 페이지</h2>
	<section id="body">
		<nav>
			<ul>
				<li><a href="javascript:void(0)" id="user_list">유저목록</a></li>
				<li><a href="#">Menu1</a></li>
				<li><a href="#">Menu2</a></li>
			</ul>
		</nav>
		<article id="admin_content">
			
		</article>
	</section>
</body>
</html>