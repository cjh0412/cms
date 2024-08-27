package config;

import domain.common.UserVo;
import domain.common.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import util.Aes256Util;

import java.util.Date;
import java.util.Objects;

// 로그인 토큰을 만들기 위해 jsonWebToken(JWT) 을 이용
// 인코딩이 된 값으로 security key를 만들어 개인정보 보안
// payload의 경우 다른 사람도 decode가 가능하기 때문에 보안상 중요한 정보는 담지x

public class  JwtAuthenticationProvider {
    private String secretKey = "secretKey";

    // 토큰 사용 기간
    private long tokenValidTime = 1000L * 60 * 60 * 24; // 1초 * 1분 * 1시간 * 24시간 = 하루설정

    // 토큰 생성
    public String createToken(String userPk, Long id, UserType userType){
        // Clailms : jws의 body부분, jwt의 값을 설정
        // setSubject : token의 용도를 명시 : 회원 id(이메일)
        Claims claims = Jwts.claims().setSubject(Aes256Util.encrypt(userPk)).setId(Aes256Util.encrypt(id.toString()));
        claims.put("roles", userType.toString());
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // claim값 설정
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간
                // 해싱 알고리즘을 통해 입력 받은 키를 암호화
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact(); // 토큰 생성
    }

    //  토큰 검증
    public boolean validateToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date()); // 현재시간보다 토큰 만료시간이 적을때만
        }catch (Exception e){
            return false;
        }
    }

    // 토큰이 가지고 있는 id, email decode
    public UserVo getUserVo(String token){
        Claims c = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return new UserVo(Long.valueOf(Objects.requireNonNull(Aes256Util.decrypt(c.getId()))),
                Aes256Util.decrypt(c.getSubject()));
    }
}
