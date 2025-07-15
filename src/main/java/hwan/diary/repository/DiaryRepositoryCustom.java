package hwan.diary.repository;

import hwan.diary.domain.Diary;

import java.util.List;
import java.util.Optional;

public interface DiaryRepositoryCustom {
    //Diary save(Diary diary);
    //Optional<Diary> findById(long id);
    //List<Diary> findAll();
    //void update(int id, String text, MultipartFile img);
    void update(long id, String text);

}
