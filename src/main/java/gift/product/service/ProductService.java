package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.ProductNotFoundException;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  private Product findProductByIdOrFail(Long id) {
     Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
     return product;
  }

  public ProductResponseDto findProductById(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return ProductResponseDto.from(product);
  }

  public ProductResponseDto saveProduct(ProductRequestDto dto) {
    Product product = new Product(dto.name(),dto.price(),dto.imageUrl());
    Product savedProduct = productRepository.save(product);
    validateKaKaoApproval(savedProduct.getId());
    return ProductResponseDto.from(savedProduct);
  }

  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {
    Product product = findProductByIdOrFail(productId);
    Product updatedProduct = new Product(productId, dto.name(), dto.price(),
        dto.imageUrl());
    Product savedProduct = productRepository.save(updatedProduct);
    return ProductResponseDto.from(savedProduct);
  }

  public void deleteProduct(Long productId) {
    findProductByIdOrFail(productId);
    productRepository.deleteById(productId);
  }

  public List<ProductResponseDto> findAllProducts() {
    List<Product> products = productRepository.findAll();
    List<ProductResponseDto> productResponseDtos = new ArrayList<>();

    for (Product product : products) {
      ProductResponseDto productResponseDto = ProductResponseDto.from(product);
      productResponseDtos.add(productResponseDto);
    }

    return productResponseDtos;
  }

  private void validateKaKaoApproval(Long productId) {
    Product product = findProductByIdOrFail(productId);
    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }
  }

}
