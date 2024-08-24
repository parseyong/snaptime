package me.snaptime.user.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.snaptime.common.BaseTimeEntity;
import me.snaptime.profilePhoto.domain.ProfilePhoto;
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

    @Column(name = "login_id",nullable = false, unique = true)
    private String loginId;

    private String password;

    private String email;

    @Column(name = "birth_day")
    private String birthDay;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_photo_id")
    private ProfilePhoto profilePhoto;

    @Column(name = "secret_key", nullable = false)
    private SecretKey secretKey;

    @Builder
    protected User(String nickname, String loginId, String password, String email, String birthDay, ProfilePhoto profilePhoto, SecretKey secretKey){
        this.nickname = nickname;
        this.loginId = loginId;
        this.password =password;
        this.email = email;
        this.birthDay = birthDay;
        this.roleName = "ROLE_USER";
        this.profilePhoto = profilePhoto;
        this.secretKey = secretKey;
    }

    public void updateUserName(String userName) { this.nickname = userName;}
    public void updateUserLoginId(String loginId) { this.loginId = loginId;}
    public void updateUserEmail(String email) { this.email = email;}
    public void updateUserBirthDay(String birthDay) { this.birthDay = birthDay;}
    public void updateUserPassword(String password){this.password = password;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(roleName));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.loginId;
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
