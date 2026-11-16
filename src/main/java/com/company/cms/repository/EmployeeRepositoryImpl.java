
package com.company.cms.repository;

import com.company.cms.entity.EmployeeDTO;
import com.company.cms.exception.PmsServiceUnavailableException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Supplier;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final RestTemplate restTemplate;
    private final String pmsUrl;

    public EmployeeRepositoryImpl(
            RestTemplate restTemplate,
            @Value("${pms.service.url}") String pmsUrl) {

        this.restTemplate = restTemplate;
        this.pmsUrl = pmsUrl;
    }

    // COMMON PMS REQUEST HANDLER
    private <T> T executePmsRequest(Supplier<T> request) {
        try {
            return request.get();

        } catch (ResourceAccessException ex) {
            throw new PmsServiceUnavailableException(
                    "EMS is currently unavailable. Please try again later.",
                    ex
            );
        }
    }

    // VALIDATE EMPLOYEE TYPE
    private String validateEmployeeType(String employeeType) {

        if (employeeType == null ||
                !("PERMANENT".equalsIgnoreCase(employeeType.trim())
                        || "CONTRACTOR".equalsIgnoreCase(employeeType.trim()))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee type must be PERMANENT or CONTRACTOR."
            );
        }

        return employeeType.trim().toUpperCase();
    }

    // CREATE
    @Override
    public EmployeeDTO save(EmployeeDTO employee) {

        try {
            String employeeType =
                    validateEmployeeType(employee.getEmployeeType());

            employee.setEmployeeType(employeeType);

            EmployeeDTO savedEmployee = executePmsRequest(() ->
                    restTemplate.postForObject(
                            pmsUrl + "/api/employees",
                            employee,
                            EmployeeDTO.class
                    )
            );

            if (savedEmployee != null) {
                savedEmployee.setEmployeeType(employeeType);
            }

            return savedEmployee;

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email already exists. Please use a different email address."
                );
            }

            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );
        }
    }

    // READ ALL
    @Override
    public List<EmployeeDTO> findAll(String employeeType) {

        String type = validateEmployeeType(employeeType);

        try {
            return executePmsRequest(() ->
                    restTemplate.exchange(
                            pmsUrl + "/api/employees?employeeType=" + type,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<EmployeeDTO>>() {}
                    ).getBody()
            );

        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    "EMS could not retrieve employees."
            );
        }
    }

    // READ BY ID
    @Override
    public EmployeeDTO findById(Long id, String employeeType) {

        String type = validateEmployeeType(employeeType);

        try {
            return executePmsRequest(() ->
                    restTemplate.getForObject(
                            pmsUrl + "/api/employees/" + id
                                    + "?employeeType=" + type,
                            EmployeeDTO.class
                    )
            );

        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    ex.getStatusCode() == HttpStatus.NOT_FOUND
                            ? "Employee not found with ID: " + id
                            : "EMS could not retrieve the employee."
            );
        }
    }

    // UPDATE
    @Override
    public EmployeeDTO update(
            Long id,
            String employeeType,
            EmployeeDTO employee) {

        String type = validateEmployeeType(employeeType);

        if (employee.getEmployeeType() == null
                || !type.equalsIgnoreCase(employee.getEmployeeType().trim())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Employee type in query and body must match."
            );
        }

        employee.setEmployeeType(type);

        try {
            executePmsRequest(() -> {
                restTemplate.put(
                        pmsUrl + "/api/employees/" + id
                                + "?employeeType=" + type,
                        employee
                );
                return null;
            });

            return findById(id, type);

        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    ex.getStatusCode() == HttpStatus.NOT_FOUND
                            ? "Employee not found with ID: " + id
                            : "EMS could not update the employee."
            );
        }
    }

    // DELETE
    @Override
    public void deleteById(Long id, String employeeType) {

        String type = validateEmployeeType(employeeType);

        String url = pmsUrl + "/api/employees/" + id
                + "?employeeType=" + type;

        System.out.println("========== DELETE EMPLOYEE ==========");
        System.out.println("Employee ID: " + id);
        System.out.println("Employee Type: " + type);
        System.out.println("PMS URL: " + url);

        try {
            executePmsRequest(() -> {
                restTemplate.delete(url);
                return null;
            });

            System.out.println("Employee deleted successfully.");

        } catch (HttpClientErrorException ex) {

            System.out.println("PMS Error Status: " + ex.getStatusCode());
            System.out.println("PMS Error Body: " + ex.getResponseBodyAsString());

            throw new ResponseStatusException(
                    ex.getStatusCode(),
                    ex.getStatusCode() == HttpStatus.NOT_FOUND
                            ? "Employee not found with ID: " + id
                            : "PMS could not delete the employee."
            );
        }
    }
}
