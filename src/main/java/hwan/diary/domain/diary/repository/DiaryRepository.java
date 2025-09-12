package hwan.diary.domain.diary.repository;

import hwan.diary.domain.diary.entity.Diary;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("""
        select d from Diary d
        where d.userId = :userId
        order by d.diaryDate desc, d.id desc
    """)
    Slice<Diary> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Diary> findByIdAndUserId(Long id, Long userId);
}
