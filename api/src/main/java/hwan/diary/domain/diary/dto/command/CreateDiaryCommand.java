package hwan.diary.domain.diary.dto.command;

import hwan.diary.domain.diary.dto.request.CreateDiaryRequest;

import java.time.LocalDate;

public record CreateDiaryCommand(
    String title,
    String content,
    String imageKey,
    LocalDate diaryDate
) {
    public static CreateDiaryCommand from(CreateDiaryRequest request) {
        return new CreateDiaryCommand(request.title(), request.content(), request.objectKey(), request.diaryDate());
    }
}
