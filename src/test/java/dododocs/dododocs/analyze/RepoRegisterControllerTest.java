package dododocs.dododocs.analyze;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dododocs.dododocs.analyze.domain.RepoAnalyze;
import dododocs.dododocs.analyze.dto.DeleteRepoRegisterRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.analyze.dto.FindRepoRegisterResponses;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.Map;

public class RepoRegisterControllerTest extends ControllerTestConfig {

    @DisplayName("등록된 레포 리스트를 조회하고 상태코드 200을 리턴한다.")
    @Test
    void findRegisteredReposControllerTest() throws Exception {
        // given
        given(jwtTokenCreator.extractMemberId(anyString())).willReturn(1L);
        given(repoRegisterService.findRegisteredRepos(anyLong())).willReturn(
                new FindRepoRegisterResponses(List.of(
                        new RepoAnalyze(1L, "Gatsby-Starter-Haon"),
                        new RepoAnalyze(2L, "repo2-name")
                ))
        );

        // when, then
        mockMvc.perform(get("/api/register")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("register/find/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("엑세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("registeredRepoResponses[]").type(JsonFieldType.ARRAY).description("등록된 레포 리스트"),
                                fieldWithPath("registeredRepoResponses[].registeredRepoId").type(JsonFieldType.NUMBER).description("레포 고유 ID 값"),
                                fieldWithPath("registeredRepoResponses[].repositoryName").type(JsonFieldType.STRING).description("레포 이름")
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("등록된 레포를 삭제하고 상태코드 204를 리턴한다.")
    @Test
    void deleteRegisteredRepoControllerTest() throws Exception {
        // given
        given(jwtTokenCreator.extractMemberId(anyString())).willReturn(1L);
        doNothing().when(repoRegisterService).removeRegisteredRepos(anyLong());

        // when, then
        mockMvc.perform(delete("/api/register")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DeleteRepoRegisterRequest(1L))))
                .andDo(print())
                .andDo(document("register/delete/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("엑세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("registeredRepoId").description("삭제할 레포지토리 고유 ID")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}
