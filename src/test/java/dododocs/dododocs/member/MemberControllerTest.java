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

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.config.ControllerTestConfig;
import dododocs.dododocs.member.domain.Member;
import dododocs.dododocs.member.dto.FindRegisterMemberRepoResponses;
import dododocs.dododocs.member.dto.FindRepoNameListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

    @DisplayName("멤버가 등록한 레포지토리 리스트를 조회하고 상태코드 200을 리턴한다.")
    @Test
    void findMemberRegisteredRepoList() throws Exception {
        // given
        given(authService.extractMemberId(anyString())).willReturn(1L);
        given(memberService.findRegisterMemberRepoResponses(anyLong()))
                .willReturn(new FindRegisterMemberRepoResponses(List.of(
                        new RepoAnalyze("dododocs", "main", "key1", "key1", "https://dododocs.github.com", new Member("")),
                        new RepoAnalyze("repo-name2", "develop", "key2", "key2", "https://repo.github.com", new Member("")),
                        new RepoAnalyze("repo-name3", "feature/social-login", "key3", "key3", "https://repo3.github.com", new Member(""))
                )));

        // when, then
        mockMvc.perform(get("/api/member/repos/registered")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("repos/registered/find/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("findRegisterRepoResponses[]").description("등록한 레포지토리 리스트"),
                                fieldWithPath("findRegisterRepoResponses[].repositoryName").description("레포 이름"),
                                fieldWithPath("findRegisterRepoResponses[].branchName").description("레포 브랜치 명"),
                                fieldWithPath("findRegisterRepoResponses[].createdAt").description("레포 등록날짜"),
                                fieldWithPath("findRegisterRepoResponses[].progressState").description("레포 등록 진행상태(진행률)")
                        )
                ))
                .andExpect(status().isOk());
    }
}
