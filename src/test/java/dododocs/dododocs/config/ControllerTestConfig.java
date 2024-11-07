package dododocs.dododocs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dododocs.dododocs.analyze.application.AnalyzeService;
import dododocs.dododocs.auth.application.AuthService;
import dododocs.dododocs.auth.domain.GithubOAuthUriProvider;
import dododocs.dododocs.auth.domain.JwtTokenCreator;
import dododocs.dododocs.auth.domain.JwtTokenProvider;
import dododocs.dododocs.auth.infrastructure.GithubOAuthClient;
import dododocs.dododocs.auth.presentation.AuthController;
import dododocs.dododocs.auth.presentation.authentication.AuthenticationBearerExtractor;
import dododocs.dododocs.global.config.S3Config;
import dododocs.dododocs.member.application.MemberService;
import dododocs.dododocs.member.presentation.MemberController;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@WebMvcTest({
        AuthController.class,
        MemberController.class,
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
}
