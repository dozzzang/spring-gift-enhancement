package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.ProductNotFoundException;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  private Product findProductByIdOrFail(Long id) {
    Product product = productRepository.findById(id);
    if (product == null) {
      throw new ProductNotFoundException();
    }
    return product;
  }

  public ProductResponseDto findProductById(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return ProductResponseDto.from(product);
  }

  public ProductResponseDto saveProduct(ProductRequestDto dto) {
    Product product = productRepository.saveProduct(dto.name(), dto.price(), dto.imageUrl());
    validateKaKaoApproval(product.getId());
    return ProductResponseDto.from(product);
  }

  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {
    Product updatedProduct = productRepository.updateProduct(productId, dto.name(), dto.price(),
        dto.imageUrl());
    return ProductResponseDto.from(updatedProduct);
  }

  //가격만 수정하는 것은 꽤 합리적이라고 생각
  public ProductResponseDto updateProductPrice(Long productId, int price) {

    Product updatedProduct = productRepository.updatePrice(productId, price);
    return ProductResponseDto.from(updatedProduct);
  }

  public void deleteProduct(Long productId) {
    findProductByIdOrFail(productId);
    productRepository.deleteById(productId);
  }

  public List<ProductResponseDto> findAllProducts() {
    return
        productRepository.findAllProducts()
            .stream()
            .map(ProductResponseDto::from)
            .collect(Collectors.toList());
  }

  private void validateKaKaoApproval(Long productId) {
    Product product = findProductByIdOrFail(productId);
    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }
  }

}
