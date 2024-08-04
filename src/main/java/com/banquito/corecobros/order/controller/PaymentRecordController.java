package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.service.PaymentRecordService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "PaymentRecordController", description = "APIs related to Payment Records")
public class PaymentRecordController {
    private final PaymentRecordService paymentRecordService;

    public PaymentRecordController(PaymentRecordService paymentRecordService) {
        this.paymentRecordService = paymentRecordService;
    }

    @Operation(summary = "Update a collection payment record", description = "Updates the details of a collection payment record by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<CollectionPaymentRecordDTO> updateCollectionPaymentRecord(@PathVariable Integer id,
            @RequestBody CollectionPaymentRecordDTO dto) {
        CollectionPaymentRecordDTO updatedRecord = paymentRecordService.updatePaymentRecord(id, dto);
        return ResponseEntity.ok(updatedRecord);
    }

    @Operation(summary = "Get payment records by account ID", description = "Fetches a list of collection payment records associated with a specific account ID.")
    @GetMapping("/records/{accountId}")
    public List<CollectionPaymentRecordDTO> getPaymentRecordsByAccountId(@PathVariable String accountId) {
        return paymentRecordService.findCollectionPaymentRecordsByAccountId(accountId);
    }

    @Operation(summary = "Get collection payment records by item collection ID", description = "Fetches a list of collection payment records associated with a specific item collection ID.")
    @GetMapping("/by-item-collection/{itemCollectionId}")
    public ResponseEntity<List<CollectionPaymentRecordDTO>> getCollectionPaymentRecordsByItemCollectionId(
            @PathVariable Integer itemCollectionId) {
        List<CollectionPaymentRecordDTO> records = paymentRecordService
                .getCollectionPaymentRecordsByItemCollectionId(itemCollectionId);
        return ResponseEntity.ok(records);
    }
}
