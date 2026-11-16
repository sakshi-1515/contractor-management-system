
package com.company.cms.service;

import com.company.cms.entity.EmployeeDTO;
import com.company.cms.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(
            EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeDTO save(EmployeeDTO employee) {
        return repository.save(employee);
    }

    public List<EmployeeDTO> findAll(String employeeType) {
        return repository.findAll(employeeType);
    }

    public EmployeeDTO findById(Long id, String employeeType) {
        return repository.findById(id, employeeType);
    }

    public EmployeeDTO update(
            Long id,
            String employeeType,
            EmployeeDTO employee) {
        return repository.update(id, employeeType, employee);
    }

    public void deleteById(Long id, String employeeType) {
        repository.deleteById(id, employeeType);
    }

}
