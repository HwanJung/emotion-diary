package hwan.diary.domain.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateDiaryRequest(
    @NotBlank @Size(max = 100) String title,
    @NotBlank @Size(max = 10_000) String content,
    @Size(max = 2048) String imageKey,
    @NotNull @PastOrPresent @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate diaryDate
    ) {}
