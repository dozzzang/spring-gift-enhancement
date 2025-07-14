package gift.wish.service;

import gift.product.dto.ProductResponseDto;
import gift.product.service.ProductService;
import gift.wish.dao.WishDao;
import gift.wish.dto.WishRequestDto;
import gift.wish.dto.WishResponseDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WishService {
  private final ProductService productService;
  private final WishDao wishDao;

  public WishService(ProductService productService, WishDao wishDao) {
    this.productService = productService;
    this.wishDao = wishDao;
  }

  public WishResponseDto createWish(Long memberId, WishRequestDto wishRequestDto) {
    productService.findProductById(wishRequestDto.productId());

    return wishDao.createWish(memberId, wishRequestDto.productId());
  }

  public List<ProductResponseDto> getWishes(Long memberId) {
   return  wishDao.findWishesByMemberId(memberId);
  }

  public void deleteWish(Long memberId, Long productId) {
    wishDao.deleteWish(memberId, productId);
  }
}
