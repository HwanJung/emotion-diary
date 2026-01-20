package hwan.diary.domain.diary.entity;

import hwan.diary.domain.diary.enums.AnalysisStatus;
import hwan.diary.domain.diary.enums.Emotion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "emotion_analysis"
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class EmotionAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "diary_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_emotion_analysis__diaries")
    )
    private Diary diary;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Emotion emotion;

    @Column(length = 16)
    private String colorCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AnalysisStatus status;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime analyzedAt;

    public static EmotionAnalysis create(Diary diary) {
        EmotionAnalysis ea = new EmotionAnalysis();
        ea.diary = diary;
        ea.status = AnalysisStatus.PENDING;

        return ea;
    }

    public EmotionAnalysis update(Emotion emotion, String colorCode) {
        this.emotion = emotion;
        this.colorCode = colorCode;
        analyzedAt = LocalDateTime.now();

        return this;
    }
}
