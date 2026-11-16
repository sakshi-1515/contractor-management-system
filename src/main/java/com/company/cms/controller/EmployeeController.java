
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
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

    private final EmployeeService service;

    // Constructor Injection
    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<EmployeeDTO> save(
            @Valid @RequestBody EmployeeDTO employee) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(employee));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> findById(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id,

            @RequestParam String employeeType) {

        return ResponseEntity.ok(
                service.findById(id, employeeType)
        );
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> findAll(
            @RequestParam String employeeType) {

        return ResponseEntity.ok(
                service.findAll(employeeType)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> update(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id,

            @RequestParam String employeeType,

            @Valid @RequestBody EmployeeDTO employee) {

        return ResponseEntity.ok(
                service.update(id, employeeType, employee)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @PathVariable
            @Positive(message = "Employee ID must be positive")
            Long id,

            @RequestParam String employeeType) {

        service.deleteById(id, employeeType);

        return ResponseEntity.noContent().build();
    }
}
