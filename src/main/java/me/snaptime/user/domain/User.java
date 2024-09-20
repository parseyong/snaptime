package me.snaptime.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(name = "birth_day")
    private String birthDay;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "profile_photo_name",nullable = false)
    private String profilePhotoName;

    @Column(name = "secret_key", nullable = false)
    private SecretKey secretKey;

    @Builder
    protected User(String nickname, String email, String password,
                   String birthDay, SecretKey secretKey, String profilePhotoName){
        this.nickname = nickname;
        this.email = email;
        this.password =password;
        this.birthDay = birthDay;
        this.roleName = "ROLE_USER";
        this.secretKey = secretKey;
        this.profilePhotoName = profilePhotoName;
    }

    public void updateNickName(String nickName) {
        this.nickname = nickName;
    }

    public void updateBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateProfilePhotoName(String profilePhotoName){
        this.profilePhotoName = profilePhotoName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
