package gift.user;

import gift.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
  private final SecretKey secretKeySpec;
  private final JwtParser jwtParser;

  public JwtTokenProvider() {

    this.secretKeySpec = Keys.hmacShaKeyFor(secretKey.getBytes());

    this.jwtParser = Jwts.parser()
        .verifyWith(secretKeySpec)
        .build();
  }

  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().toString())
        .expiration(createExpirationDate())
        .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
        .compact();
  }

  private Date createExpirationDate() {
    long EXPIRATION_MINUTES = 30;
    return new Date(System.currentTimeMillis() + EXPIRATION_MINUTES * 60 * 1000);
  }

  public void validateToken(String token) throws Exception {
    try {
      jwtParser.parse(token);
    } catch (ExpiredJwtException e) {
      throw new Exception("만료된 토큰입니다.",e);
    } catch (SignatureException e) {
      throw new Exception("유효하지 않은 토큰 서명입니다.",e);
    } catch (MalformedJwtException e) {
      throw new Exception("올바르지 않은 토큰 형식입니다.", e);
    } catch (SecurityException e) {
      throw new Exception("보안을 만족하지 못한 토큰입니다.", e);
    } catch (Exception e) {
      throw new Exception("토큰 검증 중 서버 오류가 발생하였습니다.",e);
    }
  }

public String getRole(String token) throws Exception {
  try {
    Claims claims = jwtParser.parseSignedClaims(token).getPayload();
    return claims.get("role", String.class);
  } catch (JwtException e) {
    throw new Exception("유효하지 않은 토큰입니다", e);
  } catch (IllegalArgumentException e) {
    throw new Exception("토큰이 null이거나 빈 문자열입니다", e);
  } catch (Exception e) {
    throw new Exception("토큰에서 role을 가져올 수 없습니다", e);
  }
}

  public String getEmail(String token) throws Exception {
    try {
      Claims claims = jwtParser.parseSignedClaims(token).getPayload();
      return claims.get("email", String.class);
    } catch (JwtException e) {
      throw new Exception("유효하지 않은 토큰입니다", e);
    } catch (IllegalArgumentException e) {
      throw new Exception("토큰이 null이거나 빈 문자열입니다", e);
    } catch (Exception e) {
      throw new Exception("토큰에서 role을 가져올 수 없습니다", e);
    }
  }

}
