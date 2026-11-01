package dev.saisrin.retailflow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Product p where p.id = :id")
 Optional<Product> lockById(@Param("id") Long id);
}
