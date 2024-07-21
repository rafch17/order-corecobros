package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.service.ItemCollectionService;

public class PaymentRecordController {
    private final ItemCollectionService itemCollectionService;

    public PaymentRecordController(ItemCollectionService itemCollectionService) {
        this.itemCollectionService = itemCollectionService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CollectionPaymentRecord>> getPaymentRecordsByAccountId(@PathVariable Integer accountId) {
        List<CollectionPaymentRecord> records = itemCollectionService.getPaymentRecordsByAccountId(accountId);
        return ResponseEntity.ok(records);
    }
}
