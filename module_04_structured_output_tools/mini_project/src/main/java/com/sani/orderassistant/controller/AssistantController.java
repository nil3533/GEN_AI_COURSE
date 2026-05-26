package com.sani.orderassistant.controller;

import com.sani.orderassistant.dto.AssistantRequest;
import com.sani.orderassistant.dto.AssistantResponse;
import com.sani.orderassistant.order.OrderService;
import com.sani.orderassistant.order.OrderSummary;
import com.sani.orderassistant.service.AssistantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AssistantController {

    private final AssistantService assistantService;

    private final OrderService orderService;

    public AssistantController(AssistantService assistantService, OrderService orderService) {
        this.assistantService = assistantService;
        this.orderService = orderService;
    }

    @PostMapping("/assistant")
    public AssistantResponse assistant(@Valid @RequestBody AssistantRequest request) {
        return assistantService.respond(request);
    }

    @GetMapping("/assistant/providers")
    public Map<String, Object> providers() {
        return assistantService.providerSummary();
    }

    @GetMapping("/orders/{customerId}")
    public List<OrderSummary> orders(@PathVariable String customerId) {
        return orderService.recentOrders(customerId);
    }
}
