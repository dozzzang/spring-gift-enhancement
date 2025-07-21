package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.OverlappingOptionNameException;
import gift.exception.ProductNotFoundException;
import gift.option.entity.Option;
import gift.option.dto.OptionRequestDto;
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
import java.util.HashSet;
import java.util.Set;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  private Product findProductByIdOrFail(Long id) {
    return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
  }

  public ProductResponseDto findProductById(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return ProductResponseDto.from(product);
  }

  @Transactional
  public ProductResponseDto saveProduct(ProductRequestDto productRequestDto) {
    validateUniqueOptionNames(productRequestDto);

    Product product = new Product(productRequestDto.name(), productRequestDto.price(), productRequestDto.imageUrl());

    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }

    productRequestDto.options().stream()
        .map(optionRequest -> new Option(optionRequest.name(), optionRequest.quantity()))
        .forEach(product::addOption);

    Product savedProduct = productRepository.save(product);
    return ProductResponseDto.from(savedProduct);
  }

  @Transactional
  public ProductResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto) {

    validateUniqueOptionNames(productRequestDto);

    Product product = findProductByIdOrFail(productId);

    product.setName(productRequestDto.name());
    product.setPrice(productRequestDto.price());
    product.setImageUrl(productRequestDto.imageUrl());

    product.getOptions().clear();
    productRequestDto.options().stream()
        .map(optionRequest -> new Option(optionRequest.name(), optionRequest.quantity()))
        .forEach(product::addOption);

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

  private void validateUniqueOptionNames(ProductRequestDto dto) {
    long originalCount = dto.options().size();
    long distinctCount = dto.options().stream()
        .map(OptionRequestDto::name)
        .distinct()
        .count();

    if (originalCount != distinctCount) {
      throw new OverlappingOptionNameException();
    }
  }
}