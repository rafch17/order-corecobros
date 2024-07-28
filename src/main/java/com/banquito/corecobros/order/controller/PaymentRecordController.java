package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.service.PaymentRecordService;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentRecordController {
    private final PaymentRecordService paymentRecordService;

    public PaymentRecordController(PaymentRecordService paymentRecordService) {
        this.paymentRecordService = paymentRecordService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionPaymentRecordDTO> updateCollectionPaymentRecord(@PathVariable Integer id, 
                                                                                   @RequestBody CollectionPaymentRecordDTO dto) {
        CollectionPaymentRecordDTO updatedRecord = paymentRecordService.updatePaymentRecord(id, dto);
        return ResponseEntity.ok(updatedRecord);
    }

    @GetMapping("/records/{accountId}")
    public List<CollectionPaymentRecordDTO> getPaymentRecordsByAccountId(@PathVariable Integer accountId) {
        return paymentRecordService.findCollectionPaymentRecordsByAccountId(accountId);
    }
}
