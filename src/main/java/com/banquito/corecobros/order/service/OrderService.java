package com.banquito.corecobros.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.ItemCollectionMapper;
import com.banquito.corecobros.order.util.mapper.OrderMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemCollectionService itemCollectionService;
    private final ItemCollectionRepository itemCollectionRepository;
    private final ItemAutomaticDebitRepository itemAutomaticDebitRepository;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ItemCollectionService itemCollectionService, ItemCollectionRepository itemCollectionRepository, ItemAutomaticDebitRepository itemAutomaticDebitRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemCollectionService = itemCollectionService;
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemAutomaticDebitRepository = itemAutomaticDebitRepository;
    }
    
    public void createOrder(MultipartFile file, OrderDTO dto){
        if(dto.getOrderId()!=null && orderRepository.existsById(dto.getOrderId())){
            throw new RuntimeException("El ID " + dto.getOrderId() + " ya existe.");
        }
        Order order = this.orderMapper.toPersistence(dto);
        order.setStatus("PEN");
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

    public List<OrderDTO> getActiveOrdersByServiceId(Integer serviceId) {
        List<Order> orders = orderRepository.findActiveOrdersByServiceId(serviceId);
        return orders.stream()
                     .map(orderMapper::toDTO)
                     .collect(Collectors.toList());
    }

    @Transactional
    public void expireItemsAfterOrderEndDate() {
        LocalDate today = LocalDate.now();
        List<Order> orders = orderRepository.findByEndDateBeforeAndStatus(today, "ACT");
        log.info("order {}", orders);
        if (orders.isEmpty()) {
            log.info("vacio");
            return;
        }

        List<Integer> orderIds = orders.stream()
                                       .map(Order::getOrderId)
                                       .collect(Collectors.toList());

        List<ItemCollection> items = itemCollectionRepository.findByOrderIdIn(orderIds);
        List<ItemAutomaticDebit> itemsAD = itemAutomaticDebitRepository.findByOrderIdIn(orderIds);

        for (ItemCollection item : items) {
            log.info("cambio de estado en item");
            item.setStatus("EXP");
            itemCollectionRepository.save(item);
        }

        for (ItemAutomaticDebit itemAD : itemsAD) {
            log.info("cambio de estado en itemAD {}", itemAD.getId());
            itemAD.setStatus("EXP");
            itemAutomaticDebitRepository.save(itemAD);
        }

        for (Order order : orders) {
            log.info("cambio de estado en orden");
            order.setStatus("EXP");
            orderRepository.save(order);
        }
    }

    @Scheduled(cron = "0 * * * * ?") // Run daily at midnight
    @Async
    public void updateExpiredItems() {
        this.expireItemsAfterOrderEndDate();
    }

    // @Scheduled(cron = "0 0 13 * * ?") // Ejecuta a la 1 p.m. todos los días
    // @Async
    // @Transactional
    // public void processAutomaticDebits() {
    //     log.info("Starting automatic debit processing...");

    //     // Filtra las órdenes activas con service_id = 2
    //     List<OrderDTO> orders = this.getActiveOrdersByServiceId(2);
    //     for (OrderDTO order : orders) {
    //         // Obtener todos los items relacionados con la orden
    //         List<ItemCollectionDTO> items = itemCollectionService.getItemCollectionsByOrderId(order.getOrderId());

    //         // Procesar el débito automático (aquí se asume que existe un método debitProcessingService.processDebits que trabaja con DTOs)
    //         Map<String, List<ItemCollectionDTO>> debitResults = debitProcessingService.processDebits(items);

    //         // Realizar la transacción del débito automático llamando a otro módulo
    //         RestClient restClient = RestClient.builder().baseUrl("http://otro-modulo/api/debit").build();
    //         for (ItemCollectionDTO item : items) {
    //             restClient.post()
    //                 .uri("/process")
    //                 .bodyValue(item)
    //                 .retrieve()
    //                 .bodyToMono(String.class)
    //                 .block();
    //         }

    //         // Log de resultados
    //         log.info("Order ID: {} - Fully processed items: {}, Partially processed items: {}",
    //             order.getOrderId(),
    //             debitResults.get("fullyProcessed").size(),
    //             debitResults.get("partiallyProcessed").size());
    //     }

    //     log.info("Automatic debit processing completed.");
    // }

}
