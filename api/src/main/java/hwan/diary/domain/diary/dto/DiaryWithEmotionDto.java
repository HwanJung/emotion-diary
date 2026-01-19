package hwan.diary.domain.diary.dto;

import java.time.LocalDate;

public record DiaryWithEmotionDto(
    Long id,
    String title,
    String content,
    String imageKey,
    LocalDate diaryDate,
    String emotion,
    String colorCode
) {
}
