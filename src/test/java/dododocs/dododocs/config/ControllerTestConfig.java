package dododocs.dododocs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.application.AnalyzeService;
import dododocs.dododocs.analyze.application.DownloadFromS3Service;
import dododocs.dododocs.analyze.application.RepoRegisterService;
import dododocs.dododocs.analyze.domain.repository.RepoAnalyzeRepository;
import dododocs.dododocs.analyze.presentation.AnalyzeController;
import dododocs.dododocs.analyze.presentation.RepoRegisterController;
import dododocs.dododocs.analyze.presentation.S3DownloadController;
import dododocs.dododocs.auth.application.AuthService;
import dododocs.dododocs.auth.domain.GithubOAuthUriProvider;
import dododocs.dododocs.auth.domain.JwtTokenCreator;
import dododocs.dododocs.auth.domain.JwtTokenProvider;
import dododocs.dododocs.auth.domain.repository.MemberRepository;
import dododocs.dododocs.auth.infrastructure.GithubOAuthClient;
import dododocs.dododocs.auth.presentation.AuthController;
import dododocs.dododocs.auth.presentation.authentication.AuthenticationBearerExtractor;
import dododocs.dododocs.chatbot.application.ChatbotService;
import dododocs.dododocs.chatbot.domain.repository.ChatLogRepository;
import dododocs.dododocs.chatbot.presentation.ChatbotController;
import dododocs.dododocs.global.config.S3Config;
import dododocs.dododocs.member.application.MemberService;
import dododocs.dododocs.member.presentation.MemberController;
import dododocs.dododocs.test.ApiTestController;
import dododocs.dododocs.test.infrastructure.ExternalAiTestClient;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfigureRestDocs
@WebMvcTest({
        AuthController.class,
        MemberController.class,
        S3DownloadController.class,
        AnalyzeController.class,
        ApiTestController.class,
        RepoRegisterController.class,
        ChatbotController.class,
})
@Import(TestConfig.class)
@ActiveProfiles("test")
public abstract class ControllerTestConfig {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthService authService;

    @Mock
    protected GithubOAuthClient githubOAuthClient;

    @MockBean
    protected AuthenticationBearerExtractor authenticationBearerExtractor;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected JwtTokenCreator jwtTokenCreator;

    @MockBean
    protected GithubOAuthUriProvider githubOAuthUriProvider;

    @MockBean
    protected AnalyzeService analyzeService;

    @MockBean
    protected DownloadFromS3Service downloadFromS3Service;

    @MockBean
    protected ExternalAiTestClient externalAiTestClient;

    @MockBean
    protected MemberRepository memberRepository;

    @MockBean
    protected RepoRegisterService repoRegisterService;

    @MockBean
    protected ChatbotService chatbotService;

    @MockBean
    protected ChatLogRepository chatLogRepository;

    @MockBean
    protected RepoAnalyzeRepository repoAnalyzeRepository;

    @MockBean
    protected WebClient webClient;
}
