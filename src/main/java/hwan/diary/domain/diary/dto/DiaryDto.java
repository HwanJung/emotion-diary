package hwan.diary.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record DiaryDto(
    Long id,
    String title,
    String content,
    String imageKey,
    LocalDate date
) {
}
