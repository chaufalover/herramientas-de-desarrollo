package com.agente.digitalperu.features.session;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.agente.digitalperu.features.customers.CustomerService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/session-logs")
@AllArgsConstructor
public class SessionLogController {

    private SessionLogService sessionLogService;
    private CustomerService customerService;

    @GetMapping
    public String verLogs(Model model) {

        List<SessionLog> logs = sessionLogService.findAll();

        List<SessionLogDTO> dtoList = logs.stream().map(log -> {
            var customer = customerService.getCustomerById(log.getCustomerId());

            SessionLogDTO dto = new SessionLogDTO();
            dto.setId(log.getId());
            dto.setEventTime(log.getEventTime());
            dto.setEventType(log.getEventType());

            if (customer != null) {
                dto.setCustomerName(
                        customer.getName() + " " +
                                customer.getLastName() + " - " +
                                customer.getEmail());
            } else {
                dto.setCustomerName("Cliente no encontrado");
            }

            return dto;
        }).toList();

        // Enviar DTOs a la tabla
        model.addAttribute("sessionLogs", dtoList);

        // agrupar por cliente
        Map<String, Long> alertasPorCliente = dtoList.stream()
                .collect(Collectors.groupingBy(SessionLogDTO::getCustomerName, Collectors.counting()));

        model.addAttribute("labels", alertasPorCliente.keySet());
        model.addAttribute("data", alertasPorCliente.values());

        return "admin/alertas";

    }

}
