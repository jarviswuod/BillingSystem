package com.jarviswuod.billingsystem.service;

import com.jarviswuod.billingsystem.exception.BusinessRuleViolationException;
import com.jarviswuod.billingsystem.exception.ResourceNotFoundException;
import com.jarviswuod.billingsystem.model.Customer;
import com.jarviswuod.billingsystem.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public Customer createCustomer(Customer customer) {
        repo.findByEmail(customer.getEmail()).ifPresent(c -> {
            throw new BusinessRuleViolationException("Email already exists: " + customer.getEmail());
        });
        return repo.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }

    public Customer getCustomerById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);

        // If email is changing, check it's not taken by someone else
        if (!existing.getEmail().equals(updatedCustomer.getEmail())) {
            repo.findByEmail(updatedCustomer.getEmail()).ifPresent(c -> {
                throw new BusinessRuleViolationException("Email already exists: " + updatedCustomer.getEmail());
            });
        }

        existing.setName(updatedCustomer.getName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPhone(updatedCustomer.getPhone());

        return repo.save(existing);
    }

    public void deleteCustomer(Long id) {
        Customer existing = getCustomerById(id);
        repo.delete(existing);
    }
}