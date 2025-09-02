package com.challenge.BrokageFirm.Customer;

import com.challenge.BrokageFirm.Customer.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByUsername(String username);
}

