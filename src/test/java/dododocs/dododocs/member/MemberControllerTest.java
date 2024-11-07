package dododocs.dododocs.member;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dododocs.dododocs.config.ControllerTestConfig;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

public class MemberControllerTest extends ControllerTestConfig {

    @DisplayName("멤버의 깃허브 레포지토리 이름 리스트를 불러온다.")
    @Test
    void findMemberGitRepoNameList() throws Exception {
        // given
        given(authService.extractMemberId(anyString())).willReturn(1L);
        given(memberService.getUserRepositories(anyLong()))
                .willReturn(new FindRepoNameListResponse(List.of("repo-name1", "repo-name2", "repo-name3")));

        // when, then
        mockMvc.perform(get("/api/member/repos")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("member/repos",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("names").type(JsonFieldType.ARRAY).description("저장소 이름 목록")
                        )
                ))
                .andExpect(status().isOk());
    }
}
