package dododocs.dododocs.global.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    @Profile({"dev", "prod"})
    public AmazonS3Client amazonS3ClientWithProxy() {
        // AWS 인증 정보 생성
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // 프록시 설정 추가
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProxyHost("krmp-proxy.9rum.cc");
        clientConfiguration.setProxyPort(3128);

        // S3 클라이언트 생성
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withClientConfiguration(clientConfiguration)
                .build();
    }

    @Bean
    @Profile({"default", "local", "test"})
    public AmazonS3Client simpleAmazonS3Client() {
        // AWS 인증 정보 생성
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey, secretKey);

        // 기본 S3 클라이언트 생성 (프록시 없음)
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }
}
