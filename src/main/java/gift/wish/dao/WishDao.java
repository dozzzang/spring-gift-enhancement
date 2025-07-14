package gift.wish.dao;

import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.product.dto.ProductResponseDto;
import gift.wish.dto.WishResponseDto;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class WishDao {
  private final JdbcClient jdbcClient;

  public WishDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public WishResponseDto createWish(Long memberId, Long productId) {
    String sql = "INSERT INTO wishes (memberId, productId) VALUES (?, ?)";

    jdbcClient.sql(sql)
        .param(memberId)
        .param(productId)
        .update();

    String selectSql = "SELECT wishId, memberId, productId " +
        "FROM wishes WHERE memberId = ? AND productId = ?";

    return jdbcClient.sql(selectSql)
        .param(memberId)
        .param(productId)
        .query((rs, rowNum) -> new WishResponseDto(
            rs.getLong("wishId"),
            rs.getLong("memberId"),
            rs.getLong("productId")
        ))
        .single();
  }

  public List<ProductResponseDto> findWishesByMemberId(Long memberId) {
    String sql = "SELECT \n"
        + "    w.wishId,\n"
        + "    w.memberId,\n"
        + "    w.productId,\n"
        + "    p.name AS productName,\n"
        + "    p.price AS productPrice,\n"
        + "    p.imageUrl AS productImageUrl,\n"
        + "    p.kakaoApproval AS productKakaoApproval\n"
        + "FROM wishes AS w\n"
        + "JOIN products AS p \n"
        + "    ON p.id = w.productId\n"
        + "WHERE w.memberId = ?";

    return jdbcClient.sql(sql)
        .param(memberId)
        .query((rs, rowNum) -> new ProductResponseDto(
            rs.getLong("productId"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("imageUrl"),
            rs.getBoolean("kakaoApproval")
        ))
        .list();
  }

  public void deleteWish(Long memberId, Long productId) {
    String sql = "DELETE FROM wishes WHERE memberId = ? AND productId = ?";

    int deletedRow = jdbcClient.sql(sql)
        .param(memberId)
        .param(productId)
        .update();

    if (deletedRow == 0) {
      throw new BusinessException(ErrorCode.WISH_NOT_FOUND);
    }

  }
}
