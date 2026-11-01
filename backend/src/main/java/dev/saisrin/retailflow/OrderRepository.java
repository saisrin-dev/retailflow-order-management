package dev.saisrin.retailflow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface OrderRepository extends JpaRepository<PurchaseOrder,String> {
 Optional<PurchaseOrder> findByRequestKey(String key);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select o from PurchaseOrder o where o.id = :id")
 Optional<PurchaseOrder> lockById(@Param("id") String id);
}
