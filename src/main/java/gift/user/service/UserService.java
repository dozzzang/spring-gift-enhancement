package gift.user.service;

import gift.exception.ErrorCode;
import gift.exception.InvalidLoginException;
import gift.exception.UnAuthorizationException;
import gift.exception.UserNotFoundException;
import gift.security.PasswordEncoder;
import gift.user.JwtTokenProvider;
import gift.user.dao.UserDao;
import gift.user.domain.User;
import gift.user.dto.LoginRequestDto;
import gift.user.dto.LoginResponseDto;
import gift.user.dto.RegisterRequestDto;
import gift.user.dto.RegisterResponseDto;
import gift.user.dto.UserRequestDto;
import gift.user.dto.UserResponseDto;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserDao userDao;
  private final JwtTokenProvider jwtTokenProvider;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserDao userDao, JwtTokenProvider jwtTokenProvider,
      PasswordEncoder passwordEncoder) {
    this.userDao = userDao;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
  }

  private User findByIdOrFail(Long id) {
    User user = userDao.findById(id);

    if (user == null) {
      throw new UserNotFoundException();
    }

    return user;
  }

  public RegisterResponseDto registerUser(RegisterRequestDto registerRequestDto) {
    String encryptedPassword = passwordEncoder.encrypt(registerRequestDto.email(),
        registerRequestDto.password());

    User user = userDao.saveUser(registerRequestDto.email(), encryptedPassword);

    String token = jwtTokenProvider.generateToken(user);

    return new RegisterResponseDto(token);
  }

  public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
    User user = userDao.findByEmail(loginRequestDto.email());

    if (user == null) {
      throw new UserNotFoundException();
    }

    if (!user.isEqualPassword(loginRequestDto.password(), passwordEncoder)) {
      throw new InvalidLoginException();
    }

    String token = jwtTokenProvider.generateToken(user);

    return new LoginResponseDto(token);
  }

  public List<UserResponseDto> findAllUsers() {
    return userDao.findAllUsers()
        .stream()
        .map(UserResponseDto::from)
        .collect(Collectors.toList());
  }

  public UserResponseDto saveUser(UserRequestDto dto) {
    String encryptedPassword = passwordEncoder.encrypt(dto.email(), dto.password());
    User user = userDao.saveUser(dto.email(), encryptedPassword);
    return UserResponseDto.from(user);
  }

  public UserResponseDto findById(Long userId) {
    User user = findByIdOrFail(userId);
    return UserResponseDto.from(user);
  }

  public UserResponseDto updateUser(Long userId, UserRequestDto dto) {
    User existingUser = findByIdOrFail(userId);
    String finalPassword;

    if (dto.password() == null || dto.password().trim().isEmpty()) {
      finalPassword = existingUser.getEncodedPassword();
    } else {
      finalPassword = passwordEncoder.encrypt(dto.email(), dto.password());
    }

    User user = userDao.updateUser(userId, dto.email(), finalPassword);
    return UserResponseDto.from(user);
  }

  public void deleteUser(Long userId) {
    findByIdOrFail(userId);
    userDao.deleteById(userId);
  }

  public User findUserByToken(String token) {
    try {
      String email = jwtTokenProvider.getEmail(token);
      User user = userDao.findByEmail(email);

      if (user == null) {
        throw new UserNotFoundException();
      }

      return user;
    } catch (Exception e) {
      throw new UnAuthorizationException(ErrorCode.INVALID_JWT);
    }
  }


}