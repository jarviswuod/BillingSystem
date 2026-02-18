package com.jarviswuod.billingsystem.controller;

import com.jarviswuod.billingsystem.model.Invoice;
import com.jarviswuod.billingsystem.service.InvoiceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")

public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        Double amount = Double.valueOf(body.get("amount").toString());
        LocalDate dueDate = LocalDate.parse(body.get("dueDate").toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(customerId, amount, dueDate));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    // -- --
    @GetMapping("/overdue")
    public ResponseEntity<List<Map<String, Object>>> getOverdueInvoices(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(invoiceService.getOverdueInvoices(customerId, startDate, endDate));
    }
}