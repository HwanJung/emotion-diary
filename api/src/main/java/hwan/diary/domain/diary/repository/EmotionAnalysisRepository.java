package hwan.diary.domain.diary.repository;

import hwan.diary.domain.diary.entity.EmotionAnalysis;
import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, Long> {
    @Modifying
    @Query("""
        UPDATE EmotionAnalysis ea
        SET ea.status = :status,
            ea.emotion = :emotion,
            ea.colorCode = :colorCode,
            ea.analyzedAt = CURRENT_TIMESTAMP
        WHERE ea.diary.id = :diaryId
    """)
    int updateAnalysisResult(@Param("diaryId") Long diaryId,
                              @Param("status") AnalysisStatus status,
                              @Param("emotion") Emotion emotion,
                              @Param("colorCode") String colorCode);

    @Modifying
    @Query("""
        UPDATE EmotionAnalysis ea
        SET ea.status = :status
        WHERE ea.diary.id = :diaryId
    """)
    int updateStatusByDiaryId(@Param("diaryId") Long diaryId,
                               @Param("status") AnalysisStatus status);
}
