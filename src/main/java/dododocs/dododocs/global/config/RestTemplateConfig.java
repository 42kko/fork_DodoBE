package dododocs.dododocs.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;


@Configuration
public class RestTemplateConfig {

    @Bean
    @Profile({"dev", "prod"})
    public RestTemplate restTemplateWithProxy() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("krmp-proxy.9rum.cc", 3128));
        factory.setProxy(proxy);

        return new RestTemplate(factory);
    }

    @Bean
    @Profile({"default", "local", "test"})
    public RestTemplate simpleRestTemplate() {
        return new RestTemplate();
    }
}
