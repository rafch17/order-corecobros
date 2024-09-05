package com.banquito.corecobros.order.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.service.OrderService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderDTO = OrderDTO.builder()
                .serviceId("JXM0025321")
                .companyUid("JXM0025321")
                .accountId("JXM0025321")
                .totalAmount(BigDecimal.ZERO)
                .status("PEN")
                .build();
        file = mock(MultipartFile.class);
    }

    @Test
    void testCreateOrderCollection() {
        ResponseEntity<Void> response = orderController.createOrderCollection(file, orderDTO);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testExpireOrders() {
        ResponseEntity<Void> response = orderController.expireOrders();

        assertEquals(200, response.getStatusCode().value());
    }

}
