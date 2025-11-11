package com.agente.digitalperu.features.customers;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer")
public class CustomerController {
    private final CustomerService customerService;
    @GetMapping
     public String ListCustomer(Model model){
        model.addAttribute("list", customerService.getAllCustomer());
        model.addAttribute("customer", new Customer());
        return "admin/registro-usuario";
    }
    @PostMapping("/save")
    public String addCustomer(@Valid @ModelAttribute Customer customer, BindingResult error, Model model) {
        if (error.hasErrors()) {
            System.out.println("Errores: " + error.getAllErrors());
            model.addAttribute("list", customerService.getAllCustomer());
            return "admin/registro-usuario";
        }
       
        
        customerService.updateAddCustomer(customer);
        return "redirect:/customer";
    }
     @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("customer", customerService.getCustomerById(id));
            model.addAttribute("list", customerService.getAllCustomer());
            return "admin/registro-usuario";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "El cargo con ID " + id + " no fue encontrada.");
            return "admin/registro-usuario";
        }
    }
    
    @PostMapping("/delete")
    public String deleteCustomer(@RequestParam Long id, Model model) {
        try {
            customerService.deleteCustomer(id);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo eliminar el cargo con ID " + id);
        }
        return "redirect:/customer";
    }
}
