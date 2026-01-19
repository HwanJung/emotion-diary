package hwan.diary.domain.diary.util;

import hwan.diary.domain.diary.client.dto.AnalysisResponse;
import hwan.diary.domain.diary.dto.DiaryDto;
import hwan.diary.domain.diary.dto.DiaryWithEmotionDto;
import hwan.diary.domain.diary.entity.Diary;

public class DiaryMapper {
    public static DiaryDto toDiaryDto(Diary diary) {
        return new DiaryDto(
            diary.getId(),
            diary.getTitle(),
            diary.getContent(),
            diary.getImageKey(),
            diary.getDiaryDate()
        );
    }

    public static DiaryWithEmotionDto toDiaryWithEmotionDto(Diary diary, AnalysisResponse analysisResponse) {
        return new DiaryWithEmotionDto(
            diary.getId(),
            diary.getTitle(),
            diary.getContent(),
            diary.getImageKey(),
            diary.getDiaryDate(),
            analysisResponse.emotion(),
            analysisResponse.colorCode()
        );
    }
}
