<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="rootPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<%@ include file="/WEB-INF/views/include/include_head.jspf" %>
	<style>
		.mypage_form {
			display: block;
			width: 50%;
			margin: 10px auto;
			border: 1px solid rgba(0,0,0,0.55);
			border-radius: 15px;
			color: black;
			box-shadow:0 1px 0 #cfcfcf;
			padding: 20px;
		}
		.mypage-div {
			display: flex;
		}
		.mypage-label {
			flex: 1;
			text-align: right;
			align-self: center;
			padding: 10px;
		}
		.mypage-content {
			flex: 3;
			align-self: center;
			padding: 10px;
		}
		.mypage-content input {
			width: 70%;
		}
		
		.age {
			display: flex;
		}
		#year, #month, #day {
			width: 15%;
			min-width: 70px;
			margin-right: 20px;
		}
		
		#btn_edit {
			display: block;
			width: 100px;
			padding: 10px;
			margin-top: 20px;
		}
		.flex {
			display: flex;
		}
		
		.email_change_item {
			text-align: center;
		}
		.email_change_item p, .auth_code_box p {
			margin-bottom: 10px;
		}
		.auth_code_box {
			display: none;
			margin-top: 20px;
			text-align: center;
		}
		#encrypted_auth_code {
			display: none;
		}
		
		#btn_edit {
			margin-left: auto;
		}
		#btn_close {
			cursor: pointer;
		}
		
	</style>
	<script>
		$(function() {
			let enable_btn_send_email = true
			let enable_btn_auth_code = true
			
			function isEmail(email) {
				let regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{1,6})+$/
				return regex.test(email)
			}
			
			// --------------------------------------------------------
			
			$(".mypage_form").submit(function() {
				if(!confirm("정말로 수정하시겠습니까?")) {
					return false
				}
			})
			
			$(document).on("click", "#btn_change_pw", function() {
				document.location.href = "${rootPath}/user/check-pw"
			})
			
			$(document).on("click", "#btn_send_email", function() {
				if(!enable_btn_send_email) return false
				let email = $("#email")
				
				// 유효성 검사
				if(email.val() == "") {
					alert("이메일을 입력하세요.")
					email.focus()
					return false
				} else if( !isEmail(email.val()) ) {
					alert("올바른 형식의 이메일이 아닙니다.")
					email.focus()
					return false
				}
				
				// 유효성 검사 통과 시
				// 이메일 스팸 및 서버 부하를 줄이기 위해 ajax 완료될 때까지 버튼 기능 끄기
				enable_btn_send_email = false
					
				$.ajax({
					url : "${rootPath}/user/change-email",
					type : "POST",
					data : {
						"${_csrf.parameterName}" : "${_csrf.token}",
						email : email.val()
					},
					success : function(result) {
						if(result == 'fail') {
							alert("메일 발송이 실패했습니다.\n정확히 입력했는지 확인 후 다시 시도하세요.")
						} else {
							$(".auth_code_box").css("display", "block")
							$("#encrypted_auth_code").text(result)
							alert("인증 메일이 발송되었습니다.")
						}
					},
					error : function() {
						alert("서버 통신 오류")
					}
				}).always(function() {
					enable_btn_send_email = true
				})
				
			})
			
			$(document).on("click", "#btn_auth_code", function() {
				if(!enable_btn_auth_code) return false
				let auth_code = $("#auth_code")
				
				// 유효성 검사
				if(auth_code.val() == "") {
					alert("인증코드를 입력하세요.")
					auth_code.focus()
					return false
				}
				
				// 유효성 검사 통과 시
				// 이메일 스팸 및 서버 부하를 줄이기 위해 ajax 완료될 때까지 버튼 기능 끄기
				enable_btn_auth_code = false
					
				$.ajax({
					url : "${rootPath}/user/change-email-auth",
					type : "POST",
					data : {
						"${_csrf.parameterName}" : "${_csrf.token}",
						enc_auth_code : $("#encrypted_auth_code").text(),
						auth_code : $("#auth_code").val()
					},
					success : function(result) {
						if(result == 1) {
							alert("이메일이 변경되었습니다.")
							document.location.replace(document.location.href)
						} else if(result == 3){
							alert("이메일 변경 및 인증이 완료되었습니다.")
							document.location.replace(document.location.href)
						} else if(result == 7) {
							alert("문제가 발생했습니다. 잠시 후 다시 시도하세요.")
						} else if(result == 4) {
							alert("인증코드가 일치하지 않습니다.")
						}
					},
					error : function() {
						alert("서버 통신 오류")
					}
				}).always(function() {
					enable_btn_auth_code = true
				})
				
			})
			
		})
	</script>
</head>
<body>
	<%@ include file="/WEB-INF/views/include/include_nav.jspf" %>
	<h2>마이페이지</h2>
	<form:form class="mypage_form" action="${rootPath}/user/mypage" autocomplete="off">
		<div class="mypage-div">
			<span class="mypage-label">ID</span>
			<div class="mypage-content">
				${loginVO.username}
			</div>
		</div>
		
		<div class="mypage-div">
			<span class="mypage-label">닉네임</span>
			<div class="mypage-content">
				<input id="nickname" name="nickname" value="${loginVO.nickname}" />
			</div>
		</div>
		
		<div class="mypage-div">
			<span class="mypage-label">비밀번호</span>
			<div class="mypage-content">
				<button id="btn_change_pw" type="button">비밀번호 변경</button>
			</div>
		</div>
		
		<div class="mypage-div">
			<span class="mypage-label">이메일</span>
			<div class="mypage-content">
				<span>${loginVO.email}</span>
				<button id="btn_email_change" data-toggle="modal" data-target="#email_change_modal" type="button">이메일 변경</button>
			</div>
		</div>
		
		<div class="mypage-div">
			<span class="mypage-label">핸드폰</span>
			<div class="mypage-content">
				<input id="phone" name="phone" value="${loginVO.phone}" />
			</div>
		</div>
		
		<div class="mypage-div">
			<span class="mypage-label">생년월일</span>
			<div class="mypage-content age">
				<input id="year" name="year" placeholder="년" maxlength="4" value="${loginVO.year}"/>
				<select id="month" name="month">
					<option value="">월</option>
					<c:forEach begin="1" end="12" var="count">
						<option value="${count}" <c:if test="${count == loginVO.month}">selected='selected'</c:if>>${count}월</option>
					</c:forEach>
				</select>
				<input id="day" name="day" placeholder="일" maxlength="2" value="${loginVO.day}"/>
			</div>
		</div>
		
		<div class="flex">
			<button id="btn_edit">수정</button>
		</div>
	</form:form>
	
	<article id="email_change_modal" class="modal">
		<section class="modal-dialog">
			<article class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">이메일 변경</h4>
					<span id="btn_close" class="close" data-dismiss="modal">&times;</span>
				</div>
				
				<div id="email_change_box" class="modal-body">
					<div class="email_change_item">
						<p>변경할 이메일을 입력하세요.</p>
						<input type="email" id="email" placeholder="Email 입력"/>
						<button id="btn_send_email">인증메일 발송</button>
					</div>
					
					<div class="auth_code_box">
						<p>이메일로 발송된 인증코드를 입력하세요.</p>
						<span id="encrypted_auth_code"></span>
						<input id="auth_code" placeholder="인증코드 입력"/>
						<button id="btn_auth_code">확인</button>
					</div>
				</div>
			</article>
		</section>
	</article>
</body>
</html>