package com.banquito.corecobros.order.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.OrderMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ItemCollectionService itemCollectionService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderCollection() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        OrderDTO dto = OrderDTO.builder()
                .uniqueId("XEV0019390")
                .serviceId("JXM0025321")
                .accountId("JXM0025321")
                .status("PEN")
                .build();
        Order order = new Order();
        order.setOrderId(1);
        order.setUniqueId("XEV0019390");
        order.setServiceId("JXM0025321");
        order.setAccountId("JXM0025321");
        order.setStatus("PEN");

        when(orderMapper.toPersistence(any(OrderDTO.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderRepository.existsByUniqueId(anyString())).thenReturn(false);
        when(itemCollectionService.processCsvFile(any(MultipartFile.class), any(Integer.class), anyString(),
                anyString()))
                .thenReturn(BigDecimal.valueOf(10));

        orderService.createOrderCollection(file, dto);

        verify(orderRepository, times(2)).save(order);
        assertEquals(BigDecimal.valueOf(10), order.getTotalAmount());
    }

}
