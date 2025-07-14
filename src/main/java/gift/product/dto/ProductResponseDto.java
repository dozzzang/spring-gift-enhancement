package gift.product.dto;

import gift.product.entity.Product;

public record ProductResponseDto(
    Long id,
    String name,
    int price,
    String imageUrl,
    boolean kakaoApproval
) {

//static factory method를 이용하여 DTO가 Entity를 받도록 변경
  public static ProductResponseDto from(Product product) {
    return new ProductResponseDto(
        product.getId(),
        product.getName(),
        product.getPrice(),
        product.getImageUrl(),
        product.isKakaoApproval()
    );
  }
}
