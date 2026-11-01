package dev.saisrin.retailflow;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.MediaType;
@SpringBootTest @AutoConfigureMockMvc @Transactional
class OrderIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ProductRepository products; @Autowired OrderRepository orders; @Autowired ObjectMapper json;
 long pid; int initial;
 @BeforeEach void setup(){var p=products.findAll().get(0);pid=p.id;initial=p.stock;}
 String body(int q){return "{\"retailer\":\"Demo Market\",\"items\":[{\"productId\":"+pid+",\"quantity\":"+q+"}]}";}
 String create(String key,int q) throws Exception {return mvc.perform(post("/api/orders").header("Idempotency-Key",key).contentType(MediaType.APPLICATION_JSON).content(body(q))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();}
 void change(String id,String status,int expected) throws Exception {mvc.perform(patch("/api/orders/"+id+"/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\""+status+"\"}")).andExpect(status().is(expected));}
 @Test void reservesStockAndUsesServerPrice() throws Exception {var o=json.readTree(create("request-test-01",2));assertEquals(initial-2,products.findById(pid).orElseThrow().stock);assertEquals(0,products.findById(pid).orElseThrow().price.multiply(java.math.BigDecimal.valueOf(2)).compareTo(o.get("total").decimalValue()));}
 @Test void replayDoesNotReserveTwice() throws Exception {var a=json.readTree(create("request-test-02",2));var b=json.readTree(create("request-test-02",2));assertEquals(a.get("id"),b.get("id"));assertEquals(initial-2,products.findById(pid).orElseThrow().stock);}
 @Test void keyCannotBeReusedForDifferentPayload() throws Exception {create("request-test-03",2);mvc.perform(post("/api/orders").header("Idempotency-Key","request-test-03").contentType(MediaType.APPLICATION_JSON).content(body(3))).andExpect(status().isConflict());}
 @Test void rejectsInsufficientStock() throws Exception {mvc.perform(post("/api/orders").header("Idempotency-Key","request-test-04").contentType(MediaType.APPLICATION_JSON).content(body(999))).andExpect(status().isConflict());assertEquals(initial,products.findById(pid).orElseThrow().stock);}
 @Test void cancellationReturnsStockExactlyOnce() throws Exception {String id=json.readTree(create("request-test-05",2)).get("id").asText();change(id,"CANCELLED",200);change(id,"CANCELLED",200);assertEquals(initial,products.findById(pid).orElseThrow().stock);}
 @Test void enforcesLifecycleAndRecordsEvents() throws Exception {String id=json.readTree(create("request-test-06",1)).get("id").asText();change(id,"DELIVERED",409);change(id,"CONFIRMED",200);change(id,"SHIPPED",200);change(id,"CANCELLED",409);change(id,"DELIVERED",200);assertEquals(4,orders.findById(id).orElseThrow().events.size());}
 @Test void rejectsZeroAndEmptyOrders() throws Exception {mvc.perform(post("/api/orders").header("Idempotency-Key","request-test-07").contentType(MediaType.APPLICATION_JSON).content(body(0))).andExpect(status().isBadRequest());mvc.perform(post("/api/orders").header("Idempotency-Key","request-test-08").contentType(MediaType.APPLICATION_JSON).content("{\"retailer\":\"Demo\",\"items\":[]}")).andExpect(status().isBadRequest());}
 @Test void missingRequestKeyRejected() throws Exception {mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(body(1))).andExpect(status().isBadRequest());}
 @Test void restockingAndMissingProduct() throws Exception {mvc.perform(post("/api/products/"+pid+"/restock").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":20}")).andExpect(status().isOk()).andExpect(jsonPath("$.stock").value(initial+20));mvc.perform(post("/api/products/99999/restock").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":20}")).andExpect(status().isNotFound());}
 @Test void negativeRestockRejected() throws Exception {mvc.perform(post("/api/products/"+pid+"/restock").contentType(MediaType.APPLICATION_JSON).content("{\"quantity\":-1}")).andExpect(status().isBadRequest());}
}
