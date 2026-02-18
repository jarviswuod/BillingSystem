package com.jarviswuod.billingsystem.repository;

import com.jarviswuod.billingsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.invoice.id = :invoiceId")
    Double sumPaymentsByInvoiceId(@Param("invoiceId") Long invoiceId);

    // --

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end")
    Double sumAmountByPaymentDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
                SELECT p.invoice.customer.name, SUM(p.amount)
                FROM Payment p
                WHERE p.paymentDate BETWEEN :start AND :end
                GROUP BY p.invoice.customer.id, p.invoice.customer.name
                ORDER BY SUM(p.amount) DESC
                LIMIT 5
            """)
    List<Object[]> findTop5CustomersByPayments(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
                SELECT FUNCTION('TO_CHAR', p.paymentDate, 'YYYY-MM'), SUM(p.amount)
                FROM Payment p
                WHERE p.paymentDate BETWEEN :start AND :end
                GROUP BY FUNCTION('TO_CHAR', p.paymentDate, 'YYYY-MM')
                ORDER BY FUNCTION('TO_CHAR', p.paymentDate, 'YYYY-MM')
            """)
    List<Object[]> findMonthlyRevenue(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
