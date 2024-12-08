package dododocs.dododocs.analyze;

import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeRequest;
import dododocs.dododocs.analyze.dto.DownloadAiAnalyzeResponse;
import dododocs.dododocs.analyze.exception.NoExistRepoAnalyzeException;
import dododocs.dododocs.auth.dto.LoginRequest;
import dododocs.dododocs.config.ControllerTestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DownloadControllerTest extends ControllerTestConfig {

    @DisplayName("AI 문서화 결과를 다운로드 받고 상태코드 200을 리턴한다.")
    @Test
    void AI_문서화_결과를_다운로드_받고_상태코드_200을_리턴한다() throws Exception {
        // given
        given(jwtTokenCreator.extractMemberId(anyString())).willReturn(1L);
        given(downloadFromS3Service.downloadAndProcessZip(anyString()))
                .willReturn(new DownloadAiAnalyzeResponse(
                        List.of(new DownloadAiAnalyzeResponse.FileDetail("Controller_Summary.md", "전체 컨트롤러 요약 내용"),
                                new DownloadAiAnalyzeResponse.FileDetail("Service_Summary.md", "전체 서비스 요약 내용")),
                        List.of(new DownloadAiAnalyzeResponse.FileDetail("AuthController.md", "설명1"),
                                new DownloadAiAnalyzeResponse.FileDetail("AuthService.md", "설명2"),
                                new DownloadAiAnalyzeResponse.FileDetail("TravelController.md", "설명3"))
                ));


        // when, then
        mockMvc.perform(post("/api/download/s3")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .queryParam("repositoryName", "dododocs")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("analyze/download/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("엑세스 토큰")
                        ),
                        queryParameters(
                                parameterWithName("repositoryName").description("다운로드 받을 레포명")
                        ),
                        responseFields(
                                fieldWithPath("summaryFiles[]").type(JsonFieldType.ARRAY).description("요약 파일 목록"),
                                fieldWithPath("summaryFiles[].fileName").type(JsonFieldType.STRING).description("요약 파일 이름"),
                                fieldWithPath("summaryFiles[].fileContents").type(JsonFieldType.STRING).description("요약 파일 내용"),
                                fieldWithPath("regularFiles[]").type(JsonFieldType.ARRAY).description("일반 파일 목록"),
                                fieldWithPath("regularFiles[].fileName").type(JsonFieldType.STRING).description("일반 파일 이름"),
                                fieldWithPath("regularFiles[].fileContents").type(JsonFieldType.STRING).description("일반 파일 내용")
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("아직 AI 분석 결과가 완료되지 않았다면 상태코드 400을 리턴한다.")
    @Test
    void 아직_AI_분석_결과가_완료되지_않았다면_상태코드_400을_리턴한다() throws Exception {
        // given
        given(authService.extractMemberId(anyString())).willReturn(1L);
        doThrow(new NoExistRepoAnalyzeException("레포지토리 결과물을 아직 생성중입니다. 잠시만 기다려주세요."))
                .when(downloadFromS3Service).downloadAndProcessZip(anyString());

        // when, then
        mockMvc.perform(post("/api/download/s3")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .queryParam("repositoryName", "dododocs")
                         .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("analyze/download/fail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("repositoryName").description("다운로드 받을 레포명")
                        )
                ))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("레포에서 특정 파일명 입력했을 때, 그에 대한 리드미 내용을 제공한다.")
    @Test
    void getFileContentByFileName_ValidFile_ReturnsContent() throws Exception {
        // given
        given(authService.extractMemberId(anyString())).willReturn(1L);
        given(downloadFromS3Service.downloadAndProcessZip(anyString())).willReturn(
                new DownloadAiAnalyzeResponse(
                        List.of(new DownloadAiAnalyzeResponse.FileDetail("Controller_Summary.md", "전체 컨트롤러 요약 내용")),
                        List.of(new DownloadAiAnalyzeResponse.FileDetail("AuthService.md", "설명2"))
                )
        );

        // when, then
        mockMvc.perform(get("/api/download/s3/detail")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .queryParam("repositoryName", "my-repo")
                        .queryParam("fileName", "Controller_Summary.md")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("download/repo/file/detail/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("토큰")
                        ),
                        queryParameters(
                                parameterWithName("repositoryName").description("조회할 레포 이름"),
                                parameterWithName("fileName").description("조회할 파일 이름")
                        ),
                        responseFields(
                                fieldWithPath("fileName").description("요청한 파일 이름"),
                                fieldWithPath("fileContents").description("해당 파일의 내용")
                        )
                ))
                .andExpect(status().isOk());
    }

    @DisplayName("리드미 내용을 수정하고 성공 메시지를 반환한다.")
    @Test
    void updateFileContent_Success_ReturnsOk() throws Exception {
        // given
        doNothing().when(downloadFromS3Service)
                .updateFileContent(anyString(), anyString(), anyString());

        // when, then
        mockMvc.perform(put("/api/readme")
                        .header("Authorization", "Bearer aaaaaa.bbbbbb.cccccc")
                        .queryParam("repositoryName", "dododocs")
                        .queryParam("fileName", "Controller_Summary.md")
                        .queryParam("newContent", "new contents~~")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andDo(document("readme/update/success",
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
