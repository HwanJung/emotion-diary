package hwan.diary.domain.diary.repository;

import hwan.diary.domain.diary.dto.DiaryWithEmotionDto;
import hwan.diary.domain.diary.entity.Diary;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("""
        select new hwan.diary.domain.diary.dto.DiaryWithEmotionDto(
            d.id,
            d.title,
            d.content,
            d.imageKey,
            d.diaryDate,
            e.status,
            e.emotion,
            e.colorCode
        )
        from Diary d join EmotionAnalysis e on e.diary.id = d.id
        where d.user.id = :userId
            and d.deleted = false
    """)
    Slice<DiaryWithEmotionDto> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);

    Optional<Diary> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    @Query("""
        select new hwan.diary.domain.diary.dto.DiaryWithEmotionDto(
            d.id,
            d.title,
            d.content,
            d.imageKey,
            d.diaryDate,
            e.status,
            e.emotion,
            e.colorCode
        )
        from Diary d join EmotionAnalysis e on e.diary.id = d.id
        where d.user.id = :userId
            and d.id = :diaryId
            and d.deleted = false
    """)
    Optional<DiaryWithEmotionDto> findDiaryWithEmotionById(@Param("userId") Long userId,
                                                           @Param("diaryId") Long diaryId);
}
