package com.agente.digitalperu.features.accounts;

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

import com.agente.digitalperu.features.accountType.AccountTypeService;
import com.agente.digitalperu.features.customers.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("account")
public class AccountController {
    private final AccountService accountService;
    private final AccountTypeService accountTypeService;
    private final CustomerService customerService;

    @GetMapping
    public String ListAccount(Model model) {
        model.addAttribute("list", accountService.getAllAccount());
        model.addAttribute("type", accountTypeService.getAllAccountType());
        model.addAttribute("account", new Account());
        return "admin/registro-cuenta";
    }

    @PostMapping("/save")
    public String addAccount(@Valid @ModelAttribute Account account, BindingResult error, Model model) {
        if (error.hasErrors()) {
            model.addAttribute("list", accountService.getAllAccount());
            model.addAttribute("type", accountTypeService.getAllAccountType());
            return "admin/registro-cuenta";
        }

        try {
            accountService.updateAddAccount(account);
        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());
            model.addAttribute("list", accountService.getAllAccount());
            model.addAttribute("type", accountTypeService.getAllAccountType());
            model.addAttribute("account", account);
            
            if (account.getCustomer() != null && account.getCustomer().getId() != null) {
                model.addAttribute("clienteSeleccionado", account.getCustomer());
            }

            return "admin/registro-cuenta";
        }

        return "redirect:/account";
    }

    @GetMapping("/edit/{id}")
    public String editAccount(@PathVariable Long id, Model model) {
        try {
            var account = accountService.getAccountById(id);

            model.addAttribute("account", account);
            model.addAttribute("list", accountService.getAllAccount());
            model.addAttribute("type", accountTypeService.getAllAccountType());

            if (account.getCustomer() != null) {
                model.addAttribute("clienteSeleccionado", account.getCustomer());
                model.addAttribute("mensajeBusqueda",
                        "Cliente actual: " + account.getCustomer().getName() + " (DNI: "
                                + account.getCustomer().getDocuementNumber() + ")");
            }

            return "admin/registro-cuenta";
        } catch (NoSuchElementException e) {
            model.addAttribute("error", "La cuenta con ID " + id + " no fue encontrada.");
            return "admin/registro-cuenta";
        }
    }

    @PostMapping("/delete")
    public String deleteAccount(@RequestParam Long id, Model model) {
        try {
            accountService.deleteAccount(id);
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo eliminar el cargo con ID " + id);
        }
        return "redirect:/account";
    }

    @GetMapping("/buscar-cliente")
    public String buscarCliente(@RequestParam("q") String query, Model model) {
        var resultados = customerService.searchByDocumentOrName(query);

        model.addAttribute("list", accountService.getAllAccount());
        model.addAttribute("type", accountTypeService.getAllAccountType());
        model.addAttribute("account", new Account());

        if (resultados.isEmpty()) {
            model.addAttribute("mensajeBusqueda", "No se encontró ningún cliente con ese dato.");
        } else if (resultados.size() == 1) {
            var cliente = resultados.get(0);
            model.addAttribute("mensajeBusqueda",
                    "Cliente encontrado: " + cliente.getName() + " (DNI: " + cliente.getDocuementNumber() + ")");
            model.addAttribute("clienteSeleccionado", cliente);
        } else {
            model.addAttribute("mensajeBusqueda",
                    "Se encontraron " + resultados.size() + " clientes, por favor afina la búsqueda.");
        }

        return "admin/registro-cuenta";
    }
}
