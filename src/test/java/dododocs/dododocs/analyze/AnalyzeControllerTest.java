package dododocs.dododocs.analyze;

import dododocs.dododocs.analyze.dto.FindGitRepoContentRequest;
import dododocs.dododocs.analyze.dto.UploadGitRepoContentToS3Request;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalyzeControllerTest extends ControllerTestConfig {

    @DisplayName("AI 레포지토리 분석 결과를 요청하고 상태코드 200을 리턴한다.")
    @Test
    void AI_레포지토리_분석_결과를_요청하고_상태코드_200을_리턴한다() throws Exception {
        // given
        given(jwtTokenCreator.extractMemberId(anyString())).willReturn(1L);
        doNothing().when(analyzeService).uploadGithubRepoToS3(anyLong(), anyString(), anyString());

        // when, then
        mockMvc.perform(post("/api/upload/s3")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UploadGitRepoContentToS3Request("Gatsby-Starter-Haon", "main"))))
                .andDo(print())
                .andDo(document("analyze/upload/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("repositoryName").type(JsonFieldType.STRING).description("분석 할 레포지토리 이름 (ex. Gatsby-Starter-Haon)"),
                                fieldWithPath("branchName").type(JsonFieldType.STRING).description("브랜치 명 (ex. main)")
                )))
                .andExpect(status().isOk());
    }

    @DisplayName("레포지토리의 모든 코드를 읽어오고 상태코드 200을 리턴한다.")
    @Test
    void 레포지토리의_모든_코드를_읽어오고_상태코드_200을_리턴한다() throws Exception{
        // given
        given(jwtTokenCreator.extractMemberId(anyString())).willReturn(1L);
        doNothing().when(analyzeService).uploadGithubRepoToS3(anyLong(), anyString(), anyString());

        // when, then
        mockMvc.perform(get("/api/repo/contents")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new FindGitRepoContentRequest("Gatsby-Starter-Haon", "main"))))
                .andDo(print())
                .andDo(document("find/repo/content/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("repositoryName").type(JsonFieldType.STRING).description("코드를 가져올 레포지토리 이름 (ex. Gatsby-Starter-Haon)"),
                                fieldWithPath("branchName").type(JsonFieldType.STRING).description("브랜치 명 (ex. main)")
                        )))
                .andExpect(status().isOk());
    }
}
