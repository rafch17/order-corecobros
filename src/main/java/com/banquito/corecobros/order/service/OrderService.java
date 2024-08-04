package com.banquito.corecobros.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import com.banquito.corecobros.order.dto.AccountTransactionDTO;
import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.dto.ResponseTransactionDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.OrderMapper;
import com.banquito.corecobros.order.util.uniqueId.UniqueIdGeneration;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemCollectionService itemCollectionService;
    private final ItemCollectionRepository itemCollectionRepository;
    private final ItemAutomaticDebitService itemAutomaticDebitService;
    private final ItemAutomaticDebitRepository itemAutomaticDebitRepository;
    private final AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository;
    private final PaymentRecordService paymentRecordService;


    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper,
            ItemCollectionService itemCollectionService, ItemCollectionRepository itemCollectionRepository,
            ItemAutomaticDebitService itemAutomaticDebitService,
            ItemAutomaticDebitRepository itemAutomaticDebitRepository,
            AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository,
            PaymentRecordService paymentRecordService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.itemCollectionService = itemCollectionService;
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemAutomaticDebitService = itemAutomaticDebitService;
        this.itemAutomaticDebitRepository = itemAutomaticDebitRepository;
        this.automaticDebitPaymentRecordRepository = automaticDebitPaymentRecordRepository;
        this.paymentRecordService = paymentRecordService;
    }


    public void createOrderCollection(MultipartFile file, OrderDTO dto) {
        Order order = this.orderMapper.toPersistence(dto);
        order.setStatus("PEN");
        order.setServiceId("LEY0053994");
        order.setAccountId("ZGE0000866");
        order.setTotalAmount(BigDecimal.ZERO);
        String uniqueId = generateUniqueId();

        while (orderRepository.existsByUniqueId(uniqueId)){
            uniqueId = generateUniqueId();
        }
        order.setUniqueId(uniqueId);
        Order savedOrder = this.orderRepository.save(order);
        log.info("Se creo la orden: {}", savedOrder);

        try {
            BigDecimal totalAmout = itemCollectionService.processCsvFile(file, savedOrder.getOrderId());
            savedOrder.setTotalAmount(totalAmout);
            this.orderRepository.save(savedOrder);
        } catch (Exception e) {
            log.info("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV");
        }
    }


    public void createOrderAutomaticDebit(MultipartFile file, OrderDTO dto) {
        Order order = this.orderMapper.toPersistence(dto);
        order.setStatus("PEN");
        order.setServiceId("JXM0025321");
        order.setAccountId("ZGE0000866");
        order.setTotalAmount(BigDecimal.ZERO);
        String uniqueId = generateUniqueId();

        while (orderRepository.existsByUniqueId(uniqueId)){
            uniqueId = generateUniqueId();
        }
        order.setUniqueId(uniqueId);
        Order savedOrder = this.orderRepository.save(order);
        log.info("Se creo la orden: {}", savedOrder);

        try {
            BigDecimal totalAmount = itemAutomaticDebitService.processCsvFile(file, savedOrder.getOrderId());
            savedOrder.setTotalAmount(totalAmount);
            this.orderRepository.save(savedOrder);
        } catch (Exception e) {
            log.info("Error al procesar el archivo CSV", e);
            throw new RuntimeException("Error al procesar el archivo CSV");
        }
    }

    private String generateUniqueId() {
        UniqueIdGeneration uniqueIdGenerator = new UniqueIdGeneration();
        return uniqueIdGenerator.getUniqueId();
    }

    public List<OrderDTO> obtainAllOrders() {
        log.info("Va a retornar todas las ordenes");
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(s -> this.orderMapper.toDTO(s)).collect(Collectors.toList());
    }

    public OrderDTO obtainOrderById(Integer id) {
        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
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
        if (order != null) {
            log.info("Se va a cambiar el status a la orden");
            order.setStatus(status);
            orderRepository.save(order);
            return this.orderMapper.toDTO(order);
        }
        return null;
    }

    public List<OrderDTO> getOrdersByServiceIdAndAccountIdAndDateRange(
            String serviceId, String accountId, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository
                .findByServiceIdAndAccountIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        serviceId, accountId, startDate, endDate);
        return orders.stream().map(s -> this.orderMapper.toDTO(s)).collect(Collectors.toList());
    }

    public List<OrderDTO> getActiveOrdersByServiceId(String serviceId) {
        LocalDate currentDate = LocalDate.now();
        List<Order> orders = orderRepository
                .findByServiceIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        serviceId, "ACT", currentDate, currentDate);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String getCompanyNameByAccountId(String accoundId){
        RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:8080/api/v1/companies")
        .build();
        return restClient.get()
        .uri("/account/{accountId}", accoundId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(String.class);
    }

    @Transactional
    public void expireItemsAfterOrderEndDate() {
        LocalDate today = LocalDate.now();
        List<Order> orders = orderRepository.findByEndDateBeforeAndStatus(today, "ACT");
        if (orders.isEmpty()) {
            log.info("No existe ninguna orden Activa el dia de hoy");
            return;
        }

        List<Integer> orderIds = orders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());

        List<ItemCollection> items = itemCollectionRepository.findByOrderIdIn(orderIds);
        List<ItemAutomaticDebit> itemsAD = itemAutomaticDebitRepository.findByOrderIdIn(orderIds);

        for (ItemCollection item : items) {
            log.info("Cambio de estado en itemCollection");
            item.setStatus("EXP");
            itemCollectionRepository.save(item);
        }

        for (ItemAutomaticDebit itemAD : itemsAD) {
            log.info("Cambio de estado en itemAutomaticD {}", itemAD.getId());
            itemAD.setStatus("EXP");
            itemAutomaticDebitRepository.save(itemAD);
        }

        for (Order order : orders) {
            log.info("Cambio de estado en orden");
            order.setStatus("EXP");
            orderRepository.save(order);
        }
    }

    @Scheduled(cron = "0 0 13 * * ?")
    @Async
    public void updateExpiredItems() {
        this.expireItemsAfterOrderEndDate();
    }

    @Scheduled(cron = "0 */3 * * * ?")
    @Async
    @Transactional
    public void processAutomaticDebits() {
        log.info("Iniciando procesamiento de débito automático...");
        List<OrderDTO> orders = this.getActiveOrdersByServiceId("JXM0025321");
        for (OrderDTO order : orders) {
            List<ItemAutomaticDebitDTO> items = itemAutomaticDebitService.getItemsByOrderIdAndStatus(order.getOrderId(), "PEN");

            WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/api/v1/account-transactions").build();
            for (ItemAutomaticDebitDTO item : items) {
                String companyName = this.getCompanyNameByAccountId(order.getAccountId());
                AccountTransactionDTO transactionDTO = AccountTransactionDTO.builder()
                    .accountId(5)
                    .uniqueId(order.getUniqueId())
                    .codeChannel("1")
                    .uniqueKey(order.getUniqueId())
                    .transactionType("DEB")
                    .transactionSubtype("TRANSFER")
                    .reference("COBRO AUTOMATICO " + companyName)
                    .amount(item.getDebitAmount())
                    .creditorAccount("2273445678")
                    .debitorAccount(item.getDebitAccount())
                    .createDate(LocalDateTime.now())
                    .applyTax(false)
                    .parentTransactionKey(null)
                    .status("PEN")
                    .build();

                try {
                    Mono<ResponseTransactionDTO> responseMono = webClient.post()
                        .uri("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDTO)
                        .retrieve()
                        .bodyToMono(ResponseTransactionDTO.class);

                    ResponseTransactionDTO response = responseMono.block(); 

                    AutomaticDebitPaymentRecordDTO record = new AutomaticDebitPaymentRecordDTO();
                    record.setItemAutomaticDebitId(item.getId());
                    record.setItemCommissionId(6);
                    record.setOutstandingBalance(response.getPendiente());
                    record.setDebitAmount(item.getDebitAmount());
                    record.setPaymentDate(response.getCreateDate());
                    
                    if (response.getPendiente().compareTo(BigDecimal.ZERO) > 0) {
                        record.setPaymentType("PAR");
                        record.setStatus("PEN");
                    } else {
                        record.setPaymentType("TOT");
                        record.setStatus("PAG");
                    }

                    AutomaticDebitPaymentRecord automaticDebitPaymentRecord = new AutomaticDebitPaymentRecord();
                    automaticDebitPaymentRecord.setItemAutomaticDebitId(record.getItemAutomaticDebitId());
                    automaticDebitPaymentRecord.setItemCommissionId(record.getItemCommissionId());
                    automaticDebitPaymentRecord.setUniqueId(paymentRecordService.generateUniqueId());
                    automaticDebitPaymentRecord.setOutstandingBalance(record.getOutstandingBalance());
                    automaticDebitPaymentRecord.setDebitAmount(record.getDebitAmount());
                    automaticDebitPaymentRecord.setPaymentDate(record.getPaymentDate());
                    automaticDebitPaymentRecord.setPaymentType(record.getPaymentType());
                    automaticDebitPaymentRecord.setStatus(record.getStatus());

                    automaticDebitPaymentRecordRepository.save(automaticDebitPaymentRecord);
                    
                    log.info("Procesamiento de débito automático completado.");
                } catch (Exception e) {
                    log.error("Error al procesar el débito del item {}: {}", item.getId(), e.getMessage());
                }
            }
        }
    }

}