package gift.wish.repository;

import gift.wish.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface WishRepository extends CrudRepository<Wish,Long> {

  List<Wish> findByUserId(Long userId);
  Optional<Wish> findByUserIdAndProductId(Long userId, Long productId);
  void deleteByUserIdAndProductId(Long userId, Long productId);

}
