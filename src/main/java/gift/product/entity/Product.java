package gift.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 15)
  private String name;
  @Column(nullable = false)
  private int price;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private boolean kakaoApproval = false;

  public Product(Long id, String name, int price, String imageUrl) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
  }

  public Product(Long id, String name, int price, String imageUrl,boolean kakaoApproval) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
    this.kakaoApproval = kakaoApproval;
  }

  public Product(String name, int price, String imageUrl) {
    this.name = name;
    this.price = price;
    this.imageUrl = imageUrl;
  }

  //kakaoApproval validation을 위해 Getter가 3개나 추가..
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getPrice() {
    return price;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public boolean isKakaoApproval() {
    return kakaoApproval;
  }

  protected Product() {

  }
}
