package dododocs.dododocs.test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.UploadGitRepoContentToS3Request;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.ContentModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.payload.JsonFieldType;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ApiTestControllerTest extends ControllerTestConfig {

    @DisplayName("analyzeResultTest API: 문서화 결과를 정상적으로 반환하고 상태코드 200을 리턴한다.")
    @Test
    void analyzeResultTest_정상_응답_200() throws Exception {
        mockMvc.perform(get("/api/analyze/result")
                        .param("repositoryName", "Gatsby-Starter-Haon")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("test/analyze/result/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("repositoryName").description("레포명")
                        ),
                        responseFields(
                                fieldWithPath("summaryFiles").type(JsonFieldType.ARRAY).description("요약 파일 목록"),
                                fieldWithPath("summaryFiles[].*").type(JsonFieldType.STRING).description("요약 파일의 이름과 내용"),
                                fieldWithPath("regularFiles").type(JsonFieldType.ARRAY).description("일반 파일 목록"),
                                fieldWithPath("regularFiles[].*").type(JsonFieldType.STRING).description("일반 파일의 이름과 내용")
                        )
                ));

    }

    @DisplayName("테스트 API - 리드미 내용을 수정하고 성공 메시지를 반환한다.")
    @Test
    void updateFileContent_Success_ReturnsOk() throws Exception {
        // given, when, then
        mockMvc.perform(put("/api/test/readme/update")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .queryParam("repositoryName", "dododocs")
                        .queryParam("fileName", "Controller_Summary.md")
                        .queryParam("newContent", "new contents~~")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("test/readme/update/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("토큰")
                        ),
                        queryParameters(
                                parameterWithName("repositoryName").description("레포지토리 이름"),
                                parameterWithName("fileName").description("리드미 파일 이름"),
                                parameterWithName("newContent").description("새롭게 수정할 리드미 내용")
                        )
                ))
                .andExpect(status().isNoContent());
    }
}
