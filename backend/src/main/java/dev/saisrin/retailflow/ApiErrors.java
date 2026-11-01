package dev.saisrin.retailflow;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.Map;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> domain(ResponseStatusException e){return ResponseEntity.status(e.getStatusCode()).body(Map.of("message",e.getReason()==null?"Request failed":e.getReason()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("message",e.getBindingResult().getFieldErrors().stream().map(f->f.getField()+": "+f.getDefaultMessage()).findFirst().orElse("Invalid request")));}
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<?> malformed(){return ResponseEntity.badRequest().body(Map.of("message","Invalid JSON or status value."));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> duplicate(){return ResponseEntity.status(409).body(Map.of("message","Concurrent duplicate request. Retry with the same request key."));}
}
