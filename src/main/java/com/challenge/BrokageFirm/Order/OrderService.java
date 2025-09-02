package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Asset.AssetRepository;
import com.challenge.BrokageFirm.Asset.Entity.Asset;
import com.challenge.BrokageFirm.Customer.CustomerRepository;
import com.challenge.BrokageFirm.Customer.Entity.Customer;
import com.challenge.BrokageFirm.Order.Entity.Order;
import com.challenge.BrokageFirm.Order.Enum.OrderSide;
import com.challenge.BrokageFirm.Order.Enum.OrderStatus;
import com.challenge.BrokageFirm.Order.Model.OrderCreateRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.customerRepository = customerRepository;
    }

    public Order save(OrderCreateRequest request) {
        checkAuth(request.getCustomerId());
        Long customerId = request.getCustomerId();
        String assetName = request.getAssetName();
        OrderSide orderSide = request.getOrderSide();
        Double price = request.getPrice();
        Double size = request.getSize();

        Asset tryAsset =  assetRepository.findByCustomerIdAndAssetName(customerId, "TRY").getFirst();
        Optional<Asset> targetAsset = Optional.empty();
        if (!assetName.equalsIgnoreCase("TRY")) {
            targetAsset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName).stream().findFirst();
        }
        if (orderSide.equals(OrderSide.BUY)) {
            double totalCost = size * price;
            if (tryAsset.getUsableSize() < totalCost) {
                throw new RuntimeException("Insufficient TRY balance");
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
            assetRepository.save(tryAsset);
        }
        else  {
            Asset asset = targetAsset.orElseThrow(() -> new RuntimeException("Insufficient asset amount to sell"));
            if (asset.getUsableSize() < size) {
                throw new RuntimeException("Insufficient asset amount to sell");
            }
            asset.setUsableSize(asset.getUsableSize() - size);
            assetRepository.save(asset);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + (size * price));
            assetRepository.save(tryAsset);
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(orderSide);
        order.setSize(size);
        order.setPrice(BigDecimal.valueOf(price));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDate.now());
        return orderRepository.save(order);
    }
    
    public List<Order> list(Long customerId, LocalDate startDate, LocalDate endDate) {
        checkAuth(customerId);
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }
    
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }
    
    public void deleteOrder(Long orderId) {
        Order order = findOrderById(orderId);
        checkAuth(order.getCustomerId());
        if (order.getStatus() != OrderStatus.PENDING) {
          throw new RuntimeException ("Cannot delete order with status: " + order.getStatus() + ". Only PENDING orders can be deleted.");
        }
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY").getFirst();
        List<Asset> assetList = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
        if (order.getOrderSide() == OrderSide.BUY) {
            if (!assetList.isEmpty()) {
                Asset asset = assetList.getFirst();
                asset.setUsableSize(asset.getUsableSize() - order.getSize());
                assetRepository.save(asset);
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() + order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(tryAsset);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            if (!assetList.isEmpty()) {
                Asset asset = assetList.getFirst();
                asset.setUsableSize(asset.getUsableSize() + order.getSize());
                assetRepository.save(asset);
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(tryAsset);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public void matchOrder(Long orderId) {
        Order order = findOrderById(orderId);
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if(!isAdmin){
            throw new RuntimeException("Only admins can match orders.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be matched.");
        }
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY").getFirst();
        if (order.getOrderSide() == OrderSide.BUY) {

            tryAsset.setSize(tryAsset.getSize() - order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(tryAsset);

            List<Asset> assetList = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
            if (assetList.isEmpty()) {
                createNewAsset(order.getCustomerId(), order.getAssetName(), order.getSize());
            } else {
                Asset asset = assetList.getFirst();
                asset.setSize(asset.getSize() + order.getSize());
                assetRepository.save(asset);
            }
        } else if (order.getOrderSide() == OrderSide.SELL) {

            tryAsset.setSize(tryAsset.getSize() + order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(tryAsset);

            List<Asset> assetList = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
            if (!assetList.isEmpty()) {
                Asset asset = assetList.getFirst();
                if (asset.getSize().equals(order.getSize())) {
                    assetRepository.delete(asset);
                    return;
                }
                asset.setSize(asset.getSize() - order.getSize());
                assetRepository.save(asset);
            }
        }
        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
    }

    private void createNewAsset(Long customerId, String assetName, Double size) {
        Asset newAsset = new Asset();
        newAsset.setCustomerId(customerId);
        newAsset.setAssetName(assetName);
        newAsset.setSize(size);
        newAsset.setUsableSize(size);
        assetRepository.save(newAsset);
    }

    private void checkAuth(Long customerId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User not found."));
            if (!customerId.equals(customer.getCustomerId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to perform this action for another customer.");
            }
        }
    }
}
