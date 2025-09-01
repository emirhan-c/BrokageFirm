package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Order.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateBetween(
        Long customerId,
        LocalDate startDate, 
        LocalDate endDate
    );
}
