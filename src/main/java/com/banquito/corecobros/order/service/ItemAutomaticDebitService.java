package com.banquito.corecobros.order.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.dto.ResponseItemCommissionDTO;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.util.mapper.ItemAutomaticDebitMapper;
import com.banquito.corecobros.order.util.uniqueId.UniqueIdGeneration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class ItemAutomaticDebitService {
    private final ItemAutomaticDebitRepository itemAutomaticDebitRepository;
    private final ItemAutomaticDebitMapper mapper;
    private final WebClient.Builder webClientBuilder;
    private final WebClient webClient;

    public ItemAutomaticDebitService(ItemAutomaticDebitRepository itemAutomaticDebitRepository, ItemAutomaticDebitMapper mapper, WebClient.Builder webClientBuilder) {
        this.itemAutomaticDebitRepository = itemAutomaticDebitRepository;
        this.mapper = mapper;
        this.webClientBuilder = webClientBuilder;
        this.webClient = this.webClientBuilder.build();
    }

    public void createItemAutomaticDebit(ItemAutomaticDebitDTO dto) {
        if (dto.getId() != null && itemAutomaticDebitRepository.existsById(dto.getId())) {
            throw new RuntimeException("El ID " + dto.getId() + " ya existe.");
        }
        ItemAutomaticDebit itemAutomaticDebit = this.mapper.toPersistence(dto);
        ItemAutomaticDebit savedItemAutomaticDebit = this.itemAutomaticDebitRepository.save(itemAutomaticDebit);
        log.info("Se creo la orden: {}", savedItemAutomaticDebit);
    }

    public List<ItemAutomaticDebitDTO> obtainAllItemAutomaticDebits() {
        log.info("Va a retornar todas las ordenes");
        List<ItemAutomaticDebit> itemAutomaticDebits = this.itemAutomaticDebitRepository.findAll();
        return itemAutomaticDebits.stream().map(s -> this.mapper.toDTO(s)).collect(Collectors.toList());
    }

    public ItemAutomaticDebitDTO obtainItemAutomaticDebitById(Integer id) {
        ItemAutomaticDebit itemAutomaticDebit = this.itemAutomaticDebitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        return this.mapper.toDTO(itemAutomaticDebit);
    }

    public void updateItemAutomaticDebit(Integer id, String status) {
        ItemAutomaticDebit itemAutomaticDebit = this.itemAutomaticDebitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        itemAutomaticDebit.setStatus(status);
        this.itemAutomaticDebitRepository.save(itemAutomaticDebit);
    }

    public List<ItemAutomaticDebitDTO> obtainItemAutomaticDebitsByStatus(String status){
        List<ItemAutomaticDebit> itemAutomaticDebits = this.itemAutomaticDebitRepository.findByStatus(status);
        return itemAutomaticDebits.stream().map(s -> this.mapper.toDTO(s)).collect(Collectors.toList());
    }

    public ResponseItemCommissionDTO sendCommissionData(ResponseItemCommissionDTO itemCommissionDTO) {
        String apiUrl = "http://core-cobros-alb-538320160.us-east-1.elb.amazonaws.com/commission-microservice/api/v1/item-commissions";
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
                String identification = csvRecord.get("identification");
                String debtorName = csvRecord.get("debtorName");
                String debitAccount = csvRecord.get("debitAccount");
                String debitAmount = csvRecord.get("debitAmount").trim();

                BigDecimal amount = new BigDecimal(debitAmount);
                if (amount.compareTo(BigDecimal.ZERO) > 0){
                    ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
                    dto.setOrderId(orderId);
                    String unique = this.generateUniqueId();
                    dto.setUniqueId(uniqueId);
                    dto.setIdentification(identification);
                    dto.setDebtorName(debtorName);
                    dto.setDebitAccount(debitAccount);
                    dto.setDebitAmount(amount);
                    dto.setStatus("PEN");
    
                    ResponseItemCommissionDTO commissionDTO = new ResponseItemCommissionDTO();
                    commissionDTO.setCompanyUniqueId(companyUid);
                    commissionDTO.setOrderUniqueId(uniqueId);
                    commissionDTO.setItemUniqueId(unique);
                    commissionDTO.setItemType("DEB");

                    log.info("Esto es lo que se envia:", commissionDTO);

                    ResponseItemCommissionDTO responseDTO = this.sendCommissionData(commissionDTO);
                    log.info("Respuesta de Comisión: {}", responseDTO.getOrderUniqueId());

                    dto.setItemCommissionId(Integer.parseInt(String.valueOf(responseDTO.getCommissionId())));

                    this.createItemAutomaticDebit(dto);
    
                    totalAmount = totalAmount.add(amount);
                }

            }
            log.info("Archivo CSV procesado con éxito.");
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }
        return totalAmount;
    }

    public String generateUniqueId() {
        UniqueIdGeneration uniqueIdGenerator = new UniqueIdGeneration();
        String uniqueId = "";
        boolean unique = false;

        while (!unique) {
            uniqueId = uniqueIdGenerator.generateUniqueId();
            if (!itemAutomaticDebitRepository.existsByUniqueId(uniqueId)) {
                unique = true;
            }
        }
        return uniqueId;
    }

    public List<ItemAutomaticDebitDTO> getItemAutomaticDebitsByOrderId(String orderId) {
        List<ItemAutomaticDebit> itemAutomaticDebits = this.itemAutomaticDebitRepository.findByOrderUniqueId(orderId);
        return itemAutomaticDebits.stream().map(s -> this.mapper.toDTO(s)).collect(Collectors.toList());
    }

    public List<ItemAutomaticDebitDTO> getItemsByOrderIdAndStatus(Integer orderId, String status) {
        List<ItemAutomaticDebit> items = itemAutomaticDebitRepository.findByOrderIdAndStatus(orderId, status);
        return items.stream()
                    .map(item -> mapper.toDTO(item))
                    .collect(Collectors.toList());
    }

    public void updateItem(ItemAutomaticDebitDTO itemDTO) {
        ItemAutomaticDebit item = mapper.toPersistence(itemDTO);
        itemAutomaticDebitRepository.save(item);
    }
    
    
}
