package hwan.diary.domain.diary.publisher.event;

public record DiaryAnalysisEvent(
    Long diaryId,
    Long analysisId,
    String imageUrl
    ) {
}
