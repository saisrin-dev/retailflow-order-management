package dev.saisrin.retailflow;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Component
public class SeedData implements CommandLineRunner {
 private final ProductRepository repository;
 public SeedData(ProductRepository r){repository=r;}
 @Override @Transactional public void run(String... args){if(repository.count()!=0)return;
 repository.saveAll(List.of(
 new Product("RF-1001","Organic coffee beans","Grocery","18.50",80),
 new Product("RF-1002","Ceramic pour-over set","Home","34.00",25),
 new Product("RF-1003","Linen kitchen towel","Home","12.00",8),
 new Product("RF-1004","Insulated bottle","Lifestyle","28.00",44),
 new Product("RF-1005","Oat milk - 6 pack","Grocery","21.00",60),
 new Product("RF-1006","Recycled notebook","Office","9.50",7),
 new Product("RF-1007","Desk organizer","Office","26.00",32),
 new Product("RF-1008","Canvas market tote","Lifestyle","16.00",48)));
 }
}
