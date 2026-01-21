package hwan.diary.domain.diary.client.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import okhttp3.mockwebserver.MockWebServer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hwan.diary.domain.diary.client.dto.AnalysisRequest;
import hwan.diary.domain.diary.client.dto.AnalysisResponse;

import java.net.http.HttpClient;

class EmotionAnalysisClientHttpTest {

    private MockWebServer server;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    // Equal to EmotionAnalysisRestClient
    private EmotionAnalysisClient newClient() {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        String baseUrl = server.url("/").toString();

        RestClient restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

        return new EmotionAnalysisClient(restClient, objectMapper);
    }

    @Test
    void postAnalysisRequest_shouldSendTextAndImageUrl() throws Exception {
        // given
        server.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {"emotion":"JOY","colorCode":"#52B69A"}
                """)
        );

        EmotionAnalysisClient client = newClient();
        AnalysisRequest req = new AnalysisRequest("hello diary", "https://img.example.com/a.png");

        // when
        AnalysisResponse res = client.postAnalysisRequest(req);

        // then
        assertThat(res.emotion()).isEqualTo("JOY");
        assertThat(res.colorCode()).isEqualTo("#52B69A");

        RecordedRequest recorded = server.takeRequest();
        assertThat(recorded.getMethod()).isEqualTo("POST");
        assertThat(recorded.getPath()).isEqualTo("/analyze-diary-fusion");

        JsonNode body = objectMapper.readTree(recorded.getBody().readUtf8());
        assertThat(body.get("text").asText()).isEqualTo("hello diary");
        assertThat(body.get("image_url").asText()).isEqualTo("https://img.example.com/a.png");
    }

    @Test
    void postAnalysisRequest_imageUrlNull_shouldStillWork_andOmitField() throws Exception {
        // given
        server.enqueue(new MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody("""
                {"emotion":"NEUTRAL","colorCode":"#000000"}
                """)
        );

        EmotionAnalysisClient client = newClient();
        AnalysisRequest req = new AnalysisRequest("no image", null);

        // when
        AnalysisResponse res = client.postAnalysisRequest(req);

        // then
        assertThat(res.emotion()).isEqualTo("NEUTRAL");

        RecordedRequest recorded = server.takeRequest();
        JsonNode body = objectMapper.readTree(recorded.getBody().readUtf8());

        assertThat(body.get("text").asText()).isEqualTo("no image");
        assertThat(body.has("image_url")).isFalse(); // ✅ null이어도 돼: 필드 없어도 OK
    }
}
