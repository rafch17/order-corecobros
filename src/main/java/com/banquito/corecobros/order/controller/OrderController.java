package com.banquito.corecobros.order.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
    RequestMethod.PUT })
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(this.orderService.obtainAllOrders());
    }


    @PostMapping(value = "/automatic-debit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createOrderAD(
            @RequestPart("file") MultipartFile file,
            @RequestPart("order") OrderDTO orderDTO) {
        try {
            this.orderService.createOrderAutomaticDebit(file, orderDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/collection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createOrderCollection(
            @RequestPart("file") MultipartFile file,
            @RequestPart("order") OrderDTO orderDTO) {
        try {
            this.orderService.createOrderCollection(file, orderDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(this.orderService.obtainOrderById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/expire")
    public ResponseEntity<Void> expireOrders() {
        orderService.expireOrders();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uniqueId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String uniqueId, @RequestParam String status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(uniqueId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderDTO>> getOrdersByCriteria(
            @RequestParam Integer serviceId,
            @RequestParam Integer accountId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<OrderDTO> orders = orderService.getOrdersByServiceIdAndAccountIdAndDateRange(
                serviceId, accountId, startDate, endDate);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/service/{serviceId}")
    public List<OrderDTO> getOrdersByServiceId(@PathVariable Integer serviceId) {
        return orderService.getActiveOrdersByServiceId(serviceId);
    }
}
