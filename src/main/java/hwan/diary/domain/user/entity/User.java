package hwan.diary.domain.user.entity;

import hwan.diary.domain.user.values.Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    private String profileImageUrl;

    private String email;

}
