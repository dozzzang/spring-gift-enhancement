package gift.user.domain;

import gift.security.PasswordEncoder;

public class User {

  private String encodedPassword;
  private Long id;
  private String email;
  private Role role;

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getEncodedPassword() {
    return encodedPassword;
  }

  public Role getRole() {
    return role;
  }

  public User(Long id, String email, String encodedPassword, Role role) {
    this.id = id;
    this.email = email;
    this.encodedPassword = encodedPassword;
    this.role = role;
  }

  public User(Long id, String email, String encodedPassword) {
    this.id = id;
    this.email = email;
    this.encodedPassword = encodedPassword;
  }

  public boolean isEqualPassword(String password, PasswordEncoder passwordEncoder) {
    return passwordEncoder.isMatched(this.email, password, this.encodedPassword);
  }
}
