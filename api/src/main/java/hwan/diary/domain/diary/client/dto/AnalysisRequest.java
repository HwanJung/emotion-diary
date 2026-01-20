package hwan.diary.domain.diary.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnalysisRequest(
    @JsonProperty("text")String content,
    @JsonProperty("image_url") String imageUrl
) {
}
