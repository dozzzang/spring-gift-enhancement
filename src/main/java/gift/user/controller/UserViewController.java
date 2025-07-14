package gift.user.controller;

import gift.user.dto.LoginRequestDto;
import gift.user.dto.RegisterRequestDto;
import gift.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserViewController {

  private final UserService userService;

  UserViewController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/register")
  public String moveRegisterForm() {
    return "product/register";
  }

  @GetMapping("/login")
  public String moveLoginForm() {
    return "product/login";
  }

  @PostMapping("/register")
  public String Register(@RequestParam String email,
      @RequestParam String password) {
      RegisterRequestDto dto = new RegisterRequestDto(email, password);
      userService.registerUser(dto);
      return "redirect:/";
  }

  @PostMapping("/login")
  public String Login(@RequestParam String email,
      @RequestParam String password) {
    LoginRequestDto dto = new LoginRequestDto(email, password);
    userService.loginUser(dto);
    return "redirect:/";
  }

}
