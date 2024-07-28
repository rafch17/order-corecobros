package com.banquito.corecobros.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.AutomaticDebitPaymentRecordMapper;
import com.banquito.corecobros.order.util.mapper.CollectionPaymentRecordMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentRecordService {
    private final CollectionPaymentRecordRepository collectionPaymentRecordRepository;
    private final AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository;
    private final AutomaticDebitPaymentRecordMapper automaticDebitPaymentRecordMapper;
    private final CollectionPaymentRecordMapper collectionPaymentRecordMapper;
    private final OrderRepository orderRepository;

    public PaymentRecordService(CollectionPaymentRecordRepository collectionPaymentRecordRepository,
            AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository,
            AutomaticDebitPaymentRecordMapper automaticDebitPaymentRecordMapper,
            CollectionPaymentRecordMapper collectionPaymentRecordMapper,
            OrderRepository orderRepository) {
        this.collectionPaymentRecordRepository = collectionPaymentRecordRepository;
        this.automaticDebitPaymentRecordRepository = automaticDebitPaymentRecordRepository;
        this.automaticDebitPaymentRecordMapper = automaticDebitPaymentRecordMapper;
        this.collectionPaymentRecordMapper = collectionPaymentRecordMapper;
        this.orderRepository = orderRepository;
    }

    public CollectionPaymentRecord createCollectionPaymentRecord(CollectionPaymentRecordDTO dto) {
        CollectionPaymentRecord record = this.collectionPaymentRecordMapper.toPersistence(dto);
        CollectionPaymentRecord savedRecord = collectionPaymentRecordRepository.save(record);
        log.info("Se creó el registro de pago de colección: {}", savedRecord);
        return savedRecord;
    }

    public AutomaticDebitPaymentRecord createAutomaticDebitPaymentRecord(AutomaticDebitPaymentRecordDTO dto) {
        AutomaticDebitPaymentRecord record = this.automaticDebitPaymentRecordMapper.toPersistence(dto);
        AutomaticDebitPaymentRecord savedRecord = automaticDebitPaymentRecordRepository.save(record);
        log.info("Se creó el registro de pago de débito automático: {}", savedRecord);
        return savedRecord;
    }

    // public List<CollectionPaymentRecord> getPaymentRecordsByAccountId(Integer accountId) {
    //     log.info("Va a buscar por Cuenta Id:");
    //     return collectionPaymentRecordRepository.findByAccountId(accountId);
    // } 

    public List<CollectionPaymentRecordDTO> getAll(){
        log.info("Se va a retornar todaos los registros de Collections");
        List<CollectionPaymentRecord> records = collectionPaymentRecordRepository.findAll();
        return records.stream().map(s -> this.collectionPaymentRecordMapper.toDTO(s)).collect(Collectors.toList());
    }

    public CollectionPaymentRecordDTO getPaymentRecordById(Integer id) {
        CollectionPaymentRecord record = this.collectionPaymentRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el registro de pago con el ID " + id));
        return this.collectionPaymentRecordMapper.toDTO(record);
    }

    public CollectionPaymentRecordDTO updatePaymentRecord(Integer id, CollectionPaymentRecordDTO dto) {
        CollectionPaymentRecord collectionPaymentRecord = this.collectionPaymentRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el registro de pago de colección con el ID " + id));
        collectionPaymentRecord.setItemCollectionId(dto.getItemCollectionId());
        collectionPaymentRecord.setCollectionAmount(dto.getCollectionAmount());
        collectionPaymentRecord.setPaymentType(dto.getPaymentType());
        collectionPaymentRecord.setPaymentDate(dto.getPaymentDate());
        collectionPaymentRecord.setOutstandingBalance(dto.getOutstandingBalance());
        collectionPaymentRecord.setChannel(dto.getChannel());
        CollectionPaymentRecord updatedRecord = this.collectionPaymentRecordRepository.save(collectionPaymentRecord);
        return this.collectionPaymentRecordMapper.toDTO(updatedRecord);
    }

    public List<CollectionPaymentRecordDTO> findCollectionPaymentRecordsByAccountId(Integer accountId) {
        List<Order> orders = orderRepository.findByAccountId(accountId);

        List<Integer> itemCollectionIds = orders.stream()
            .flatMap(order -> order.getItemCollections().stream())
            .map(itemCollection -> itemCollection.getId())
            .collect(Collectors.toList());

        if (itemCollectionIds.isEmpty()) {
            return new ArrayList<>();
        }

        return collectionPaymentRecordRepository.findByItemCollectionIdIn(itemCollectionIds).stream()
            .map(collectionPaymentRecordMapper::toDTO)
            .collect(Collectors.toList());
    }


}
