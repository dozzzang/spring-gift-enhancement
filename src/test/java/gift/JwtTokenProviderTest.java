package gift;

import static org.assertj.core.api.Assertions.assertThat;

import gift.user.JwtTokenProvider;
import gift.user.entity.Role;
import gift.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static String createExpiredToken() {
    String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

    //TODO : 30분이 아닌 1시간으로 setting
    Date expiredTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

    return Jwts.builder()
        .setSubject("1")
        .claim("email", "admin.admin.com")
        .claim("role", "ADMIN")
        .expiration(expiredTime)
        .signWith(key)
        .compact();
  }

  private static String createWrongSignatureToken() {
    String wrongSecretKey = "qwe다른아무키rtyuiopasdfghjklzxcvbnm";
    SecretKey wrongKey = Keys.hmacShaKeyFor(wrongSecretKey.getBytes());

    Date futureTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

    return Jwts.builder()
        .setSubject("1")
        .claim("email", "admin.admin@com")
        .claim("role", "ADMIN")
        .expiration(futureTime)
        .signWith(wrongKey)
        .compact();
  }

  @Test
  void 토큰_생성_성공() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    User testUser = new User(1L, "admin@admin.com", "password", Role.ADMIN);

    //when
    String token = jwtTokenProvider.generateToken(testUser);

    //then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
  }


  @Test
  void 만료된_토큰() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createExpiredToken();

    try {
      jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("만료된 토큰입니다");
    }
  }

  @Test
  void 잘못된_서명_SignatureException() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createWrongSignatureToken();

    try {
      jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("유효하지 않은 토큰 서명입니다.");
    }
  }

  @Test
  void 잘못된_형식_MalformedJwtException() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = "not_allowed_format";

    try {
      jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("올바르지 않은 토큰 형식입니다.");
    }
  }

  @Test
  void 만료된_토큰_JwtException() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createExpiredToken();

    try {
      jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("만료된 토큰입니다.");
    }
  }

  @Test
  void 기타예외_토큰이_null인경우_IllegalArgumentException() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = null;

    try {
      jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("토큰 검증 중 서버 오류가 발생하였습니다");
    }
  }

  @Test
  void 유효한토큰_role추출_성공() throws Exception {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    User testUser = new User(1L, "admin@admin.com", "password", Role.ADMIN);
    String token = jwtTokenProvider.generateToken(testUser);

    //when
    String role = jwtTokenProvider.getRole(token);

    //then
    assertThat(role).isEqualTo("ADMIN");
  }

  @Test
  void 잘못된서명_토큰에서_role추출_JwtException() {
    //given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = createWrongSignatureToken();

    try {
      jwtTokenProvider.getRole(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("유효하지 않은 토큰입니다");
    }
  }

  @Test
  void null_토큰에서_role추출_IllegalArgumentException() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = null;

    try {
      jwtTokenProvider.getRole(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("토큰이 null이거나 빈 문자열입니다");
    }
  }

  @Test
  void 빈문자열_토큰에서_IllegalArgumentException() {
    // given
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
    String token = "";

    try {
      jwtTokenProvider.getRole(token);
    } catch (Exception e) {
      String errorMessage = e.getMessage();
      assertThat(errorMessage).contains("토큰이 null이거나 빈 문자열입니다");
    }
  }

}