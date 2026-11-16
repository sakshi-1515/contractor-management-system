
package com.company.cms.repository;

import com.company.cms.entity.EmployeeDTO;

import java.util.List;

public interface EmployeeRepository {

    EmployeeDTO save(EmployeeDTO employee);

    List<EmployeeDTO> findAll(String employeeType);

    EmployeeDTO findById(Long id, String employeeType);

    EmployeeDTO update(
            Long id,
            String employeeType,
            EmployeeDTO employee);

    void deleteById(Long id, String employeeType);
}
