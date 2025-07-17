package hwan.diary.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity @EntityListeners(AuditingEntityListener.class)
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long uid;
    private String title;
    @Lob
    private String text;
    private String image_url;

    @CreatedDate
    private LocalDateTime created_at;

    @LastModifiedDate
    private LocalDateTime modified_at;

}
