package dododocs.dododocs.test;

import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
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
    public ResponseEntity<DownloadAiAnalyzeResponse> analyzeResultTest(@RequestBody final DownloadAiAnalyzeRequest request) {
        List<Map<String, String>> summaryFiles = List.of(
                Map.of("Controller_summary.md", """
            # Controller Files Summary

            ## MemberLiveInformationController.md
            # 시스템 아키텍처 요약

            ## 1. 컴포넌트 개요
            - **주요 책임 및 목적**:
              - 클라이언트 요청 처리 및 비즈니스 로직 수행
              - 데이터베이스와의 상호작용을 통한 정보 관리
            - **주요 기능 및 능력**:
              - 회원의 생활 정보 조회 및 업데이트 API 제공
              - 인증 및 트랜잭션 관리 기능 포함
            - **핵심 패턴 및 접근법**:
              - MVC 패턴을 기반으로 한 구조
              - RESTful API 설계

            ## 2. 아키텍처 및 구현
            - **주요 컴포넌트 및 역할**:
              - **Controller Layer**: 클라이언트 요청을 처리하고 서비스 레이어와 상호작용
              - **Service Layer**: 비즈니스 로직 처리 및 리포지토리 호출
              - **Repository Layer**: 데이터베이스와의 직접적인 상호작용
              - **Database**: 데이터 저장소
            - **컴포넌트 상호작용 및 의존성**:
              - 클라이언트 → 컨트롤러 → 서비스 → 리포지토리 → 데이터베이스
            - **핵심 구현 패턴**:
              - `@Authentication` 및 `@Transactional` 어노테이션 사용
            - **중요 흐름 및 프로세스**:
              - 클라이언트 요청 → 컨트롤러 처리 → 서비스 로직 실행 → 데이터베이스 쿼리 → 응답 반환

            ## AuthController.md
            # 시스템 아키텍처 요약
            - 주요 책임:
              - OAuth 인증 처리 및 토큰 발급 관리
              - 클라이언트 요청에 대한 로그인, 로그아웃 처리
            - 주요 흐름:
              - 클라이언트 → AuthController → AuthService → TokenManager → Database
        """),
                Map.of("TripController_summary.md", """
            ## TripController.md
            # 시스템 아키텍처 요약

            ## 주요 기능
            - 여행 생성, 조회, 삭제 API 제공
            - RESTful API 설계
            - 데이터베이스와 상호작용을 통한 여행 정보 관리
            - 방문 횟수 및 유사 여행지 추천 기능 포함
            - 주요 패턴:
              - 트랜잭션 기반 데이터 처리
              - 인증 및 권한 체크

            ## 핵심 컴포넌트
            - Controller Layer: 클라이언트 요청 수신 및 처리
            - Service Layer: 여행 관련 비즈니스 로직 구현
            - Repository Layer: 데이터 접근
        """)
        );

        List<Map<String, String>> regularFiles = List.of(
                Map.of("MemberLiveInformationController.md", """
            # 시스템 아키텍처 문서

            ## 전체 구조
            ```mermaid
            graph TD
                A[Client] --> B[Controller Layer]
                B --> C[Service Layer]
                C --> D[Repository Layer]
                D --> E[Database]
            ```

            ## 시스템 흐름
            ```mermaid
            sequenceDiagram
                Client->>Controller: Request
                Controller->>Service: Process
                Service->>Repository: Data Access
                Repository->>Database: Query
            ```

            ## 주요 컴포넌트 설명
            ...
        """),
                Map.of("KeywordController.md", """
            # KeywordController.md

            ## 주요 책임
            - 키워드 CRUD API 제공
            - 키워드 기반 여행 추천 기능
            - 데이터 검증 및 캐싱 활용
        """),
                Map.of("PlannerController.md", """
            # PlannerController.md

            ## 주요 기능
            - 여행 일정 관리 API 제공
            - 일정 생성, 수정, 삭제
            - 여행과 일정 간의 관계 정의
        """)
        );

        return ResponseEntity.ok(new DownloadAiAnalyzeResponse(summaryFiles, regularFiles));
    }

}
