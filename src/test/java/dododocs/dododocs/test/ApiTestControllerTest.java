package dododocs.dododocs.test;

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

import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.UploadGitRepoContentToS3Request;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class ApiTestControllerTest extends ControllerTestConfig {

    @DisplayName("analyzeResultTest API: 문서화 결과를 정상적으로 반환하고 상태코드 200을 리턴한다.")
    @Test
    void analyzeResultTest_정상_응답_200() throws Exception {
        mockMvc.perform(get("/api/analyze/result")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DownloadAiAnalyzeRequest("Gatsby-Starter-Haon"))))
                .andDo(print())
                .andDo(document("test/analyze/result/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("repositoryName").type(JsonFieldType.STRING).description("레포명")
                        ),
                        responseFields(
                                fieldWithPath("summaryFiles").type(JsonFieldType.ARRAY).description("요약 파일 목록"),
                                fieldWithPath("summaryFiles[].*").type(JsonFieldType.STRING).description("요약 파일의 이름과 내용"),
                                fieldWithPath("regularFiles").type(JsonFieldType.ARRAY).description("일반 파일 목록"),
                                fieldWithPath("regularFiles[].*").type(JsonFieldType.STRING).description("일반 파일의 이름과 내용")
                        )
                ));
    }
}
