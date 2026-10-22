package com.talleyouro.springmvcjpademo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/shippers")
public class ShipperController {

    private final ShipperRepository repo;

    public ShipperController(ShipperRepository repo) {
        this.repo = repo;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Shipper shipper) {
        if (shipper.getShipperId() != null && repo.existsById(shipper.getShipperId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Shipper id already exists");
        }
        shipper.setAddedOn(LocalDate.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(shipper));
    }

    // READ all (optional ?name= filter)
    @GetMapping
    public List<Shipper> getAll(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return repo.findByCompanyNameContainingIgnoreCase(name);
        }
        return repo.findAll();
    }

    // READ one
    @GetMapping("/{id}")
    public ResponseEntity<Shipper> getById(@PathVariable Short id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Shipper> update(@PathVariable Short id, @RequestBody Shipper in) {
        return repo.findById(id).map(existing -> {
            existing.setCompanyName(in.getCompanyName());
            existing.setPhone(in.getPhone());
            existing.setEditedBy(in.getEditedBy());
            existing.setEditedOn(LocalDate.now());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Short id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}