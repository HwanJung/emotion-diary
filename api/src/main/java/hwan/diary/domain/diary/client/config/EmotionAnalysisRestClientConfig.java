package hwan.diary.domain.diary.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class EmotionAnalysisRestClientConfig {

    @Bean
    public RestClient emotionanalysisRestClient(RestClient.Builder builder, @Value("${analysis-server-url") String url) {
        return builder.baseUrl(url)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
