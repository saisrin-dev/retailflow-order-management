package dev.saisrin.retailflow;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false,unique=true) public String sku;
 @Column(nullable=false) public String name;
 @Column(nullable=false) public String category;
 @Column(nullable=false,precision=12,scale=2) public BigDecimal price;
 public int stock;
 @Version public long version;
 protected Product() {}
 public Product(String sku,String name,String category,String price,int stock) {
  this.sku=sku;this.name=name;this.category=category;this.price=new BigDecimal(price);this.stock=stock;
 }
}
