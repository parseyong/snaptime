package me.snaptime.snap.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.user.domain.User;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.crypto.SecretKey;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Encryption {

    @Id
    @Column(name = "encryption_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long encryptionId;

    @Column(name = "secret_key", nullable = false)
    private SecretKey secretKey;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    protected Encryption(SecretKey secretKey, User user) {
        this.secretKey = secretKey;
        this.user = user;
    }
}
