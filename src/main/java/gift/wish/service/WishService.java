package gift.wish.service;

import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import gift.user.entity.User;
import gift.user.repository.UserRepository;
import gift.wish.dto.WishRequestDto;
import gift.wish.dto.WishResponseDto;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class WishService {

  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final WishRepository wishRepository;

  public WishService(ProductRepository productRepository, UserRepository userRepository,
      WishRepository wishRepository) {
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.wishRepository = wishRepository;
  }

  private User findUserByIdOrFail(Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }
    return userOptional.get();
  }

  private Product findProductByIdOrFail(Long productId) {
    Optional<Product> productOptional = productRepository.findById(productId);
    if (productOptional.isEmpty()) {
      throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
    }
    return productOptional.get();
  }


  public WishResponseDto createWish(Long userId, WishRequestDto wishRequestDto) {
    User foundUser = findUserByIdOrFail(userId);
    Product foundProduct = findProductByIdOrFail(wishRequestDto.productId());

    Optional<Wish> wish = wishRepository.findByUserIdAndProductId(userId,
        wishRequestDto.productId());
    if (wish.isPresent()) {
      throw new BusinessException(ErrorCode.WISH_ALREADY_EXISTED);
    }

    Wish newWish = new Wish(foundUser, foundProduct);
    Wish savedWish = wishRepository.save(newWish);

    return new WishResponseDto(
        savedWish.getId(),
        savedWish.getUser().getId(),
        savedWish.getProduct().getId()
    );
  }

  public List<ProductResponseDto> getWishes(Long userId) {
    List<Wish> wishes = wishRepository.findByUserId(userId);
    List<ProductResponseDto> productResponseDtos = new ArrayList<>();

    for (Wish wish : wishes) {
      Product product = wish.getProduct();
      ProductResponseDto productResponseDto = new ProductResponseDto(
          product.getId(),
          product.getName(),
          product.getPrice(),
          product.getImageUrl(),
          product.isKakaoApproval()
      );
      productResponseDtos.add(productResponseDto);
    }

    return productResponseDtos;

  }

  public void deleteWish(Long userId, Long productId) {
    Optional<Wish> wish = wishRepository.findByUserIdAndProductId(userId, productId);

    if (wish.isEmpty()) {
      throw new BusinessException(ErrorCode.WISH_NOT_FOUND);
    }

    wishRepository.deleteByUserIdAndProductId(userId, productId);
  }
}
