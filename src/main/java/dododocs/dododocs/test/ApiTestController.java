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
                [ 📝 Overview](#📝-overview)  
                [ 📁 Project Structure](#📁-project-structure)  
                [ 🚀 Getting Started](#🚀-getting-started)  
                [ 💡 Motivation](#💡-motivation)  
                [ 🎬 Demo](#🎬-demo)  
                [ 🌐 Deployment](#🌐-deployment)  
                [ 🤝 Contributing](#🤝-contributing)  
                [ ❓ Troubleshooting & FAQ](#❓-troubleshootaing-&-faq)  
                [ 📈 Performance](#📈-performance)  

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
                ...
            """)
        );

        List<DownloadAiAnalyzeResponse.FileDetail> regularFiles = List.of(
                new DownloadAiAnalyzeResponse.FileDetail("AuthController.md", """
                # Project Name
                moheng

                ## Table of Contents
                [ 📝 Overview](#📝-overview)  
                [ 📁 Project Structure](#📁-project-structure)  
                [ 🚀 Getting Started](#🚀-getting-started)  
                [ 💡 Motivation](#💡-motivation)  
                [ 🎬 Demo](#🎬-demo)  
                [ 🌐 Deployment](#🌐-deployment)  
                [ 🤝 Contributing](#🤝-contributing)  
                [ ❓ Troubleshooting & FAQ](#❓-troubleshootaing-&-faq)  
                [ 📈 Performance](#📈-performance)  

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
                ...
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
