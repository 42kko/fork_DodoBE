package dododocs.dododocs.test;

import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.dto.Accessor;
import dododocs.dododocs.auth.presentation.authentication.Authentication;
import dododocs.dododocs.member.domain.Member;
import dododocs.dododocs.test.dto.CreateMemberRequest;
import dododocs.dododocs.test.dto.FindDbTestResponse;
import dododocs.dododocs.test.dto.FindTrueTestResponse;
import dododocs.dododocs.test.infrastructure.ExternalAiTestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class ApiTestController {
    private final ExternalAiTestClient externalAiTestClient;
    private final MemberRepository memberRepository;

    public ApiTestController(final MemberRepository memberRepository,
                             final ExternalAiTestClient externalAiTestClient) {
        this.memberRepository = memberRepository;
        this.externalAiTestClient = externalAiTestClient;
    }

    @GetMapping("/server")
    public ResponseEntity<FindTrueTestResponse> findTrueResponse() {
        return ResponseEntity.ok(new FindTrueTestResponse("ok!"));
    }

    @GetMapping("/db")
    public ResponseEntity<FindDbTestResponse> findDBResponse() {
        return ResponseEntity.ok(new FindDbTestResponse(1L, "Hello DKOS!"));
    }

    @PostMapping("/dbinsert")
    public ResponseEntity<Void> insertMember() {
        Member member = memberRepository.save(new Member("email"));
        System.out.println(member.getId());
        System.out.println(member.getSocialLoginId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dbfind")
    public ResponseEntity<Member> findMember() {
        return ResponseEntity.ok(memberRepository.findById(1L).orElse(null));
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(externalAiTestClient.requestTestAI());
    }

    @GetMapping("/analyze/result")
    public ResponseEntity<DownloadAiAnalyzeResponse> analyzeResultTest(@RequestParam final String repositoryName) {
        List<DownloadAiAnalyzeResponse.FileDetail> summaryFiles = List.of(
                new DownloadAiAnalyzeResponse.FileDetail("kakao-25_moheng.README.md", """
                # Project Name
                            moheng
                        
                            ## Table of Contents
                            [ 📝 Overview](#📝-overview) \s
                            [ 📁 Project Structure](#📁-project-structure) \s
                            [ 🚀 Getting Started](#🚀-getting-started) \s
                            [ 💡 Motivation](#💡-motivation) \s
                            [ 🎬 Demo](#🎬-demo) \s
                            [ 🌐 Deployment](#🌐-deployment) \s
                            [ 🤝 Contributing](#🤝-contributing) \s
                            [ ❓ Troubleshooting & FAQ](#❓-troubleshooting-&-faq) \s
                            [ 📈 Performance](#📈-performance) \s
                        
                            ## 📝 Overview
                            이 프로젝트는 여행 계획 및 추천 시스템을 구축하기 위한 것입니다. 사용자는 여행지에 대한 정보를 얻고, 개인화된 추천을 받을 수 있습니다.
                        
                            ### Main Purpose
                            - 사용자가 여행 계획을 세우고, 개인의 선호에 맞는 여행지를 추천받을 수 있도록 지원합니다.
                            - 여행지에 대한 정보를 제공하고, 사용자의 클릭 데이터를 기반으로 추천 알고리즘을 통해 맞춤형 여행지를 제안합니다.
                        
                            ### Key Features
                            - 사용자 맞춤형 여행지 추천
                            - 여행지 정보 제공
                            - 클릭 데이터를 기반으로 한 추천 알고리즘
                        
                            ### Core Technology Stack
                            - Frontend: React, Vite
                            - Backend: Spring Boot
                            - Database: MySQL
                            - Others: Python, FastAPI (AI 모델 서빙)
                        
                            ## 📁 Project Structure
                            ```
                            moheng
                            ├── 📁 ai
                            │   ├── 📁 model_serving
                            │   │   ├── 📁 application
                            │   │   ├── 📁 domain
                            │   │   ├── 📁 infra
                            │   │   ├── 📁 interface
                            │   │   └── ...
                            │   └── ...
                            ├── 📁 frontend
                            │   ├── 📁 src
                            │   │   ├── 📁 api
                            │   │   ├── 📁 components
                            │   │   └── ...
                            │   └── ...
                            ├── 📁 moheng
                            │   ├── 📁 auth
                            │   ├── 📁 planner
                            │   ├── 📁 trip
                            │   ├── 📁 member
                            │   └── ...
                            └── ...
                            ```
                        
                            ## 🚀 Getting Started
                        
                            ### Prerequisites
                        
                            - 지원 운영 체제
                              * Windows, macOS, Linux
                            - 필수 소프트웨어
                              * Node.js (프론트엔드)
                              * Java (백엔드)
                              * Python 3.11 (AI)
                            - 버전 요구 사항
                              * Node.js: 최신 안정 버전
                              * Java: OpenJDK 22
                              * Python: 3.11.x
                            - 패키지 관리자
                              * npm (프론트엔드)
                              * Poetry (AI)
                            - 시스템 종속성
                              * Docker
                        
                            ### Installation
                        
                            - Docker를 사용하여 환경을 설정할 수 있습니다.
                        
                            ```bash
                            # 리포지토리 클론
                            git clone https://github.com/kakao-25/moheng.git
                            cd moheng-develop
                        
                            # Docker 설정을 위해 Docker가 시스템에 설치되고 실행 중인지 확인하십시오.
                        
                            # Docker 컨테이너 빌드 및 실행
                            docker-compose up --build
                        
                            # 각 구성 요소를 별도로 빌드하고 실행하려면 아래 단계를 따르십시오:
                        
                            # 프론트엔드 설정
                            cd frontend
                            docker build -t moheng-frontend -f Dockerfile.front .
                            docker run -p 3000:3000 moheng-frontend
                        
                            # 백엔드 설정
                            cd backend
                            docker build -t moheng-backend -f Dockerfile.prod .
                            docker run -p 8080:8080 moheng-backend
                        
                            # AI/서비스 설정
                            cd ai
                            docker build -t moheng-ai -f Dockerfile .
                            docker run -p 8000:8000 moheng-ai
                        
                            # Nginx 설정
                            cd nginx
                            docker build -t moheng-nginx -f Dockerfile.prod .
                            docker run -p 80:80 moheng-nginx
                        
                            # 환경 구성
                            # 각 구성 요소에 필요한 환경 변수가 설정되어 있는지 확인하십시오.
                            ```
                        
                            ### Usage
                        
                            ```bash
                            # 실행 방법
                            # Docker 컨테이너 설정 후, 다음 URL을 통해 서비스를 액세스하십시오:
                        
                            # 프론트엔드
                            http://localhost:3000
                        
                            # 백엔드
                            http://localhost:8080
                        
                            # AI 서비스
                            http://localhost:8000
                        
                            # Nginx (리버스 프록시 역할)
                            http://localhost
                        
                        
                            ## 💡 Motivation
                            이 프로젝트는 여행 계획을 세우는 데 있어 사용자에게 더 나은 경험을 제공하기 위해 시작되었습니다. 개인의 선호를 반영한 추천 시스템을 통해 사용자가 더 쉽게 여행지를 선택할 수 있도록 돕고자 합니다.
                        
                            ## 🎬 Demo
                            ![Demo Video or Screenshot](path/to/demo.mp4)
                        
                            ## 🌐 Deployment
                            - AWS, Heroku와 같은 클라우드 플랫폼에 배포 가능
                            - 배포 단계에 따라 환경 설정이 필요합니다.
                        
                            ## 🤝 Contributing
                            - 기여 방법: 이슈를 생성하거나 Pull Request를 통해 기여할 수 있습니다.
                            - 코드 표준: Java, Python, JavaScript의 일반적인 코딩 표준을 따릅니다.
                            - Pull Request 프로세스: 변경 사항을 설명하는 메시지와 함께 Pull Request를 제출합니다.
                            - 행동 강령: 모든 기여자는 존중과 배려의 태도로 참여해야 합니다.
                        
                            ## ❓ Troubleshooting & FAQ
                            - 자주 발생하는 문제 및 해결 방법을 문서화합니다.
                            - FAQ 섹션을 통해 사용자 질문에 대한 답변을 제공합니다.
                        
                            ## 📈 Performance
                            - 성능 벤치마크 및 최적화 기법을 문서화합니다.
                            - 시스템의 확장성 고려 사항을 포함합니다.
            """)
        );

        List<DownloadAiAnalyzeResponse.FileDetail> regularFiles = List.of(
                new DownloadAiAnalyzeResponse.FileDetail("AuthController.md", """
                아래는 제공된 Java 코드에 대한 아키텍처 문서입니다. 이 문서는 시스템의 구조와 각 컴포넌트의 역할을 설명합니다.
                        
                            # 시스템 아키텍처 문서
                        
                            ## 전체 구조
                            ```mermaid
                            graph TD
                                A[Client] --> B[AuthController]
                                B --> C[AuthService]
                                C --> D[TokenManager]
                                C --> E[MemberService]
                                D --> F[MemberToken]
                                E --> G[Member]
                            ```
                        
                            ## 시스템 흐름
                            ```mermaid
                            sequenceDiagram
                                Client->>AuthController: Request for URI
                                AuthController->>AuthService: generateUri()
                                AuthService-->>AuthController: Return URI
                                AuthController->>Client: Send URI
                        
                                Client->>AuthController: Login Request
                                AuthController->>AuthService: generateTokenWithCode()
                                AuthService-->>AuthController: Return MemberToken
                                AuthController->>Client: Send Access Token
                        
                                Client->>AuthController: Extend Login Request
                                AuthController->>AuthService: generateRenewalAccessToken()
                                AuthService-->>AuthController: Return RenewalAccessTokenResponse
                                AuthController->>Client: Send New Access Token
                        
                                Client->>AuthController: Logout Request
                                AuthController->>AuthService: removeRefreshToken()
                                AuthService-->>AuthController: Refresh Token Removed
                                AuthController->>Client: No Content Response
                            ```
                        
                            ## 주요 컴포넌트 설명
                        
                            ### AuthController
                            - **역할과 책임**: 클라이언트의 요청을 처리하고, 서비스 계층과의 상호작용을 통해 인증 관련 작업을 수행합니다.
                            - **주요 메서드**:
                              - `generateUri()`: OAuth 제공자로부터 인증 URI를 생성합니다.
                              - `login()`: OAuth 인증 코드를 사용하여 로그인하고, 액세스 토큰을 생성합니다.
                              - `extendLogin()`: 기존의 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성합니다.
                              - `logout()`: 리프레시 토큰을 제거하여 로그아웃을 처리합니다.
                        
                            ### AuthService
                            - **역할과 책임**: 인증 관련 비즈니스 로직을 처리합니다. OAuth 제공자와의 상호작용, 멤버 생성 및 토큰 관리를 담당합니다.
                            - **주요 메서드**:
                              - `generateTokenWithCode()`: OAuth 인증 코드를 사용하여 멤버 토큰을 생성합니다.
                              - `generateUri()`: OAuth 제공자로부터 URI를 생성합니다.
                              - `generateRenewalAccessToken()`: 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성합니다.
                              - `removeRefreshToken()`: 로그아웃 시 리프레시 토큰을 제거합니다.
                              - `extractMemberId()`: 액세스 토큰에서 멤버 ID를 추출합니다.
                        
                            ### TokenManager
                            - **역할과 책임**: 토큰 생성 및 검증을 담당합니다. 액세스 토큰과 리프레시 토큰의 생성 및 관리 기능을 제공합니다.
                        
                            ### MemberService
                            - **역할과 책임**: 멤버 관련 데이터의 CRUD 작업을 처리합니다. 멤버의 존재 여부를 확인하고, 새로운 멤버를 생성합니다.
                        
                            ### MemberToken
                            - **역할과 책임**: 액세스 토큰과 리프레시 토큰을 포함하는 데이터 구조입니다. 인증 과정에서 생성된 토큰 정보를 저장합니다.
                        
                            ## 주의사항
                            1. 각 컴포넌트의 역할과 책임을 명확히 이해하고, 필요 시 추가적인 설명을 덧붙이세요.
                            2. 시스템의 흐름을 시퀀스 다이어그램으로 표현하여 클라이언트와 서버 간의 상호작용을 명확히 하세요.
                            3. 각 메서드의 기능과 사용 예를 문서화하여 개발자들이 쉽게 이해할 수 있도록 하세요.
            """)
        );

        return ResponseEntity.ok(new DownloadAiAnalyzeResponse(summaryFiles, regularFiles));
    }



    @PutMapping("/test/readme/update")
    public ResponseEntity<Void> updateTestReadme(@RequestParam String repositoryName,
                                                 @RequestParam String fileName,
                                                 @RequestParam String newContent) throws Exception {
        return ResponseEntity.noContent().build();
    }
}
