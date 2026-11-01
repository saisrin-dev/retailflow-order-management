package dev.saisrin.retailflow;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="purchase_orders")
public class PurchaseOrder {
 public enum Status { PLACED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
 @Id public String id=UUID.randomUUID().toString();
 @Column(nullable=false,unique=true,length=80) public String requestKey;
 @Column(nullable=false,length=4000) public String requestFingerprint;
 @Column(nullable=false,length=80) public String retailer;
 @Enumerated(EnumType.STRING) public Status status=Status.PLACED;
 public Instant createdAt=Instant.now();
 @Column(precision=14,scale=2) public BigDecimal total=BigDecimal.ZERO;
 @ElementCollection @CollectionTable(name="order_lines") public List<Line> lines=new ArrayList<>();
 @ElementCollection @CollectionTable(name="order_events") @OrderColumn(name="event_position") public List<Event> events=new ArrayList<>();
 @Embeddable public static class Line {
  public Long productId; public String name; public int quantity;
  @Column(precision=12,scale=2) public BigDecimal unitPrice;
  public Line() {}
  public Line(Product p,int q){productId=p.id;name=p.name;quantity=q;unitPrice=p.price;}
 }
 @Embeddable public static class Event {
  public String status; public Instant at;
  public Event() {}
  public Event(Status s){status=s.name();at=Instant.now();}
 }
}
