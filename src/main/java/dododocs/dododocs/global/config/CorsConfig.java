package dododocs.dododocs.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8000", "http://localhost:8080", "http://127.0.0.1:5500",
                        "https://dododocs.com", "https://dododocs.com:3000", "https://dododocs.com:443",
                        "http://ai.dododocs.com:5001",
                        "https://kcfaa61d53ebba.user-app.krampoline.com", "https://kcfaa61d53ebba.user-app.krampoline.com:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true);
    }
}