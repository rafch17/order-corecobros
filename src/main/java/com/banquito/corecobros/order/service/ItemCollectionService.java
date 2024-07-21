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

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.util.mapper.ItemCollectionMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemCollectionService {
    private final ItemCollectionRepository itemCollectionRepository;
    private final ItemCollectionMapper itemCollectionMapper;
    private final PaymentRecordService collectionPaymentRecordService;

    public ItemCollectionService(ItemCollectionRepository itemCollectionRepository,
            ItemCollectionMapper itemCollectionMapper, PaymentRecordService collectionPaymentRecordService) {
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemCollectionMapper = itemCollectionMapper;
        this.collectionPaymentRecordService = collectionPaymentRecordService;
    }

    public void createItemCollection(ItemCollectionDTO dto) {
        if (dto.getCode() != null && itemCollectionRepository.existsById(dto.getCode())) {
            throw new RuntimeException("El ID " + dto.getCode() + " ya existe.");
        }
        ItemCollection itemCollection = this.itemCollectionMapper.toPersistence(dto);
        ItemCollection savedItemCollection = this.itemCollectionRepository.save(itemCollection);
        log.info("Se creo la orden: {}", savedItemCollection);
        this.collectionPaymentRecordService.createCollectionPaymentRecord(savedItemCollection);
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

    public List<ItemCollectionDTO> obtainItemCollectionsByStatus() {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus("ACT");
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> findActiveItemCollections() {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus("ACT");
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollection> getItemCollectionsByOrderId(Integer orderId) {
        return itemCollectionRepository.findByOrderId(orderId);
    }

    public List<CollectionPaymentRecord> getPaymentRecordsByAccountId(Integer accountId) {
        return itemCollectionRepository.findByAccountId(accountId);
    }

    public void processCsvFile(MultipartFile file) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            for (CSVRecord csvRecord : csvParser) {
                String code = csvRecord.get("code");
                String orderCode = csvRecord.get("orderCode");
                String uniqueId = csvRecord.get("uniqueId");
                String debtorName = csvRecord.get("debtorName");
                String counterpart = csvRecord.get("counterpart");
                String collectionAmount = csvRecord.get("collectionAmount");
                String status = csvRecord.get("status");

                ItemCollectionDTO dto = new ItemCollectionDTO();
                dto.setCode(Integer.valueOf(code));
                dto.setOrderCode(Integer.valueOf(orderCode));
                dto.setUniqueCode(uniqueId);
                dto.setDebtorName(debtorName);
                dto.setCounterpart(counterpart);
                dto.setCollectionAmount(new BigDecimal(collectionAmount));
                dto.setStatus(status);

                this.createItemCollection(dto);
            }

            log.info("Archivo CSV procesado con éxito.");
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }

    }
}
