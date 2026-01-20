package hwan.diary.domain.diary.client.analysis;

import hwan.diary.domain.diary.client.dto.AnalysisRequest;
import hwan.diary.domain.diary.client.dto.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class EmotionAnalysisClient {

    private final RestClient emotionAnalysisRestClient;

    public AnalysisResponse postAnalysisRequest(AnalysisRequest request) {
        return emotionAnalysisRestClient.post()
            .uri("/analyze-diary-fusion")
            .accept(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(AnalysisResponse.class);
    }
}
