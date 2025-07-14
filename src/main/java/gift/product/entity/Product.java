package gift.product.entity;


public class Product {

  private Long id;
  private String name;
  private int price;
  private String imageUrl;
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
}
