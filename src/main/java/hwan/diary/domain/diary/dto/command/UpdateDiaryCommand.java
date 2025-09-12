package hwan.diary.domain.diary.dto.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import hwan.diary.domain.diary.dto.request.UpdateDiaryRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateDiaryCommand(
    String title,
    String content,
    LocalDate date,
    String newImageKey,
    boolean clearImage
) {
    public static UpdateDiaryCommand of(UpdateDiaryRequest request) {
        return new UpdateDiaryCommand(request.title(), request.content(), request.date(), request.newImageKey(), request.clearImage());
    }
}
