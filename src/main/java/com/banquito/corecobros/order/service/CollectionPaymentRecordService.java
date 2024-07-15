package com.banquito.corecobros.order.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CollectionPaymentRecordService {
    private final CollectionPaymentRecordRepository collectionPaymentRecordRepository;

    public CollectionPaymentRecordService(CollectionPaymentRecordRepository collectionPaymentRecordRepository) {
        this.collectionPaymentRecordRepository = collectionPaymentRecordRepository;
    }

    public CollectionPaymentRecord createCollectionPaymentRecord(ItemCollection itemCollection) {
        CollectionPaymentRecord record = new CollectionPaymentRecord();
        record.setCode(itemCollection.getCode());
        record.setOwedPayment(itemCollection.getCollectionAmount());
        record.setPaymentType("MIN");
        record.setPaymentDate(LocalDateTime.now());
        record.setOutstandingBalance(null);
        record.setChannel("WEB");
        record.setStatus("PAG");
        CollectionPaymentRecord savedRecord = collectionPaymentRecordRepository.save(record);
        log.info("Se creó el registro de pago de colección: {}", savedRecord);
        return savedRecord;
    }
}
