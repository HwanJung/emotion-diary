package hwan.diary.domain.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Optional;

public record UpdateDiaryRequest(
    @NotBlank @Size(max=100) String title,
    @NotBlank @Size(max=10_000) String content,
    @NotBlank @PastOrPresent @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate date,
    @Size(max = 2048) String newImageKey,
    boolean clearImage
) {}
