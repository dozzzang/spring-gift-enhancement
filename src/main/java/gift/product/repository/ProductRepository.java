package gift.product.repository;

import gift.product.entity.Product;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
  @Override
  List<Product> findAll();

}
