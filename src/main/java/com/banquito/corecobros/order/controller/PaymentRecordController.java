package com.banquito.corecobros.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.service.ItemCollectionService;
import com.banquito.corecobros.order.service.PaymentRecordService;


public class PaymentRecordController {
    private final ItemCollectionService itemCollectionService;
    private final PaymentRecordService paymentRecordService;

    public PaymentRecordController(ItemCollectionService itemCollectionService, PaymentRecordService paymentRecordService) {
        this.itemCollectionService = itemCollectionService;
        this.paymentRecordService = paymentRecordService;
    }

    // @GetMapping("/account/{accountId}")
    // public ResponseEntity<List<CollectionPaymentRecord>> getPaymentRecordsByAccountId(@PathVariable Integer accountId) {
    //     List<CollectionPaymentRecord> records = itemCollectionService.getPaymentRecordsByAccountId(accountId);
    //     return ResponseEntity.ok(records);
    // }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionPaymentRecordDTO> updateCollectionPaymentRecord(@PathVariable Integer id, 
                                                                                   @RequestBody CollectionPaymentRecordDTO dto) {
        CollectionPaymentRecordDTO updatedRecord = paymentRecordService.updatePaymentRecord(id, dto);
        return ResponseEntity.ok(updatedRecord);
    }
}
