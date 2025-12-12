package es.davante.mi_primer_servicio.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ValidationController {

    private final Map<Long, Item> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public static class Item {
        public Long id;
        public String name;
        public String value;

        public Item() {}

        public Item(Long id, String name, String value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }

    @GetMapping("/service/items/{id}")
    public ResponseEntity<?> getItem(@PathVariable Long id) {
        Item it = store.get(id);
        if (it == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(it);
    }

    @PostMapping("/service/items")
    public ResponseEntity<Item> createItem(@RequestBody Item body) {
        long id = counter.getAndIncrement();
        Item it = new Item(id, body.name, body.value);
        store.put(id, it);
        return new ResponseEntity<>(it, HttpStatus.CREATED);
    }

    @PutMapping("/service/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item body) {
        Item existing = store.get(id);
        if (existing == null) return ResponseEntity.notFound().build();
        existing.name = body.name;
        existing.value = body.value;
        store.put(id, existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/service/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        Item removed = store.remove(id);
        if (removed == null) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar")
    public ResponseEntity<?> validar(
            @RequestParam(required = false) String passportId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {

        Map<String, Object> result = new java.util.LinkedHashMap<>();

        if (passportId != null) {
            Pattern p = Pattern.compile("^[A-Z0-9]{6,9}$");
            result.put("passportId", Map.of("value", passportId, "valid", p.matcher(passportId).matches()));
        }

        if (email != null) {
            Pattern p = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            result.put("email", Map.of("value", email, "valid", p.matcher(email).matches()));
        }

        if (phone != null) {
            Pattern p = Pattern.compile("^\\+?[0-9]{7,15}$");
            result.put("phone", Map.of("value", phone, "valid", p.matcher(phone).matches()));
        }

        if (result.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "No parameters provided"));
        return ResponseEntity.ok(result);
    }
}
