package gift.wish.controller;

import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.security.LoginUser;
import gift.user.entity.User;
import gift.wish.dto.WishResponseDto;
import gift.wish.service.WishService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/wishlist")
public class WishViewController {

  private final WishService wishService;

  public WishViewController(WishService wishService) {
    this.wishService = wishService;
  }

  @GetMapping
  public String index(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "7") int size,
      @RequestParam(defaultValue = "id") String sort,
      @LoginUser User user,
      Model model) {

    PageRequestDto pageRequestDto = new PageRequestDto(page, size, sort);
    Page<ProductResponseDto> wishes = wishService.getWishes(user.getId(), pageRequestDto);

    model.addAttribute("wishes", wishes);
    return "wishlist/index";
  }
}
