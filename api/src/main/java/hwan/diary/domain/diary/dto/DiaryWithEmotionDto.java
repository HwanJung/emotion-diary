package hwan.diary.domain.diary.dto;

import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;

import java.time.LocalDate;

public record DiaryWithEmotionDto(
    Long diaryId,
    String title,
    String content,
    String imageKey,
    LocalDate diaryDate,
    Long analysisId,
    AnalysisStatus analysisStatus,
    Emotion emotion,
    String colorCode
) {
}
