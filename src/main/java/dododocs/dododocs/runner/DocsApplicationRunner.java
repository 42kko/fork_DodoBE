package dododocs.dododocs.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
public class DocsApplicationRunner implements ApplicationRunner {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=====================================================");
        log.info("DATABASE URL: {}", dbUrl);
        log.info("=====================================================");
    }
}
