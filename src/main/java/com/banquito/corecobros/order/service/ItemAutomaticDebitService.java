package com.banquito.corecobros.order.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.util.mapper.ItemAutomaticDebitMapper;
import com.banquito.corecobros.order.util.uniqueId.UniqueIdGeneration;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ItemAutomaticDebitService {
    private final ItemAutomaticDebitRepository itemAutomaticDebitRepository;
    private final ItemAutomaticDebitMapper mapper;

    public ItemAutomaticDebitService(ItemAutomaticDebitRepository itemAutomaticDebitRepository,
            ItemAutomaticDebitMapper mapper) {
        this.itemAutomaticDebitRepository = itemAutomaticDebitRepository;
        this.mapper = mapper;
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

    public void processCsvFile(MultipartFile file, Integer orderId) throws IOException {
        UniqueIdGeneration uniqueIdGenerator = new UniqueIdGeneration();
        String uniqueId = uniqueIdGenerator.generateUniqueId();
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            for (CSVRecord csvRecord : csvParser) {
                String identification = csvRecord.get("identification");
                String debtorName = csvRecord.get("debtorName");
                String debitAccount = csvRecord.get("debitAccount");
                String debitAmount = csvRecord.get("debitAmount");

                ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
                dto.setOrderId(orderId);
                dto.setUniqueId(uniqueId);
                dto.setIdentification(identification);
                dto.setDebtorName(debtorName);
                dto.setDebitAccount(debitAccount);
                dto.setDebitAmount(new BigDecimal(debitAmount));
                dto.setStatus("PEN");

                this.createItemAutomaticDebit(dto);
            }

            log.info("Archivo CSV procesado con éxito.");
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }

    }

    public List<ItemAutomaticDebitDTO> getItemAutomaticDebitsByOrderId(Integer id) {
        List<ItemAutomaticDebit> itemAutomaticDebits = this.itemAutomaticDebitRepository.findByOrderId(id);
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
