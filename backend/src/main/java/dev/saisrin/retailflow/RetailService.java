package dev.saisrin.retailflow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.*;
import static dev.saisrin.retailflow.ApiModels.*;
import static dev.saisrin.retailflow.PurchaseOrder.Status.*;
@Service @Transactional
public class RetailService {
 private final ProductRepository products; private final OrderRepository orders;
 public RetailService(ProductRepository p,OrderRepository o){products=p;orders=o;}
 @Transactional(readOnly=true) public List<ProductView> products(){return products.findAll().stream().sorted(Comparator.comparing(p->p.id)).map(ProductView::of).toList();}
 @Transactional(readOnly=true) public List<OrderView> orders(){return orders.findAll().stream().sorted(Comparator.comparing((PurchaseOrder o)->o.createdAt).reversed()).map(OrderView::of).toList();}
 @Transactional(readOnly=true) public Dashboard dashboard(){
  var ps=products.findAll();var os=orders.findAll();
  return new Dashboard(ps.size(),os.size(),ps.stream().filter(p->p.stock<10).count(),os.stream().filter(o->o.status!=CANCELLED).map(o->o.total).reduce(BigDecimal.ZERO,BigDecimal::add));
 }
 public ProductView restock(Long id,int q){var p=products.lockById(id).orElseThrow(()->missing("Product"));p.stock=Math.addExact(p.stock,q);return ProductView.of(p);}
 public OrderView create(String key,CreateOrder request){
  if(key==null||!key.matches("[A-Za-z0-9_-]{8,80}"))throw bad("Use an Idempotency-Key of 8-80 letters, digits, hyphens or underscores.");
  // Sort product IDs to make database lock ordering consistent across requests.
  Map<Long,Integer> quantities=new TreeMap<>();
  for(var i:request.items())quantities.merge(i.productId(),i.quantity(),Integer::sum);
  if(quantities.values().stream().anyMatch(q->q>1000))throw bad("Maximum quantity per product is 1000.");
  String fingerprint=request.retailer().trim()+"|"+quantities;
  var previous=orders.findByRequestKey(key);
  if(previous.isPresent()){
   if(!fingerprint.equals(previous.get().requestFingerprint))throw conflict("This request key belongs to a different order.");
   return OrderView.of(previous.get());
  }
  var order=new PurchaseOrder();order.requestKey=key;order.requestFingerprint=fingerprint;order.retailer=request.retailer().trim();
  for(var entry:quantities.entrySet()){
   var p=products.lockById(entry.getKey()).orElseThrow(()->missing("Product"));
   if(p.stock<entry.getValue())throw conflict("Insufficient stock for "+p.name+". Available: "+p.stock);
   p.stock-=entry.getValue();order.lines.add(new PurchaseOrder.Line(p,entry.getValue()));
   order.total=order.total.add(p.price.multiply(BigDecimal.valueOf(entry.getValue())));
  }
  order.events.add(new PurchaseOrder.Event(PLACED));orders.saveAndFlush(order);return OrderView.of(order);
 }
 public OrderView status(String id,PurchaseOrder.Status next){
  var o=orders.lockById(id).orElseThrow(()->missing("Order"));
  if(o.status==next)return OrderView.of(o);
  boolean allowed=switch(o.status){case PLACED -> next==CONFIRMED||next==CANCELLED;case CONFIRMED -> next==SHIPPED||next==CANCELLED;case SHIPPED -> next==DELIVERED;default -> false;};
  if(!allowed)throw conflict("Cannot change "+o.status+" to "+next+".");
  if(next==CANCELLED){
   o.lines.stream().sorted(Comparator.comparing(l->l.productId)).forEach(l->{var p=products.lockById(l.productId).orElseThrow(()->missing("Product"));p.stock=Math.addExact(p.stock,l.quantity);});
  }
  o.status=next;o.events.add(new PurchaseOrder.Event(next));return OrderView.of(o);
 }
 private static ResponseStatusException missing(String s){return new ResponseStatusException(HttpStatus.NOT_FOUND,s+" not found.");}
 private static ResponseStatusException bad(String s){return new ResponseStatusException(HttpStatus.BAD_REQUEST,s);}
 private static ResponseStatusException conflict(String s){return new ResponseStatusException(HttpStatus.CONFLICT,s);}
}
