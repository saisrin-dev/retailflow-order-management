package dev.saisrin.retailflow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public class ApiModels {
 public record Item(@NotNull @Positive Long productId,@Min(1) @Max(1000) int quantity) {}
 public record CreateOrder(@NotBlank @Size(max=80) String retailer,@NotEmpty @Size(max=50) List<@NotNull @Valid Item> items) {}
 public record ChangeStatus(@NotNull PurchaseOrder.Status status) {}
 public record Restock(@Min(1) @Max(10000) int quantity) {}
 public record ProductView(Long id,String sku,String name,String category,BigDecimal price,int stock) {
  static ProductView of(Product p){return new ProductView(p.id,p.sku,p.name,p.category,p.price,p.stock);}
 }
 public record OrderView(String id,String retailer,PurchaseOrder.Status status,Instant createdAt,BigDecimal total,List<PurchaseOrder.Line> lines,List<PurchaseOrder.Event> events) {
  static OrderView of(PurchaseOrder o){return new OrderView(o.id,o.retailer,o.status,o.createdAt,o.total,List.copyOf(o.lines),List.copyOf(o.events));}
 }
 public record Dashboard(long products,long orders,long lowStock,BigDecimal bookedValue) {}
}
