package com.jarviswuod.billingsystem.service;


import com.jarviswuod.billingsystem.repository.CustomerRepository;
import com.jarviswuod.billingsystem.repository.InvoiceRepository;
import com.jarviswuod.billingsystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(CustomerRepository customerRepository, InvoiceRepository invoiceRepository, PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Object> getSummary(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        long totalCustomers = customerRepository.countByCreatedAtBetween(start, end);
        long totalInvoices = invoiceRepository.findByCreatedAtBetween(start, end).size();
        Double totalAmountInvoiced = invoiceRepository.sumAmountByCreatedAtBetween(start, end);
        Double totalAmountPaid = paymentRepository.sumAmountByPaymentDateBetween(startDate, endDate);
        double outstandingBalance = totalAmountInvoiced - totalAmountPaid;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCustomers", totalCustomers);
        summary.put("totalInvoices", totalInvoices);
        summary.put("totalAmountInvoiced", totalAmountInvoiced);
        summary.put("totalAmountPaid", totalAmountPaid);
        summary.put("outstandingBalance", outstandingBalance);

        return summary;
    }

    public List<Map<String, Object>> getTopCustomers(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = paymentRepository.findTop5CustomersByPayments(startDate, endDate);
        List<Map<String, Object>> topCustomers = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("customerName", row[0]);
            entry.put("totalPaid", row[1]);
            topCustomers.add(entry);
        }

        return topCustomers;
    }

    public List<Map<String, Object>> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = paymentRepository.findMonthlyRevenue(startDate, endDate);
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("month", row[0]);
            entry.put("total", row[1]);
            monthlyRevenue.add(entry);
        }

        return monthlyRevenue;
    }
}
