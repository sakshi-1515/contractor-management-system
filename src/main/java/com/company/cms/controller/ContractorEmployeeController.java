
package com.company.cms.controller;

import com.company.cms.entity.EmployeeDTO;
import com.company.cms.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contractors")
@Validated
public class ContractorEmployeeController {

    private final EmployeeService service;

    public ContractorEmployeeController(
            EmployeeService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<EmployeeDTO> save(
            @Valid @RequestBody EmployeeDTO employee) {

        employee.setEmployeeType("CONTRACTOR");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(employee));
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> findAll() {
        return ResponseEntity.ok(
                service.findAll("CONTRACTOR")
        );
    }

    // READ BY ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<EmployeeDTO> findById(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id) {

        return ResponseEntity.ok(
                service.findById(id, "CONTRACTOR")
        );
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id,
            @Valid @RequestBody EmployeeDTO employee) {

        employee.setEmployeeType("CONTRACTOR");

        return ResponseEntity.ok(
                service.update(id, "CONTRACTOR", employee)
        );
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id) {

        service.deleteById(id, "CONTRACTOR");

        return ResponseEntity.noContent().build();
    }
}
