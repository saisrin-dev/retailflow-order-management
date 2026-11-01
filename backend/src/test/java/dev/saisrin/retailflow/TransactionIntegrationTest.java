package dev.saisrin.retailflow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static dev.saisrin.retailflow.ApiModels.*;
@SpringBootTest
class TransactionIntegrationTest {
 @Autowired RetailService service; @Autowired ProductRepository products;
 @Test void failedMultiLineOrderRollsBackEarlierReservation(){
  var a=products.saveAndFlush(new Product("TX-ROLL-A","Rollback A","Test","10.00",5));
  var b=products.saveAndFlush(new Product("TX-ROLL-B","Rollback B","Test","20.00",0));
  assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.create("rollback-request-01",new CreateOrder("Test Market",List.of(new Item(a.id,2),new Item(b.id,1)))));
  assertEquals(5,products.findById(a.id).orElseThrow().stock);
 }
 @Test void concurrentOrdersCannotOversell() throws Exception {
  var p=products.saveAndFlush(new Product("TX-RACE","Last unit","Test","10.00",1));
  var gate=new CountDownLatch(1);var pool=Executors.newFixedThreadPool(2);
  try {
   Callable<Boolean> first=()->attempt(gate,p.id,"race-request-001");
   Callable<Boolean> second=()->attempt(gate,p.id,"race-request-002");
   var a=pool.submit(first);var b=pool.submit(second);gate.countDown();
   int successes=(a.get(10,TimeUnit.SECONDS)?1:0)+(b.get(10,TimeUnit.SECONDS)?1:0);
   assertEquals(1,successes);assertEquals(0,products.findById(p.id).orElseThrow().stock);
  } finally {pool.shutdownNow();}
 }
 boolean attempt(CountDownLatch gate,Long id,String key) throws Exception {
  gate.await();try{service.create(key,new CreateOrder("Concurrency Market",List.of(new Item(id,1))));return true;}
  catch(org.springframework.web.server.ResponseStatusException e){if(e.getStatusCode().value()!=409)throw e;return false;}
 }
}
