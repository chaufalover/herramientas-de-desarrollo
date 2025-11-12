package com.agente.digitalperu.features.customers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
    List<Customer> findTop10ByDocuementNumberContainingIgnoreCaseOrNameContainingIgnoreCase(String doc, String name);
}
