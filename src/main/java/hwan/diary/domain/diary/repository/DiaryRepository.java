package hwan.diary.domain.diary.repository;

import hwan.diary.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {


}
