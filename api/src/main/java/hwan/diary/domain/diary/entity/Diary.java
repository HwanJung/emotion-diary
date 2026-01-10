package hwan.diary.domain.diary.entity;

import hwan.diary.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "diaries",
    indexes = {
        @Index(name = "idx_diaries__user_id_diary_date", columnList = "user_id, diary_date DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Diary{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_diaries__users")
    )
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(length = 255)
    private String imageKey;

    @Column(nullable = false)
    private LocalDate diaryDate;

    @Column(nullable = false)
    private boolean deleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static Diary create(User user, String title, String content, String imageKey, LocalDate diaryDate) {
        Diary d = new Diary();
        d.user = user;
        d.title = title;
        d.content = content;
        d.imageKey = imageKey;
        d.diaryDate = diaryDate;
        d.deleted = false;
        return d;
    }

    public void update(String title, String content, LocalDate diaryDate) {
        this.title = title;
        this.content = content;
        this.diaryDate = diaryDate;
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void changeImage(String newKey) { this.imageKey = newKey; }

    public void clearImage() { this.imageKey = null; }
}
