package hwan.diary.service;

import hwan.diary.domain.Diary;
import hwan.diary.repository.DiaryRepositoryCustom;
import hwan.diary.repository.DiaryRepository;

import java.util.List;
import java.util.Optional;

public class DiaryService {
    private DiaryRepositoryCustom diaryRepository;

    public DiaryService(DiaryRepositoryCustom diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    /**
     * 일기작성
     */
    public long posting(Diary diary) {
        //diaryRepository.save(diary);
        return diary.getId();
    }

    /**
     * 전체 일기 조회
     */

    public List<Diary> findDiary() {
        //return diaryRepository.findAll();
        return null;
    }

    public Optional<Diary> findOne(long id) {
        //return diaryRepository.findById(id);
        return Optional.empty();
    }
}
