package com.agente.digitalperu.features.customers;

import java.util.Collections;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }
     public Customer getCustomerById(Long id){
        return customerRepository.findById(id).orElseThrow();
    }


    public Customer addUser(Customer customer){
            var user = Customer.builder()
                .name(customer.getName())
                .customerType(customer.getCustomerType())
                .docuementType(customer.getDocuementType())
                .docuementNumber(customer.getDocuementNumber())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .registrationDate(customer.getRegistrationDate())
                .status(customer.getStatus())
                .username(customer.getUsername())
                .password(passwordEncoder.encode(customer.getPassword()))
                .build();
            return customerRepository.save(user);
    }

    public Customer updCustomer(Customer customer){
        
        return customerRepository.save(customer);
    }
    
    public void deleteCustomer(Long id){
        customerRepository.deleteById(id);
    }
    public List<Customer> searchByDocumentOrName(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        return customerRepository.findTop10ByDocuementNumberContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }
}
