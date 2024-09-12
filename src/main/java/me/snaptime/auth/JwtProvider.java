package me.snaptime.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.snaptime.auth.redis.repository.RefreshTokenRepository;
import me.snaptime.user.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${JWT.Access.SecretKey}")
    private String accessSecretKey;

    @Value("${JWT.Refresh.SecretKey}")
    private String refreshSecretKey;

    // 30분
    public Long accessTokenValidTime = 30 * 60 * 1000L;
    // 1달
    private Long refreshTokenValidTime = 30 * 24 * 60 * 60L;

    @PostConstruct
    protected void init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes(StandardCharsets.UTF_8));
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // accessToken을 생성합니다.
    @Transactional
    public String addAccessToken(String reqLoginId, Long userId, Collection<? extends GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(reqLoginId));
        claims.put("roleList", roles);
        claims.put("userId", userId);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();
    }

    // refreshToken을 생성합니다.
    @Transactional
    public String addRefreshToken(String reqLoginId, Long userId, Collection<? extends GrantedAuthority> roles){

        Claims claims = Jwts.claims().setSubject(String.valueOf(reqLoginId));
        claims.put("roleList", roles);
        claims.put("userId", userId);
        Date now = new Date();

        String refreshToken =  Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();

        refreshTokenRepository.save(new RefreshToken(reqLoginId,refreshToken));
        return refreshToken;
    }

    // accessToken으로부터 인증객체를 반환합니다.
    public Authentication findAuthenticationByAccessToken(String accessToken){
        User user = customUserDetailsService.loadUserByUsername(this.findLoginIdByAccessToken(accessToken));
        return new UsernamePasswordAuthenticationToken(user.getLoginId(),"",user.getAuthorities());
    }

    // 헤더에서 토큰을 가져옵니다.
    public String findTokenByHeader(HttpServletRequest request){

        String token = request.getHeader("Authorization");

        // Bear 제거 후 반환
        if(token != null && token.length() > 7 )
            return token.substring(7);

        return null;
    }

    // accessToken의 유효성을 검사합니다.
    public boolean isValidAccessToken(String accessToken) {
        try{

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(accessSecretKey).build().parseClaimsJws(accessToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // refreshToken의 유효성을 검사합니다.
    public boolean isValidRefreshToken(String reqRefreshToken){
        try{

            String reqLoginId = findLoginIdByRefreshToken(reqRefreshToken);
            RefreshToken savedRefreshToken = refreshTokenRepository.findByLoginId(reqLoginId).orElseThrow();

            if(savedRefreshToken == null || !reqRefreshToken.equals(savedRefreshToken.getRefreshToken())){
                return false;
            }

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(refreshSecretKey).build().parseClaimsJws(reqRefreshToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // accessToken으로부터 loginId를 추출합니다.
    private String findLoginIdByAccessToken(String accessToken) {

        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(accessToken).getBody().getSubject();
    }

    // refreshToken으로부터 loginId를 추출합니다.
    public String findLoginIdByRefreshToken(String refreshToken){
        return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();
    }
}





