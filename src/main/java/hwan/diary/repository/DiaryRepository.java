package hwan.diary.repository;

import hwan.diary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {


}
