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
import com.banquito.corecobros.order.dto.AccountTransactionPaymentDTO;
import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.dto.CompanyDTO;
import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.dto.OrderDTO;
import com.banquito.corecobros.order.dto.ResponseItemCommissionDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.CollectionPaymentRecordMapper;
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
    private final CollectionPaymentRecordMapper collectMapper;
    private final CollectionPaymentRecordRepository collectionPaymentRecordRepo;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final WebClient webClient;

    public ItemCollectionService(ItemCollectionRepository itemCollectionRepository,
            ItemCollectionMapper itemCollectionMapper,
            OrderRepository orderRepository,
            WebClient.Builder webClientBuilder, CollectionPaymentRecordRepository collectionPaymentRecordRepo, CollectionPaymentRecordMapper collectMapper) {
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemCollectionMapper = itemCollectionMapper;
        this.collectMapper = collectMapper;
        this.collectionPaymentRecordRepo = collectionPaymentRecordRepo;
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
        itemCollection.setItemCommissionId(35);
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
    public CollectionPaymentRecordDTO processPayment(Integer itemCollectionId, String codeInternal) {
        log.info("Iniciando procesamiento del recaudo...");
        ItemCollectionDTO itemCollectioDto = this.obtainItemCollectionById(itemCollectionId);
        ItemCollection itemCollection = this.itemCollectionRepository.findById(itemCollectioDto.getId())
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + itemCollectioDto.getId()));
        Order order = this.orderRepository.findById(itemCollectioDto.getOrderId()).orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID "));;
        log.info("Iniciando transaccion del recaudo...");
        /*WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/Account-Microservice/api/v1/account-transactions").build();
        AccountTransactionPaymentDTO transactionDTO = AccountTransactionPaymentDTO.builder()
                    .accountId(47)
                    .codeChannel("CHA007363")
                    .transactionType("CRE")
                    .reference("Recaudo en Ventanilla")
                    .amount(itemCollection.getCollectionAmount())
                    .creditorAccount(codeInternal)
                    .debitorAccount("No Aplica (Ventanilla)")
                    .comission(BigDecimal.valueOf(0.4))
                    .parentTransactionKey(null)
                    .amountCollected(itemCollection.getCollectionAmount())
                    .build();*/
        /*Mono<AccountTransactionPaymentDTO> responseMono = webClient.post()
                        .uri("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDTO)
                        .retrieve()
                        .bodyToMono(AccountTransactionDTO.class);*/
        
        
        itemCollection.setStatus("PAG");

        CollectionPaymentRecord collectionPaymentRecord = new CollectionPaymentRecord();
        collectionPaymentRecord.setItemCollectionId(itemCollection.getId());
        collectionPaymentRecord.setUniqueId(this.generateUniqueId());
        collectionPaymentRecord.setItemCollection(itemCollection);
        collectionPaymentRecord.setItemCommissionId(35);
        collectionPaymentRecord.setCollectionAmount(itemCollection.getCollectionAmount());
        collectionPaymentRecord.setPaymentType("TOT");
        collectionPaymentRecord.setPaymentDate(LocalDateTime.now());
        collectionPaymentRecord.setOutstandingBalance(BigDecimal.valueOf(0.00));
        collectionPaymentRecord.setChannel("VEN");
        this.itemCollectionRepository.save(itemCollection);
        this.collectionPaymentRecordRepo.save(collectionPaymentRecord);
        log.info("Registro del recaudo guardado para item {}.", itemCollection.getId());
        CollectionPaymentRecordDTO collectionPaymentRecordDTO = this.collectMapper.toDTO(collectionPaymentRecord);
        return collectionPaymentRecordDTO;
        
    }
    
}