package dododocs.dododocs.auth.presentation.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AuthenticationConfig implements WebMvcConfigurer {
    private final AuthenticationArgumentResolver authenticationArgumentResolver;

    public AuthenticationConfig(final AuthenticationArgumentResolver authenticationArgumentResolver) {
        this.authenticationArgumentResolver = authenticationArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authenticationArgumentResolver);
    }
}
