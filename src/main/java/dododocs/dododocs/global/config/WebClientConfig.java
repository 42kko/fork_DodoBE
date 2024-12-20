package dododocs.dododocs.global.config;

import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.InetSocketAddress;

@Configuration
public class WebClientConfig {

    @Value("${ai.basic_url}")
    private String aiBasicUrl;

    @Bean
    @Profile({"dev"})
    public WebClient webClientWithProxy() {
        InetSocketAddress proxyAddress = new InetSocketAddress("krmp-proxy.9rum.cc", 3128);

        return WebClient.builder()
                .baseUrl(aiBasicUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).address(proxyAddress))))
                .build();
    }

    @Bean
    @Profile({"default", "local", "test", "prod"})
    public WebClient simpleWebClient() {
        return WebClient.builder()
                .baseUrl(aiBasicUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }
}

