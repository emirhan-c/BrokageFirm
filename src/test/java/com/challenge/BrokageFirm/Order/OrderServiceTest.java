package com.challenge.BrokageFirm.Order;

import com.challenge.BrokageFirm.Asset.AssetRepository;
import com.challenge.BrokageFirm.Asset.Entity.Asset;
import com.challenge.BrokageFirm.Order.Entity.Order;
import com.challenge.BrokageFirm.Order.Enum.OrderSide;
import com.challenge.BrokageFirm.Order.Enum.OrderStatus;
import com.challenge.BrokageFirm.Order.Model.OrderCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AssetRepository assetRepository;
    @InjectMocks
    private OrderService orderService;

    private Asset tryAsset;
    private Asset asset;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tryAsset = new Asset();
        tryAsset.setId(1L);
        tryAsset.setCustomerId(123L);
        tryAsset.setAssetName("TRY");
        tryAsset.setUsableSize(10000.0);
        tryAsset.setSize(10000.0);

        asset = new Asset();
        asset.setId(2L);
        asset.setCustomerId(123L);
        asset.setAssetName("AAPL");
        asset.setUsableSize(100.0);
        asset.setSize(100.0);

        order = new Order();
        order.setId(1L);
        order.setCustomerId(123L);
        order.setAssetName("AAPL");
        order.setOrderSide(OrderSide.BUY);
        order.setSize(10.0);
        order.setPrice(BigDecimal.valueOf(10.0));
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDate.now());
    }

    @Test
    void testSaveBuy() {
        OrderCreateRequest req = new OrderCreateRequest();
        req.setCustomerId(123L);
        req.setAssetName("AAPL");
        req.setOrderSide(OrderSide.BUY);
        req.setSize(10.0);
        req.setPrice(10.0);

        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order saved = orderService.save(req);
        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(9900.0, tryAsset.getUsableSize());
    }

    @Test
    void testSaveSell() {
        OrderCreateRequest req = new OrderCreateRequest();
        req.setCustomerId(123L);
        req.setAssetName("AAPL");
        req.setOrderSide(OrderSide.SELL);
        req.setSize(10.0);
        req.setPrice(10.0);

        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order saved = orderService.save(req);
        assertEquals(OrderStatus.PENDING, saved.getStatus());
        assertEquals(90.0, asset.getUsableSize());
        assertEquals(10100.0, tryAsset.getUsableSize());
    }

    @Test
    void testMatchOrderBuy() {
        order.setOrderSide(OrderSide.BUY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));

        orderService.matchOrder(1L);
        assertEquals(OrderStatus.MATCHED, order.getStatus());
        assertEquals(110.0, asset.getUsableSize());
    }

    @Test
    void testMatchOrderSell() {
        order.setOrderSide(OrderSide.SELL);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));

        orderService.matchOrder(1L);
        assertEquals(OrderStatus.MATCHED, order.getStatus());
        assertEquals(90.0, asset.getUsableSize());
        assertEquals(10100.0, tryAsset.getUsableSize());
    }

    @Test
    void testDeleteOrderBuy() {
        order.setOrderSide(OrderSide.BUY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));

        orderService.deleteOrder(1L);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(90.0, asset.getUsableSize());
        assertEquals(10100.0, tryAsset.getUsableSize());
    }

    @Test
    void testDeleteOrderSell() {
        order.setOrderSide(OrderSide.SELL);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "TRY")).thenReturn(List.of(tryAsset));
        when(assetRepository.findByCustomerIdAndAssetName(123L, "AAPL")).thenReturn(List.of(asset));

        orderService.deleteOrder(1L);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(110.0, asset.getUsableSize());
        assertEquals(9900.0, tryAsset.getUsableSize());
    }
}

