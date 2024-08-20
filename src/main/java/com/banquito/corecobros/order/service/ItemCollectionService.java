package com.banquito.corecobros.order.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.banquito.corecobros.order.dto.AccountTransactionDTO;
import com.banquito.corecobros.order.dto.CompanyDTO;
import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.dto.ResponseItemCommissionDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.ItemCollectionMapper;
import com.banquito.corecobros.order.util.uniqueId.UniqueIdGeneration;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ItemCollectionService {
    private final ItemCollectionRepository itemCollectionRepository;
    private final ItemCollectionMapper itemCollectionMapper;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final WebClient webClient;

    public ItemCollectionService(ItemCollectionRepository itemCollectionRepository,
            ItemCollectionMapper itemCollectionMapper,
            OrderRepository orderRepository,
            WebClient.Builder webClientBuilder) {
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemCollectionMapper = itemCollectionMapper;
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.webClient = webClientBuilder.build(); 
    }

    public List<ItemCollectionDTO> findByCounterpartAndCompany(String counterpart, String companyId) {
        WebClient webClient = this.webClientBuilder.baseUrl("http://localhost:9090/company-microservice/api/v1/companies").build();
        CompanyDTO company = webClient.get()
                .uri("/{uniqueId}", companyId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CompanyDTO.class)
                .block();

        if (company != null) {
            log.info("Compañía encontrada: {}", company);
            if (company.getUniqueId().equals(companyId)) {
                log.info("CompanyId coincide con UniqueId: {}", companyId);
                List<Order> orders = orderRepository.findByCompanyUid(companyId);
                List<Integer> orderIds = orders.stream().map(Order::getOrderId).collect(Collectors.toList());
                List<ItemCollection> itemCollections = itemCollectionRepository.findByCounterpart(counterpart);
                List<ItemCollection> filteredItemCollections = itemCollections.stream()
                        .filter(item -> orderIds.contains(item.getOrderId()) && "PEN".equals(item.getStatus()))
                        .collect(Collectors.toList());
                return filteredItemCollections.stream()
                        .map(this.itemCollectionMapper::toDTO)
                        .collect(Collectors.toList());
            } else {
                log.info("CompanyId no coincide con UniqueId. companyId: {}, company.uniqueId: {}", companyId,
                        company.getUniqueId());
            }
        } else {
            log.info("No se encontró ninguna compañía con uniqueId: {}", companyId);
        }
        return List.of();
    }

    public void createItemCollection(ItemCollectionDTO dto) {
        ItemCollection itemCollection = this.itemCollectionMapper.toPersistence(dto);
        ItemCollection savedItemCollection = this.itemCollectionRepository.save(itemCollection);
        log.info("Se creo la orden: {}", savedItemCollection);
    }

    public List<ItemCollectionDTO> obtainAllItemCollections() {
        log.info("Va a retornar todas las ordenes");
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findAll();
        return itemCollections.stream().map(s -> this.itemCollectionMapper.toDTO(s)).collect(Collectors.toList());
    }

    public ItemCollectionDTO obtainItemCollectionById(Integer id) {
        ItemCollection itemCollection = this.itemCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        return this.itemCollectionMapper.toDTO(itemCollection);
    }

    public void updateItemCollection(Integer id, String status) {
        ItemCollection itemCollection = this.itemCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        itemCollection.setStatus(status);
        this.itemCollectionRepository.save(itemCollection);
    }

    public List<ItemCollectionDTO> obtainItemCollectionsByCounterpartAndStatus(String counterpart, String status) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByCounterpartAndStatus(counterpart,
                status);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> obtainItemCollectionsByStatus(String status) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus(status);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> findActiveItemCollections() {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus("ACT");
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> getItemCollectionsByOrderId(Integer id) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByOrderId(id);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public ItemCollectionDTO findByCounterpart(String counterpart) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByCounterpart(counterpart);
        if (itemCollections.isEmpty()) {
            throw new RuntimeException("No se encontró la orden con la contrapartida " + counterpart);
        }
        return this.itemCollectionMapper.toDTO(itemCollections.get(0));
    }

    public ResponseItemCommissionDTO sendCommissionData(ResponseItemCommissionDTO itemCommissionDTO) {
        String apiUrl = "http://localhost:8080/item-commission-microservice/api/v1/item-commissions/";
        return webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemCommissionDTO), ResponseItemCommissionDTO.class)
                .retrieve()
                .bodyToMono(ResponseItemCommissionDTO.class)
                .block();
    }

    public BigDecimal processCsvFile(MultipartFile file, Integer orderId, String companyUid, String uniqueId) throws IOException {
        BigDecimal totalAmount = BigDecimal.ZERO;
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            for (CSVRecord csvRecord : csvParser) {
                String debtorName = csvRecord.get("debtorName");
                String counterpart = csvRecord.get("counterpart");
                String collectionAmount = csvRecord.get("collectionAmount").trim();
    
                BigDecimal amount = new BigDecimal(collectionAmount);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    // Crear y configurar ItemCollectionDTO
                    ItemCollectionDTO dto = new ItemCollectionDTO();
                    dto.setOrderId(orderId);
                    String unique = this.generateUniqueId();
                    dto.setUniqueId(unique);
                    dto.setDebtorName(debtorName);
                    dto.setCounterpart(counterpart);
                    dto.setCollectionAmount(amount);
                    dto.setStatus("PEN");
    
                    ResponseItemCommissionDTO commissionDTO = new ResponseItemCommissionDTO();
                    commissionDTO.setCompanyUniqueId(companyUid);
                    commissionDTO.setOrderUniqueId(uniqueId);
                    commissionDTO.setItemUniqueId(unique);
                    commissionDTO.setItemType("REC");
    
                    ResponseItemCommissionDTO responseDTO = this.sendCommissionData(commissionDTO);
                    log.info("Respuesta de Comisión: {}", responseDTO);
                    dto.setItemCommissionId(responseDTO.getCommissionId());
                    dto.setItemCommissionId(1); // Esto parece ser redundante y probablemente un error
                    this.createItemCollection(dto);
    
                    totalAmount = totalAmount.add(amount);
                }
            }
            log.info("Archivo CSV procesado con éxito.");
            return totalAmount;
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }
    }
    

    public String generateUniqueId() {
        UniqueIdGeneration uniqueIdGenerator = new UniqueIdGeneration();
        String uniqueId = "";
        boolean unique = false;

        while (!unique) {
            uniqueId = uniqueIdGenerator.generateUniqueId();
            if (!itemCollectionRepository.existsByUniqueId(uniqueId)) {
                unique = true;
            }
        }
        return uniqueId;
    }

    
    @Transactional
    public void processPayment(Integer itemCollectionId) {
        log.info("Iniciando procesamiento del recaudo...");
        ItemCollectionDTO itemCollectioDto = this.obtainItemCollectionById(itemCollectionId);
        ItemCollection itemCollection = this.itemCollectionRepository.findById(itemCollectioDto.getId())
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + itemCollectioDto.getId()));
        log.info("Iniciando transaccion del recaudo...");
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/Account-Microservice/api/v1/account-transactions").build();
        AccountTransactionDTO transactionDTO = AccountTransactionDTO.builder()
                    .accountId(47)
                    .codeChannel("CHA007363")
                    .transactionType("DEB")
                    .reference("Recaudo en Ventanilla ")
                    .amount(itemCollection.getCollectionAmount())
                    .creditorAccount("2273445678")
                    .debitorAccount("Inventarme las cuentas")
                    .commission(BigDecimal.valueOf(1.50))
                    .createDate(LocalDateTime.now())
                    .parentTransactionKey(null)
                    .status("")
                    .build();
        Mono<AccountTransactionDTO> responseMono = webClient.post()
                        .uri("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDTO)
                        .retrieve()
                        .bodyToMono(AccountTransactionDTO.class);
        
        
        itemCollection.setStatus("PAG");

        CollectionPaymentRecord collectionPaymentRecord = new CollectionPaymentRecord();
        collectionPaymentRecord.setItemCollectionId(itemCollection.getId());
        collectionPaymentRecord.setCollectionAmount(itemCollection.getCollectionAmount());
        collectionPaymentRecord.setPaymentType("TOT");
        collectionPaymentRecord.setPaymentDate(LocalDateTime.now());
        collectionPaymentRecord.setOutstandingBalance(BigDecimal.valueOf(0.00));
        collectionPaymentRecord.setChannel("VEN");


        /* 
        List<OrderDTO> orders = this.getActiveOrdersByServiceId("JXM0025321");
        log.info("Órdenes activas encontradas: {}", orders.size());
        
        for (OrderDTO order : orders) {
            log.info("Procesando orden: {}", order.getOrderId());
            List<ItemAutomaticDebitDTO> items = itemAutomaticDebitService.getItemsByOrderIdAndStatus(order.getOrderId(), "PEN");
            log.info("Items pendientes encontrados para la orden {}: {}", order.getOrderId(), items.size());
    
            //WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/Account-Microservice/api/v1/account-transactions").build();
            for (ItemAutomaticDebitDTO item : items) {
                // String companyName = this.getCompanyNameByAccountId(order.getAccountId());
                log.info("Procesando item {} de la orden {}, cuenta deudora: {}, monto: {}", item.getId(), order.getOrderId(), item.getDebitAccount(), item.getDebitAmount());

                AccountTransactionDTO transactionDTO = AccountTransactionDTO.builder()
                    .accountId(47)
                    .codeChannel("CHA007363")
                    .transactionType("DEB")
                    .reference("COBRO AUTOMATICO ")
                    .amount(item.getDebitAmount())
                    .creditorAccount("2273445678")
                    .debitorAccount(item.getDebitAccount())
                    .commission(BigDecimal.valueOf(1.50))
                    .createDate(LocalDateTime.now())
                    .parentTransactionKey(null)
                    .status("")
                    .build();
    
                try {
                    log.info("Enviando solicitud de débito automático para item {}...", item.getId());
                    Mono<AccountTransactionDTO> responseMono = webClient.post()
                        .uri("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDTO)
                        .retrieve()
                        .bodyToMono(AccountTransactionDTO.class);
    
                    AccountTransactionDTO response = responseMono.block();
                    log.info("Respuesta recibida para item {}: {}", item.getId(), response);
    
                    AccountTransactionDTO updatedResponse = AccountTransactionDTO.builder()
                        .accountId(response.getAccountId())
                        .codeChannel(response.getCodeChannel())
                        .amount(response.getAmount())
                        .debitorAccount(response.getDebitorAccount())
                        .creditorAccount(response.getCreditorAccount())
                        .commission(response.getCommission())
                        .transactionType(response.getPendiente().compareTo(BigDecimal.ZERO) > 0 ? "PAR" : "TOT")
                        .reference(response.getReference())
                        .parentTransactionKey(response.getParentTransactionKey())
                        .createDate(response.getCreateDate())
                        .status(response.getPendiente().compareTo(BigDecimal.ZERO) > 0 ? "PEN" : "PAG")
                        .pendiente(response.getPendiente())
                        .build();
    
                    // Save the record using your repository or service
                    AutomaticDebitPaymentRecord automaticDebitPaymentRecord = new AutomaticDebitPaymentRecord();
                    automaticDebitPaymentRecord.setItemAutomaticDebitId(item.getId());
                    automaticDebitPaymentRecord.setUniqueId(paymentRecordService.generateUniqueId());
                    automaticDebitPaymentRecord.setOutstandingBalance(updatedResponse.getPendiente());
                    automaticDebitPaymentRecord.setDebitAmount(updatedResponse.getAmount());
                    automaticDebitPaymentRecord.setPaymentDate(updatedResponse.getCreateDate());
                    automaticDebitPaymentRecord.setPaymentType(updatedResponse.getTransactionType());
                    automaticDebitPaymentRecord.setStatus(updatedResponse.getStatus());
    
                    automaticDebitPaymentRecordRepository.save(automaticDebitPaymentRecord);
                    log.info("Registro de débito automático guardado para item {}.", item.getId());
                } catch (Exception e) {
                    log.error("Error al procesar el débito del item {}: {}", item.getId(), e.getMessage(), e);
                }
            }
        }
        log.info("Procesamiento de débito automático completado.");*/
    }
}