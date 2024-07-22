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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.util.mapper.ItemAutomaticDebitMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ItemAutomaticDebitService {
    private final ItemAutomaticDebitRepository itemAutomaticDebitRepository;
    private final ItemAutomaticDebitMapper mapper;
    private final PaymentRecordService paymentRecordService;
    private final RestTemplate restTemplate;
    private final String accountServiceUrl = "http://your-account-service-url";


    public ItemAutomaticDebitService(ItemAutomaticDebitRepository itemAutomaticDebitRepository, ItemAutomaticDebitMapper mapper,
            PaymentRecordService paymentRecordService, RestTemplate restTemplate) {
        this.itemAutomaticDebitRepository = itemAutomaticDebitRepository;
        this.mapper = mapper;
        this.paymentRecordService = paymentRecordService;
        this.restTemplate = restTemplate;
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

    public void processCsvFile(MultipartFile file) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            for (CSVRecord csvRecord : csvParser) {
                String code = csvRecord.get("code");
                String orderCode = csvRecord.get("orderCode");
                String uniqueId = csvRecord.get("uniqueId");
                String identification = csvRecord.get("identification");
                String debtorName = csvRecord.get("debtorName");
                String debitAccount = csvRecord.get("debitAccount");
                String debitAmount = csvRecord.get("debitAmount");
                String status = csvRecord.get("status");

                ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
                dto.setId(Integer.valueOf(code));
                dto.setOrderId(Integer.valueOf(orderCode));
                dto.setUniqueId(uniqueId);
                dto.setIdentification(identification);
                dto.setDebtorName(debtorName);
                dto.setDebitAccount(debitAccount);
                dto.setDebitAmount(new BigDecimal(debitAmount));
                dto.setStatus(status);

                this.createItemAutomaticDebit(dto);
            }

            log.info("Archivo CSV procesado con éxito.");
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }

    }
    @Transactional
    public void processAutomaticDebit() {
        List<ItemAutomaticDebitDTO> activeItemAutomaticDebit = this.obtainItemAutomaticDebitsByStatus("ACT");
        for (ItemAutomaticDebitDTO item : activeItemAutomaticDebit) {
            BigDecimal accountBalance = getAccountBalance(item.getDebitAccount());

            AutomaticDebitPaymentRecordDTO record = new AutomaticDebitPaymentRecordDTO();
            record.setItemAutomaticDebitId(item.getId());
            record.setDebitAmount(item.getDebitAmount());
            record.setPaymentDate(LocalDateTime.now());

            if (accountBalance.compareTo(item.getDebitAmount()) >= 0) {
                debitAccount(item.getDebitAccount(), item.getDebitAmount());
                record.setOutstandingBalance(BigDecimal.ZERO);
                record.setPaymentType("TOT");
                record.setStatus("PAG");
            } else {
                debitAccount(item.getDebitAccount(), accountBalance);
                record.setOutstandingBalance(item.getDebitAmount().subtract(accountBalance));
                record.setPaymentType("PAR");
            }

            this.paymentRecordService.createAutomaticDebitPaymentRecord(record);
        }
    }

    private BigDecimal getAccountBalance(String debitAccount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<BigDecimal> response = restTemplate.exchange(
            accountServiceUrl + "/accountBalance?debitAccount=" + debitAccount,
            HttpMethod.GET,
            request,
            BigDecimal.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("No se pudo obtener el saldo de la cuenta " + debitAccount);
        }
    }

    private void debitAccount(String debitAccount, BigDecimal amount) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(headers);
        restTemplate.exchange(
            accountServiceUrl + "/debitAccount?debitAccount=" + debitAccount + "&amount=" + amount,
            HttpMethod.POST,
            request,
            Void.class
        );
    }

    public List<ItemAutomaticDebitDTO> getItemAutomaticDebitsByOrderId(Integer id) {
        List<ItemAutomaticDebit> itemAutomaticDebits = this.itemAutomaticDebitRepository.findByOrderId(id);
        return itemAutomaticDebits.stream().map(s -> this.mapper.toDTO(s)).collect(Collectors.toList());
    }
    
    
}
