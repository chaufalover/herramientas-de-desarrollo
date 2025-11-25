package com.agente.digitalperu.features.customers;

import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.agente.digitalperu.features.face.FaceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer")
public class CustomerController {

    private final CustomerService customerService;
    private final FaceService faceService;

    @GetMapping
    public String viewCustomer(Model model,
            @RequestParam(required = false) Long newId) {

        if (newId != null) {
            // Cuando se acaba de registrar el rostro
            Customer c = customerService.getCustomerById(newId);
            model.addAttribute("customer", c);
        } else {
            // Cuando se entra normalmente al formulario
            model.addAttribute("customer", new Customer());
        }

        model.addAttribute("list", customerService.getAllCustomer());
        return "admin/registro-usuario";
    }

    @PostMapping("/save")
    public String addCustomer(@Valid @ModelAttribute Customer customer, BindingResult error, Model model) {
        if (error.hasErrors()) {
            System.out.println("Errores: " + error.getAllErrors());
            // asegurarse de que la plantilla tenga el objeto customer y la lista
            model.addAttribute("customer", customer);
            model.addAttribute("list", customerService.getAllCustomer());
            return "admin/registro-usuario";
        }
        Customer saved = customerService.addUser(customer);
        return "redirect:/customer?newId=" + saved.getId();
    }

    @PostMapping(path = "/register/face", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> registrarRostro(@RequestBody Map<String, Object> payload) {
        try {
            Object idObj = payload.get("customerId");
            if (idObj == null) return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "customerId requerido"));

            Long customerId;
            if (idObj instanceof Number) {
                customerId = ((Number) idObj).longValue();
            } else {
                customerId = Long.valueOf(idObj.toString());
            }

            String imageBase64 = (String) payload.get("imageBase64");
            if (imageBase64 == null || imageBase64.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("ok", false, "error", "imageBase64 vacío"));
            }

            // llamar con (customerId, imageBase64) — orden según FaceService
            String path = faceService.registerFace(customerId, imageBase64);

            Customer customer = customerService.getCustomerById(customerId);
            customer.setFaceEncodingPath(path);
            customerService.updCustomer(customer);
            return ResponseEntity.ok(Map.of("ok", true, "path", path));
        } catch (Exception e) {
            System.out.println("Error registrando rostro" + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("ok", false, "error", e.getMessage()));
        }
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
