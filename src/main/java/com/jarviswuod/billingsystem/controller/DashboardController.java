package com.jarviswuod.billingsystem.controller;

import com.jarviswuod.billingsystem.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(dashboardService.getSummary(startDate, endDate));
    }

    @GetMapping("/top-customers")
    public ResponseEntity<List<Map<String, Object>>> getTopCustomers(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(dashboardService.getTopCustomers(startDate, endDate));
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(dashboardService.getMonthlyRevenue(startDate, endDate));
    }
}
