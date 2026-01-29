package hwan.diary.domain.diary.service;

import hwan.diary.domain.diary.dto.command.CreateDiaryCommand;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.entity.EmotionAnalysis;
import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;
import hwan.diary.domain.diary.repository.DiaryRepository;
import hwan.diary.domain.diary.repository.EmotionAnalysisRepository;
import hwan.diary.domain.user.entity.User;
import hwan.diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDiaryTxService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final EmotionAnalysisRepository emotionAnalysisRepository;

    @Transactional
    public Diary createDiaryTx(CreateDiaryCommand createDiaryCommand, Long userId) {
        User userRef = userRepository.getReferenceById(userId);

        Diary diary = Diary.create(
            userRef,
            createDiaryCommand.title(),
            createDiaryCommand.content(),
            createDiaryCommand.imageKey(),
            createDiaryCommand.diaryDate()
        );

        Diary savedDiary = diaryRepository.save(diary);
        emotionAnalysisRepository.save(EmotionAnalysis.create(savedDiary));

        return savedDiary;
    }

    @Transactional
    public void markDoneTx(Long diaryId, Emotion emotion, String colorCode) {
        emotionAnalysisRepository.updateAnalysisResult(diaryId, AnalysisStatus.DONE, emotion, colorCode);
    }

    @Transactional
    public void markFailedTx(Long diaryId) {
        emotionAnalysisRepository.updateStatusByDiaryId(diaryId, AnalysisStatus.FAILED);
    }
}
