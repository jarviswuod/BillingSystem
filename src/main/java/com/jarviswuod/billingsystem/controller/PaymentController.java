package com.jarviswuod.billingsystem.controller;


import com.jarviswuod.billingsystem.model.Payment;
import com.jarviswuod.billingsystem.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Map<String, Object> body) {
        Long invoiceId = Long.valueOf(body.get("invoiceId").toString());
        Double amount = Double.valueOf(body.get("amount").toString());
        LocalDate paymentDate = LocalDate.parse(body.get("paymentDate").toString());
        String paymentMethod = body.get("paymentMethod").toString();
        String transactionNumber = body.get("transactionNumber").toString();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(invoiceId, amount, paymentDate, paymentMethod, transactionNumber));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}
