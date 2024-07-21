package com.banquito.corecobros.order.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentRecordService {
    private final CollectionPaymentRecordRepository collectionPaymentRecordRepository;
    private final AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository;


    public PaymentRecordService(CollectionPaymentRecordRepository collectionPaymentRecordRepository,
            AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository) {
        this.collectionPaymentRecordRepository = collectionPaymentRecordRepository;
        this.automaticDebitPaymentRecordRepository = automaticDebitPaymentRecordRepository;
    }

    public CollectionPaymentRecord createCollectionPaymentRecord(ItemCollection itemCollection) {
        CollectionPaymentRecord record = new CollectionPaymentRecord();
        //record.setCode(itemCollection.getCode());
        record.setItemCollectionCode(itemCollection.getCode());
        record.setCollectionAmount(itemCollection.getCollectionAmount());
        record.setPaymentType("MIN");
        record.setPaymentDate(LocalDateTime.now());
        record.setOutstandingBalance(null);
        record.setChannel("WEB");
        CollectionPaymentRecord savedRecord = collectionPaymentRecordRepository.save(record);
        log.info("Se creó el registro de pago de colección: {}", savedRecord);
        return savedRecord;
    }

    public AutomaticDebitPaymentRecord createAutomaticDebitPaymentRecord(AutomaticDebitPaymentRecordDTO dto) {
        AutomaticDebitPaymentRecord record = new AutomaticDebitPaymentRecord();
        record.setCode(dto.getCode());
        record.setItemAutomaticDebitCode(dto.getItemAutomaticDebitCode());
        record.setItemCommissionCode(dto.getItemCommissionCode());
        record.setOutstandingBalance(dto.getOutstandingBalance());
        record.setDebitAmount(dto.getDebitAmount());
        record.setPaymentDate(dto.getPaymentDate());
        record.setStatus(dto.getStatus());

        AutomaticDebitPaymentRecord savedRecord = automaticDebitPaymentRecordRepository.save(record);
        log.info("Se creó el registro de pago de débito automático: {}", savedRecord);
        return savedRecord;
    }

    public List<CollectionPaymentRecord> getPaymentRecordsByAccountId(Integer accountId) {
        return collectionPaymentRecordRepository.findByAccountId(accountId);
    } 


}
