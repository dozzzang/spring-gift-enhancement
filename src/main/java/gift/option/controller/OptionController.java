package gift.option.controller;

import gift.option.dto.OptionRequestDto;
import gift.option.dto.OptionResponseDto;
import gift.option.service.OptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class OptionController {

  private final OptionService optionService;

  public OptionController(OptionService optionService) {
    this.optionService = optionService;
  }

  @GetMapping
  public ResponseEntity<List<OptionResponseDto>> getOptionsByProductId(
      @PathVariable Long productId) {

    List<OptionResponseDto> optionResponseDtos = optionService.getOptionsByProductId(productId);

    return ResponseEntity.ok(optionResponseDtos);
  }


  @PostMapping
  public ResponseEntity<OptionResponseDto> createOption(
      @PathVariable Long productId,
      @Valid @RequestBody OptionRequestDto optionRequestDto) {

    OptionResponseDto optionResponseDto = optionService.createOption(productId, optionRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(optionResponseDto);
  }
}
