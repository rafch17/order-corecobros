package com.banquito.corecobros.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;
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

    public PaymentRecordService(CollectionPaymentRecordRepository collectionPaymentRecordRepository,
            AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository,
            AutomaticDebitPaymentRecordMapper automaticDebitPaymentRecordMapper,
            CollectionPaymentRecordMapper collectionPaymentRecordMapper) {
        this.collectionPaymentRecordRepository = collectionPaymentRecordRepository;
        this.automaticDebitPaymentRecordRepository = automaticDebitPaymentRecordRepository;
        this.automaticDebitPaymentRecordMapper = automaticDebitPaymentRecordMapper;
        this.collectionPaymentRecordMapper = collectionPaymentRecordMapper;
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

    public List<CollectionPaymentRecord> getPaymentRecordsByAccountId(Integer accountId) {
        return collectionPaymentRecordRepository.findByAccountId(accountId);
    } 


}
