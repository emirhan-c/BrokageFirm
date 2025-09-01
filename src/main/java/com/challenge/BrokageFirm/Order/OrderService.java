package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Asset.AssetRepository;
import com.challenge.BrokageFirm.Asset.Entity.Asset;
import com.challenge.BrokageFirm.Order.Entity.Order;
import com.challenge.BrokageFirm.Order.Enum.OrderSide;
import com.challenge.BrokageFirm.Order.Enum.OrderStatus;
import com.challenge.BrokageFirm.Order.Model.OrderCreateRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    public Order save(OrderCreateRequest request) {

        Long customerId = request.getCustomerId();
        String assetName = request.getAssetName();
        OrderSide orderSide = request.getOrderSide();
        Double price = request.getPrice();
        Double size = request.getSize();

        Asset tryAsset =  assetRepository.findByCustomerIdAndAssetName(customerId, "TRY").get(0);
        Optional<Asset> targetAsset = null;
        if (!assetName.equalsIgnoreCase("TRY")) {
             targetAsset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName).stream().findFirst();
        }
        if (orderSide.equals(OrderSide.BUY)) {
            double totalCost = size * price;
            if (tryAsset.getUsableSize() < totalCost) {
                throw new RuntimeException("Insufficient TRY balance");
            }
            tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
            tryAsset.setSize(tryAsset.getSize() - totalCost);
            assetRepository.save(tryAsset);
        }
        else  {
            Asset asset = targetAsset.orElseThrow(() -> new RuntimeException("Insufficient asset amount to sell"));
            if (asset.getUsableSize() < size) {
                throw new RuntimeException("Insufficient asset amount to sell");
            }
            asset.setUsableSize(asset.getUsableSize() - size);
            asset.setSize(asset.getSize() - size);
            assetRepository.save(asset);
            // Satışta TRY usableSize artırılır
            tryAsset.setUsableSize(tryAsset.getUsableSize() + (size * price));
            tryAsset.setSize(tryAsset.getSize() + (size * price));
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
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }
    
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }
    
    public void deleteOrder(Long orderId) {
        Order order = findOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Cannot delete order with status: " + order.getStatus() + ". Only PENDING orders can be deleted.");
        }
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY").get(0);
        Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()).get(0);
        if (order.getOrderSide() == OrderSide.BUY) {
            // BUY iptal: asset azaltılır, TRY iade edilir
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            asset.setSize(asset.getSize() - order.getSize());
            tryAsset.setUsableSize(tryAsset.getUsableSize() + order.getSize() * order.getPrice().doubleValue());
            tryAsset.setSize(tryAsset.getSize() + order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(asset);
            assetRepository.save(tryAsset);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            // SELL iptal: asset iade edilir, TRY geri alınır
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            asset.setSize(asset.getSize() + order.getSize());
            tryAsset.setUsableSize(tryAsset.getUsableSize() - order.getSize() * order.getPrice().doubleValue());
            tryAsset.setSize(tryAsset.getSize() - order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(asset);
            assetRepository.save(tryAsset);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public void matchOrder(Long orderId) {
        Order order = findOrderById(orderId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be matched.");
        }
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY").get(0);
        Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()).get(0);
        if (order.getOrderSide() == OrderSide.BUY) {
            // BUY match: asset artırılır
            asset.setUsableSize(asset.getUsableSize() + order.getSize());
            asset.setSize(asset.getSize() + order.getSize());
            assetRepository.save(asset);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            // SELL match: asset azaltılır, TRY artırılır
            asset.setUsableSize(asset.getUsableSize() - order.getSize());
            asset.setSize(asset.getSize() - order.getSize());
            tryAsset.setUsableSize(tryAsset.getUsableSize() + order.getSize() * order.getPrice().doubleValue());
            tryAsset.setSize(tryAsset.getSize() + order.getSize() * order.getPrice().doubleValue());
            assetRepository.save(asset);
            assetRepository.save(tryAsset);
        }
        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
    }
}
