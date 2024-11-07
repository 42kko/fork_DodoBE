package dododocs.dododocs.auth;


import dododocs.dododocs.auth.dto.LoginRequest;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends ControllerTestConfig {

    @DisplayName("소셜 로그인을 위한 링크와 상태코드 200을 리턴한다.")
    @Test
    void generateSocialLoginLink() throws Exception {
        // given
        given(authService.generateUri()).willReturn("https://어쩌구저쩌구");

        // when, then
        mockMvc.perform(get("/api/auth/link"))
                .andDo(print())
                .andDo(document("auth/generate/link",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("oauthUri").type(JsonFieldType.STRING).description("OAuth 소셜 로그인 링크"))
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("OAuth 로그인을 하면 토큰과 상태코드 201을 리턴한다.")
    @Test
    public void OAuth_로그인을_하면_토큰과_상태코드_201을_반환한다() throws Exception {
        // given
        String accessTokenResponse = "aaaaaaaaa.bbbbbbbb.cccccccc";
        given(authService.generateTokenWithCode(any())).willReturn(accessTokenResponse);

        // when, then
        mockMvc.perform(post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("bjqqiqqbnj1iuhsed1"))))
                .andDo(print())
                .andDo(document("auth/generate/token/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("code").type(LoginRequest.class).description("OAuth 로그인 인증 코드")),
                        responseFields(fieldWithPath("accessToken").type(JsonFieldType.STRING).description("엑세스 토큰. 프론트엔드는 이 발급받은 엑세스 토큰을 로컬스토리지등에 저장해야한다."))
                ))
                .andExpect(status().isCreated());
    }
}
