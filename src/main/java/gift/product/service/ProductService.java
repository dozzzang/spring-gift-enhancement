package gift.product.service;

import gift.exception.KakaoApprovalException;
import gift.exception.OptionNotFoundException;
import gift.exception.OverlappingOptionNameException;
import gift.exception.ProductNotFoundException;
import gift.option.dto.OptionResponseDto;
import gift.option.entity.Option;
import gift.option.dto.OptionRequestDto;
import gift.product.dto.PageRequestDto;
import gift.product.dto.ProductRequestDto;
import gift.product.dto.ProductResponseDto;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import java.util.List;
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

  public List<OptionResponseDto> getProductOptions(Long productId) {
    Product product = findProductByIdOrFail(productId);
    return product.getOptions().stream()
        .map(OptionResponseDto::from)
        .toList();
  }

  //TODO : View 구현 미비로 인해, view controller를 위한 임시 saveProduct 메소드
  @Transactional
  public ProductResponseDto saveProduct(ProductRequestDto dto) {
    List<Option> defaultOptions = List.of(new Option("임시", 777));
    return saveProduct(dto, defaultOptions);
  }

  @Transactional
  public ProductResponseDto saveProduct(ProductRequestDto dto, List<Option> options) {
    validateUniqueOptionNames(dto);

    Product product = new Product(dto.name(), dto.price(), dto.imageUrl());

    if (product.getName().contains("카카오") && !product.isKakaoApproval()) {
      throw new KakaoApprovalException();
    }

    options.forEach(product::addOption);

    Product savedProduct = productRepository.save(product);
    return ProductResponseDto.from(savedProduct);
  }

  //TODO : View 구현 미비로 인해, view controller를 위한 임시 saveProduct 메소드
  @Transactional
  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto) {
    Product existingProduct = findProductByIdOrFail(productId);

    //고아 방지
    List<Option> existingOptions = existingProduct.getOptions().stream()
        .map(option -> new Option(option.getName(), option.getQuantity()))
        .toList();

    return updateProduct(productId, dto, existingOptions);
  }
  @Transactional
  public ProductResponseDto updateProduct(Long productId, ProductRequestDto dto, List<Option> options) {
    validateUniqueOptionNames(dto);

    Product product = findProductByIdOrFail(productId);

    product.setName(dto.name());
    product.setPrice(dto.price());
    product.setImageUrl(dto.imageUrl());

    product.getOptions().clear();

    options.forEach(product::addOption);

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