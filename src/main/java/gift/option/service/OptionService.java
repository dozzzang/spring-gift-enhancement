package gift.option.service;

import gift.exception.ProductNotFoundException;
import gift.option.dto.OptionResponseDto;
import gift.option.entity.Option;
import gift.option.repository.OptionRepository;
import gift.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

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
//    List<Option> options = optionRepository.findByProductId(productId);
//    List<OptionResponseDto> optionResponseDtos = new ArrayList<>();
//
//    for (Option option : options) {
//      optionResponseDtos.add(OptionResponseDto.from(option));
//    }
//
//    return optionResponseDtos;
    return optionRepository.findByProductId(productId).stream()
        .map(OptionResponseDto::from)
        .collect(Collectors.toList());
  }
}