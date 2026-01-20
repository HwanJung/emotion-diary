package hwan.diary.domain.diary.dto;

import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;

import java.time.LocalDate;

public record DiaryWithEmotionDto(
    Long id,
    String title,
    String content,
    String imageKey,
    LocalDate diaryDate,
    AnalysisStatus analysisStatus,
    Emotion emotion,
    String colorCode
) {
}
