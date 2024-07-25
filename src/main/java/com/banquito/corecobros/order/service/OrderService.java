package com.banquito.corecobros.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final ItemCollectionService itemCollectionService;


    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ItemCollectionService itemCollectionService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemCollectionService = itemCollectionService;
    }

    public void createOrder(MultipartFile file, OrderDTO dto){
        if(dto.getOrderId()!=null && orderRepository.existsById(dto.getOrderId())){
            throw new RuntimeException("El ID " + dto.getOrderId() + " ya existe.");
        }
        Order order = this.orderMapper.toPersistence(dto);
        //order.setStatus("PEN");
        Order savedOrder = this.orderRepository.save(order);
        log.info("Se creo la orden: {}", savedOrder);

        try {
            itemCollectionService.processCsvFile(file, savedOrder.getOrderId());
        } catch (Exception e) {
            log.info("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV");
        }
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

    public OrderDTO updateOrderStatus(String uniqueId, String status) {
        Order order = orderRepository.findByUniqueId(uniqueId);
        if(order != null){
            log.info("Se va a cambiar el status a la orden");
            order.setStatus(status);
            orderRepository.save(order);
            return this.orderMapper.toDTO(order);
        }
        return null;
    }

    public List<OrderDTO> getOrdersByServiceIdAndAccountIdAndDateRange(
        Integer serviceId, Integer accountId, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByServiceIdAndAccountIdAndDateRange(
            serviceId, accountId, startDate, endDate);
        return orders.stream().map(s -> this.orderMapper.toDTO(s)).collect(Collectors.toList());
    }

}
