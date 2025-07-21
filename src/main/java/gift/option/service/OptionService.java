package gift.option.service;

import gift.exception.OverlappingOptionNameException;
import gift.exception.ProductNotFoundException;
import gift.option.dto.OptionRequestDto;
import gift.option.dto.OptionResponseDto;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.product.entity.Product;
import gift.product.repository.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class OptionService {

  private final OptionRepository optionRepository;
  private final ProductRepository productRepository;

  public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
    this.optionRepository = optionRepository;
    this.productRepository = productRepository;
  }

  public List<OptionResponseDto> getOptionsByProductId(Long productId) {

    if (!productRepository.existsById(productId)) {
      throw new ProductNotFoundException();
    }

    return optionRepository.findByProductId(productId).stream()
        .map(OptionResponseDto::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public OptionResponseDto createOption(Long productId, OptionRequestDto optionRequestDto) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException());

    if (optionRepository.existsByProductIdAndName(productId, optionRequestDto.name())) {
      throw new OverlappingOptionNameException();
    }

    Option option = new Option(optionRequestDto.name(), optionRequestDto.quantity());
    product.addOption(option);

    Option foundOption = optionRepository.save(option);

    return OptionResponseDto.from(foundOption);
  }
}
