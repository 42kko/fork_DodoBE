package dododocs.dododocs.global.config;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {
    @Bean
    @Primary // Spring의 기본 ObjectMapper를 대체
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false) // Unicode 이스케이프 비활성화
                .configure(SerializationFeature.INDENT_OUTPUT, true)    // Pretty Print 활성화
                .build();
    }
}
