package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Order.Entity.Order;
import com.challenge.BrokageFirm.Order.Model.OrderCreateRequest;
import com.challenge.BrokageFirm.Order.Model.OrderListRequest;
import com.challenge.BrokageFirm.Customer.CustomerRepository;
import com.challenge.BrokageFirm.Customer.Entity.Customer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public String createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User not found."));
            if (!request.getCustomerId().equals(customer.getCustomerId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to perform this action for another customer.");
            }
        }
        Order savedOrder = orderService.save(request);
        return "Order with ID " + savedOrder.getId() + " has been successfully created.";
    }

    @GetMapping("/list")
    public List<Order> listOrders(@Valid @RequestBody OrderListRequest request) {
        return orderService.list(request.getCustomerId(), request.getStartDate(), request.getEndDate());
    }

    @DeleteMapping("/delete/{orderId}")
    public String deleteOrder(@PathVariable("orderId") Long orderId) {

        orderService.deleteOrder(orderId);
        return "Order with ID " + orderId + " has been successfully deleted.";
    }

    @PostMapping("/match/{orderId}")
    public String matchOrder(@PathVariable("orderId") Long orderId) {
        orderService.matchOrder(orderId);
        return "Order with ID " + orderId + " has been matched successfully.";
    }
}
