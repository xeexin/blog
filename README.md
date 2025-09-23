# Velog Clone 프로젝트

## 📖 프로젝트 소개
Spring Boot와 Thymeleaf를 사용하여 개발한 Velog 클론 블로그 플랫폼입니다.
사용자가 블로그를 만들고 글을 작성하며, 다른 사용자들과 소통할 수 있는 기능을 제공합니다.

## 🛠 기술 스택
- **Backend**: Spring Boot, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Database**: H2 Database (개발), MySQL (운영)
- **Build Tool**: Gradle
- **Language**: Java 17

## 📁 프로젝트 구조
```
src/
├── main/
│   ├── java/velog/clone/
│   │   ├── CloneApplication.java          # 메인 애플리케이션 클래스
│   │   ├── controller/                    # 컨트롤러 계층
│   │   │   ├── HomeController.java        # 홈 및 메인 페이지 컨트롤러
│   │   │   ├── admin/                     # 관리자 기능
│   │   │   │   └── AdminController.java   # 관리자 페이지 컨트롤러
│   │   │   ├── post/                      # 게시글 관련
│   │   │   │   ├── PostController.java    # 게시글 CRUD 컨트롤러
│   │   │   │   └── draftController.java   # 임시글 관리 컨트롤러
│   │   │   └── series/                    # 시리즈 관련
│   │   │       └── SeriesController.java  # 시리즈 관리 컨트롤러
│   │   ├── service/                       # 서비스 계층 (비즈니스 로직)
│   │   │   ├── UserService.java          # 사용자 관련 서비스
│   │   │   └── PostService.java          # 게시글 관련 서비스
│   │   ├── domain/                        # 엔티티 계층 (도메인 모델)
│   │   │   ├── User.java                 # 사용자 엔티티
│   │   │   ├── Post.java                 # 게시글 엔티티
│   │   │   ├── Blog.java                 # 블로그 엔티티
│   │   │   └── Comment.java              # 댓글 엔티티
│   │   ├── repository/                    # 레포지토리 계층 (데이터 접근)
│   │   │   ├── UserRepository.java       # 사용자 데이터 접근
│   │   │   ├── PostRepository.java       # 게시글 데이터 접근
│   │   │   └── CommentRepository.java    # 댓글 데이터 접근
│   │   ├── dto/                          # 데이터 전송 객체
│   │   │   └── PostDTO.java              # 게시글 DTO
│   │   ├── File/                         # 파일 처리 유틸리티
│   │   │   ├── FileStore.java            # 파일 저장/관리 클래스
│   │   │   └── UploadFile.java           # 업로드 파일 정보 클래스
│   │   └── Const/                        # 상수 정의
│   │       └── SessionConst.java         # 세션 관련 상수
│   └── resources/
│       ├── templates/                     # Thymeleaf 템플릿
│       ├── static/                        # 정적 리소스 (CSS, JS, 이미지)
│       └── application.properties         # 애플리케이션 설정
└── test/                                  # 테스트 코드
```

## ✨ 주요 기능

### 🔐 사용자 인증
- [x] 회원 가입
- [x] 로그인
- [x] 로그아웃

### 📝 블로그 포스트
- [x] 임시글 작성
- [x] 즉시 출간
- [x] 임시 글 목록
- [ ] 글 작성시 (이미지, URL) 포함
- [x] 태그 기능
- [x] 임시글 목록에서 임시 글 삭제
- [x] 임시글 수정, 수정 후 바로 출간

### ❤️ 소셜 기능 (좋아요, 팔로우)
- [x] 좋아하기
- [ ] 좋아하기 모아 보기
- [ ] 읽은 글 확인하기
- [x] 팔로우한 사용자 목록
- [x] 언팔로우

### 👤 개인정보 관리
- [x] 프로필 이미지 등록
- [x] 프로필 삭제
- [x] 블로그 제목 설정
- [ ] 블로그 제목 미 작성 시 아이디 사용하기
- [x] 이메일 주소 변경 → 프로필 이미지, 이름 수정
- [ ] 이메일 수신 설정
- [x] 회원 탈퇴 기능

### 📰 출간 관리
- [x] 포스트 미리보기 이미지 등록
- [x] 글의 제목 내용 일부 보기
- [ ] 전체공개/비공개
- [x] URL 구조: @아이디/posts/제목 (제목은 URL 인코딩 처리)
- [x] 시리즈 기능

### 📖 블로그 글 보기
- [x] 블로그 글 보기
- [ ] 비공개 표시
- [x] 내 글이 아닌 경우 좋아요
- [ ] 통계 정보
- [x] 글 수정
- [x] 글 삭제
- [x] 블로그를 작성한 사람의 프로필 이미지와 아이디 보여주기
- [x] 팔로우
- [ ] 사용자의 이전 글, 다음 글 보여주기

### 💬 댓글 시스템
- [x] 댓글 작성
- [x] 글보기 시, 댓글 수
- [ ] 답댓글
- [x] 댓글 삭제

### 👨‍💼 관리자 기능
- [x] 관리자 페이지로 가면 모든 포스팅 된 글 목록 보기
- [x] 어떤 글이든 삭제

## 🚀 시작하기

### 필수 요구사항
- Java 17 이상
- Gradle 7.x 이상

### 실행 방법
1. 프로젝트 클론
```bash
git clone [repository-url]
cd blog
```

2. 애플리케이션 실행
```bash
./gradlew bootRun
```

3. 브라우저에서 접속
```
http://localhost:8080
```

## 📝 개발 진행 상황
- ✅ **완료된 기능**: 기본적인 CRUD, 사용자 인증, 소셜 기능
- 🚧 **진행 중인 기능**: 이미지 업로드, 통계 기능
- 📋 **예정된 기능**: 답댓글, 전체공개/비공개, 읽은 글 확인

## 🤝 기여하기
프로젝트에 기여하고 싶으시다면 다음 단계를 따라주세요:
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request