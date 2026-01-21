package hwan.diary.domain.diary.client.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import hwan.diary.domain.diary.client.dto.AnalysisRequest;
import hwan.diary.domain.diary.client.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmotionAnalysisClient {

    private final RestClient emotionAnalysisRestClient;
    private final ObjectMapper objectMapper;

    public AnalysisResponse postAnalysisRequest(AnalysisRequest request) {
        try {
            log.info("[ANALYSIS_SERVER][REQ_BODY] {}", objectMapper.writeValueAsString(request));
        } catch (Exception ignore) {}


        return emotionAnalysisRestClient.post()
            .uri("/analyze-diary-fusion")
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(AnalysisResponse.class);
    }
}
