package com.jarviswuod.billingsystem.service;


import com.jarviswuod.billingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.billingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.billingsystem.model.Customer;
import com.jarviswuod.billingsystem.model.Invoice;
import com.jarviswuod.billingsystem.model.InvoiceStatus;
import com.jarviswuod.billingsystem.repository.InvoiceRepository;
import com.jarviswuod.billingsystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final PaymentRepository paymentRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerService customerService, PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerService = customerService;
        this.paymentRepository = paymentRepository;
    }

    public Invoice createInvoice(Long customerId, Double amount, LocalDate dueDate) {
        // Validate customer exists
        Customer customer = customerService.getCustomerById(customerId);

        // Validate amount
        if (amount == null || amount <= 0) {
            throw new BusinessRuleViolationException("Invoice amount must be positive and non-zero.");
        }

        // Validate due date is in the future
        if (dueDate == null || !dueDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleViolationException("Due date must be in the future.");
        }

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setAmount(amount);
        invoice.setDueDate(dueDate);
        invoice.setStatus(InvoiceStatus.PENDING);

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = getInvoiceById(id);

        if (!invoice.getStatus().equals(InvoiceStatus.PENDING)) {
            throw new BusinessRuleViolationException("Cannot delete an invoice that has payments.");
        }

        invoiceRepository.delete(invoice);
    }

    // --
    public List<Map<String, Object>> getOverdueInvoices(Long customerId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now(), customerId, start, end);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Invoice invoice : overdueInvoices) {
            Double totalPaid = paymentRepository.sumPaymentsByInvoiceId(invoice.getId());
            double balance = invoice.getAmount() - totalPaid;
            long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(invoice.getDueDate(), LocalDate.now());

            Map<String, Object> entry = new java.util.HashMap<>();
            entry.put("invoiceId", invoice.getId());
            entry.put("customerName", invoice.getCustomer().getName());
            entry.put("amount", invoice.getAmount());
            entry.put("amountPaid", totalPaid);
            entry.put("balance", balance);
            entry.put("dueDate", invoice.getDueDate().toString());
            entry.put("daysOverdue", daysOverdue);
            entry.put("status", "OVERDUE");

            result.add(entry);
        }

        return result;
    }
}