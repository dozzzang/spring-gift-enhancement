package gift.user.dto;

import gift.user.domain.User;

public record UserResponseDto(
    Long id,
    String email
) {

  public static UserResponseDto from(User user) {
    return new UserResponseDto(user.getId(), user.getEmail());
  }
}
