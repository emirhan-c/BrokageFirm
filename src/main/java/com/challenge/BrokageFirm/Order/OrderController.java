package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Order.Entity.Order;
import com.challenge.BrokageFirm.Order.Enum.OrderStatus;
import com.challenge.BrokageFirm.Order.Model.OrderCreateRequest;
import com.challenge.BrokageFirm.Order.Model.OrderListRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public String createOrder(@Valid @RequestBody OrderCreateRequest request) {
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
