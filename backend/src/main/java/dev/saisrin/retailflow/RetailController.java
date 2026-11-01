package dev.saisrin.retailflow;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.List;
import static dev.saisrin.retailflow.ApiModels.*;
@RestController @RequestMapping("/api")
public class RetailController {
 private final RetailService service;
 public RetailController(RetailService service){this.service=service;}
 @GetMapping("/products") public List<ProductView> products(){return service.products();}
 @PostMapping("/products/{id}/restock") public ProductView restock(@PathVariable Long id,@Valid @RequestBody Restock body){return service.restock(id,body.quantity());}
 @GetMapping("/orders") public List<OrderView> orders(){return service.orders();}
 @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED) public OrderView create(@RequestHeader(value="Idempotency-Key",required=false) String key,@Valid @RequestBody CreateOrder body){return service.create(key,body);}
 @PatchMapping("/orders/{id}/status") public OrderView status(@PathVariable String id,@Valid @RequestBody ChangeStatus body){return service.status(id,body.status());}
 @GetMapping("/dashboard") public Dashboard dashboard(){return service.dashboard();}
}
