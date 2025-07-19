package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.ProductNotFoundException;
import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }

    Product savedProduct = productRepository.save(product);
    return ProductResponseDto.from(savedProduct);
  }

  @Transactional
  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {
    Product product = findProductByIdOrFail(productId);

    product.setName(dto.name());
    product.setPrice(dto.price());
    product.setImageUrl(dto.imageUrl());

    return ProductResponseDto.from(product);
  }

  public void deleteProduct(Long productId) {
    findProductByIdOrFail(productId);
    productRepository.deleteById(productId);
  }

  public Page<ProductResponseDto> findAllProducts(PageRequestDto pageRequestDto) {
    Sort sortCondition = Sort.by(Direction.DESC, pageRequestDto.sort());
    Pageable pageable = PageRequest.of(pageRequestDto.page(), pageRequestDto.size(), sortCondition);

    Page<Product> products = productRepository.findAll(pageable);

    return products.map(ProductResponseDto::from);
  }

  private void validateKaKaoApproval(Long productId) {
    Product product = findProductByIdOrFail(productId);
    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }
  }

}
