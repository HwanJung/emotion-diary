package hwan.diary.domain.diary.service;

import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.diary.repository.DiaryRepository;

import java.util.List;
import java.util.Optional;

public class DiaryService {
    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    /**
     * 일기작성
     */
    public long posting(Diary diary) {
        diaryRepository.save(diary);
        return diary.getId();
    }

    /**
     * 전체 일기 조회
     */

    public List<Diary> findDiary() {
        return diaryRepository.findAll();
    }

    public Optional<Diary> findOne(long id) {
        return diaryRepository.findById(id);
    }
}
