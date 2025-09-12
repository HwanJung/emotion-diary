package hwan.diary.domain.user.entity;

import hwan.diary.common.entity.BaseTimeEntity;
import hwan.diary.domain.diary.entity.Diary;
import hwan.diary.domain.user.values.Provider;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false, unique = true, length = 100)
    private String providerId;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false, length = 100)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Diary> diaries = new ArrayList<>();
}
