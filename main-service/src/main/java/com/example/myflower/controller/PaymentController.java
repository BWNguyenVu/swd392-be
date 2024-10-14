package com.example.myflower.controller;

import com.example.myflower.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@CrossOrigin("**")
public class PaymentController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PaymentService paymentService;

    @PostMapping(path = "/confirm-webhook")
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
    {
       ObjectNode response = objectMapper.createObjectNode();
        try {
            paymentService.payosTransferHandler(body);
            response.put("error", 0);
            response.put("message", "Webhook delivered");
            response.set("data", null);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

}
