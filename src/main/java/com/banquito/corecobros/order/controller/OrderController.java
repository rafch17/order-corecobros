package com.banquito.corecobros.order.controller;

import java.time.LocalDate;
import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@Slf4j
@RestController
@RequestMapping("/order-microservice/api/v1/orders")
@Tag(name = "OrderController", description = "APIs related to Orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get all orders", description = "Fetches a list of all orders.")
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(this.orderService.obtainAllOrders());
    }

    @Operation(summary = "Create a new automatic debit order", description = "Creates a new automatic debit order with the provided file and order details.")
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

    @Operation(summary = "Create a new collection order", description = "Creates a new collection order with the provided file and order details.")
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

    @Operation(summary = "Get an order by ID", description = "Fetches the details of an order by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(this.orderService.obtainOrderById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Expire orders", description = "Changes the status of all orders to expired.")
    @PutMapping("/expire")
    public ResponseEntity<Void> expireOrders() {
        orderService.expireOrders();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update order status", description = "Updates the status of an order based on its unique ID.")
    @PutMapping("/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@RequestParam String uniqueId, @RequestParam String status) {
        try {
            OrderDTO updatedOrder = orderService.updateOrderStatus(uniqueId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Search orders by criteria", description = "Fetches orders based on service ID, account ID, and a date range.")
    @GetMapping("/search")
    public ResponseEntity<List<OrderDTO>> getOrdersByCriteria(
            @RequestParam String serviceId,
            @RequestParam String accountId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByServiceIdAndAccountIdAndDateRange(
                serviceId, accountId, startDate, endDate);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get orders by service ID", description = "Fetches all active orders for a specific service ID.")
    @GetMapping("/service/{serviceId}")
    public List<OrderDTO> getOrdersByServiceId(@PathVariable String serviceId) {
        return orderService.getActiveOrdersByServiceId(serviceId);
    }

    @GetMapping("/order-status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }

    @GetMapping("/active/{serviceId}")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersByServiceId(@PathVariable String serviceId) {
        List<OrderDTO> activeOrders = orderService.getActiveOrdersByServiceId(serviceId);
        return ResponseEntity.ok(activeOrders);
    }
}
