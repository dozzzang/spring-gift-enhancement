package gift.option.entity;

import gift.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "options")
public class Option {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 50, nullable = false)
  @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s()\\[\\]\\+\\-&/_]*$",
            message = "( ), [ ], +, -, &, /, _ 외 특수문자는 사용이 불가합니다.")
  @Size(max = 50, message = "옵션명은 50자 이하여야 합니다.")
  @NotBlank
  private String name;

  @Column(name = "quantity", nullable = false)
  @Min(value = 1, message = "수량은 최소 1개입니다.")
  @Max(value = 99999999, message = "수량은 1억 미만개여야 합니다.")
  private int quantity;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  protected Option() {

  }

}
