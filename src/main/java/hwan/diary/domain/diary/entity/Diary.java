package hwan.diary.domain.diary.entity;

import hwan.diary.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDate;

@Entity
@Table(
    name = "diaries",
    indexes = {
        @Index(name = "ix_diary_user_date_id", columnList = "user_id, diary_date, id")
    }
)
@SoftDelete(columnName = "deleted")
@Getter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 2048)
    private String imageKey;

    @Column(name = "diary_date", nullable = false)
    private LocalDate diaryDate;

    public static Diary create(Long userId, String title, String content, String imageKey, LocalDate diaryDate) {
        Diary d = new Diary();
        d.userId = userId;
        d.title = title;
        d.content = content;
        d.imageKey = imageKey;
        d.diaryDate = diaryDate;
        return d;
    }

    public void update(String title, String content, LocalDate diaryDate) {
        this.title = title;
        this.content = content;
        this.diaryDate = diaryDate;
    }

    public void changeImage(String newKey) { this.imageKey = newKey; }

    public void clearImage() { this.imageKey = null; }
}
