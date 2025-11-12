package com.agente.digitalperu.features.customers;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    public List<Customer> getAllCustomer(){
        return customerRepository.findAll();
    }
     public Customer getCustomerById(Long id){
        return customerRepository.findById(id).orElseThrow();
    }


    public Customer updateAddCustomer(Customer customer){
        
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
