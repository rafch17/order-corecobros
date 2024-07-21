package com.banquito.corecobros.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    public void createOrder(OrderDTO dto){
        if(dto.getCode()!=null && orderRepository.existsById(dto.getCode())){
            throw new RuntimeException("El ID " + dto.getCode() + " ya existe.");
        }
        Order order = this.orderMapper.toPersistence(dto);
        Order savedOrder = this.orderRepository.save(order);
        log.info("Se creo la orden: {}", savedOrder);
    }

    public List<OrderDTO> obtainAllOrders(){
        log.info("Va a retornar todas las ordenes");
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(s -> this.orderMapper.toDTO(s)).collect(Collectors.toList());
    }

    public OrderDTO obtainOrderById(Integer id){
        Order order = this.orderRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        return this.orderMapper.toDTO(order);
    }

    public void expireOrders() {
        List<Order> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDate currentDate = now.toLocalDate(); 

        for (Order order : orders) {
            LocalDate endDate = order.getEndDate(); 

            if (endDate.isBefore(currentDate) && !order.getStatus().equals("EXP")) {
                order.setStatus("EXP");
                orderRepository.save(order);
            }
        }
        
    }

    public List<Order> getOrdersByServiceCompanyAndDateRange(Integer serviceId, Integer companyId, LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByServiceIdAndCompanyIdAndDateBetween(serviceId, companyId, startDate, endDate);
    }

    public Order updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("No se encontró la orden con el ID " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
