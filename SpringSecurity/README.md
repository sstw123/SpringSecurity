## 로그인/회원가입 + Spring Security

### 사용법
* Tomcat 서버 설치(개발 환경 : Tomcat 9.0)
* 방법 A와 방법 B 중 하나를 택한 뒤 진행
* 방법 A.
* com.sif.util 패키지 안에 있는 EncMySQL Java Application으로 실행, 오류 발생 확인하기
* com.sif.util 패키지 안에 있는 EncGmail Java Application으로 실행, 오류 발생 확인하기
* 프로젝트 우클릭 -> Run As -> Run Configurations 클릭
* Tomcat v9.0 Server at localhost, EncMySQL, EncGmail에 각각 Environment -> Add 클릭
* NAME은 ENV_PASS, VALUE는 암호화 키로 사용할 문자열 입력 (단, 복호화에 필요하므로 암호화 키는 모두 통일해야 함)

* 방법 B.
* 서버 컴퓨터로 사용할 PC에서(Windows 10 기준) 내 컴퓨터 -> 속성 -> 고급 시스템 설정 -> 환경 변수에 새로 만들기(사용자 변수 또는 시스템 변수 아무 곳이나 상관 없음)
* 변수 이름 : ENV_PASS, 변수 값 : 암호화 키로 사용할 문자열 입력

* A와 B 중 하나를 끝냈다면 다음으로 진행
* MySQL에서 사용할 Schema(Database), User 생성하기
* EncMySQL 실행 후 사용할 MySQL의 유저이름, 비밀번호 입력
* /WEB-INF/spring/properties 폴더 클릭 후 새로고침(F5) 하기. 새로고침 하지 않으면 새로 생성된 파일 인식 불가.
* /WEB-INF/spring/db-context.xml에서 BasicDataSource url의 community 부분을 사용할 MySQL 스키마 이름으로 변경
* EncGmail 실행 후 SMTP 메일로 사용할 Gmail의 아이디(@gmail.com 붙이지 않음), 앱 비밀번호(2단계 인증 사용해야 발급 가능) 입력
* 웹 브라우저 Gmail 로그인 -> 설정 -> 전달 및 POP/IMAP 탭 -> IMAP 사용 클릭 -> 변경사항 저장

* 어플리케이션 실행 후 작동 확인

### Spring Security
* Spring Security Core
* Spring Security Web
* Spring Security Config
* Spring Security Taglibs

* jasypt
* jasypt-spring31

### DB
* spring JDBC
* apache DBCP
* MyBatis
* MyBatis-Spring
* MySQL-J(ava)

### Basic Dependency
* lombok
* jackson-databind
* logback

### Spring Security 시작하기
#### 기본설정
1. web.xml 설정 : 한글 인코딩 필터 추가, *-context.xml
2. servlet-context.xml 설정 : component-scan controller, service 추가

#### Spring Security 기본 설정
1. spring 폴더 아래에 [jasypt-context.xml] 생성 후 설정
2. spring 폴더 아래에 [db-context.xml] 생성 후 설정
3. spring 폴더 아래에 [security-context.xml] 생성 후 설정
4. spring 폴더 아래에 properties 폴더 생성 후(security-context.xml에서 설정한 폴더) [spring-security.message.ko.properties] 파일 복사
5. service 패키지에 [AuthenticationProvider]를 implements 한 클래스를 하나 만들고 security-context.xml에 설정
6. 유저정보 테이블에 저장할 [UserDetailsVO](implements UserDetails), 권한 테이블에 저장할 [AuthorityVO] 하나씩 만들고 설정
7. [AuthenticationProviderImpl](implements AuthenticationProvider) -> [UserDetailsService] -> [UserDao], [AuthoritiesDao] 만들고 설정
8. spring 폴더 아래에 mapper 폴더 생성 후 [auth-mapper.xml], [user-mapper.xml] 생성 및 설정. user-mapper는 resultMap을 이용해 authorities 테이블 조회 결과도 가져오기

### Spring Security 설정
#### web.xml 설정
* 반드시 springSecurityFilterChain, springSecurityFilterChain 설정
* 반드시 contextConfigLocation에 jasypt -> db -> security 순서로 context를 로드하도록 설정한다. 논리적인 연동 순서대로 로드하지 않으면 오류 발생