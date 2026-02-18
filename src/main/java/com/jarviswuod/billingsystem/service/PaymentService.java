package com.jarviswuod.billingsystem.service;


import com.jarviswuod.billingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.billingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.billingsystem.model.Invoice;
import com.jarviswuod.billingsystem.model.InvoiceStatus;
import com.jarviswuod.billingsystem.model.Payment;
import com.jarviswuod.billingsystem.repository.InvoiceRepository;
import com.jarviswuod.billingsystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository, InvoiceService invoiceService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    public Payment createPayment(Long invoiceId, Double amount, LocalDate paymentDate,
                                 String paymentMethod, String transactionNumber) {

        // Validate invoice exists
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        // Validate amount is positive
        if (amount == null || amount <= 0) {
            throw new BusinessRuleViolationException("Payment amount must be positive.");
        }

        // Validate payment date is not in the future
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new BusinessRuleViolationException("Payment date cannot be in the future.");
        }

        // Validate no overpayment
        Double totalPaid = paymentRepository.sumPaymentsByInvoiceId(invoiceId);
        if (totalPaid + amount > invoice.getAmount()) {
            throw new BusinessRuleViolationException(
                    "Payment would exceed invoice amount. Outstanding balance: " + (invoice.getAmount() - totalPaid)
            );
        }

        // Create payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionNumber(transactionNumber);

        Payment saved = paymentRepository.save(payment);

        // Update invoice status
        updateInvoiceStatus(invoice, totalPaid + amount);

        return saved;
    }

    private void updateInvoiceStatus(Invoice invoice, Double totalPaid) {
        if (totalPaid >= invoice.getAmount()) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (totalPaid > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING);
        }
        invoiceRepository.save(invoice);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }
}
