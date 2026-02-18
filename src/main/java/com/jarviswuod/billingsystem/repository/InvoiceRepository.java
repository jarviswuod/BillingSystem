package com.jarviswuod.billingsystem.repository;


import com.jarviswuod.billingsystem.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // --
    List<Invoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i WHERE i.createdAt BETWEEN :start AND :end")
    Double sumAmountByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // --
    @Query("""
                SELECT i FROM Invoice i
                WHERE i.dueDate < :today
                AND i.status <> 'PAID'
                AND (:customerId IS NULL OR i.customer.id = :customerId)
                AND (CAST(:start AS java.time.LocalDateTime) IS NULL OR i.createdAt >= :start)
                AND (CAST(:end AS java.time.LocalDateTime) IS NULL OR i.createdAt <= :end)
            """)
    List<Invoice> findOverdueInvoices(
            @Param("today") LocalDate today,
            @Param("customerId") Long customerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}