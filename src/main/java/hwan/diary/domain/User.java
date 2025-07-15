package hwan.diary.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private SNS provider;

    private String providerId;

    private String profileImageUrl;

    private String email;
}
