package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.service.ItemAutomaticDebitService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/order-microservice/api/v1/automaticDebits")
@Tag(name = "ItemAutomaticDebit", description = "APIs related to Item Automatic Debits")
public class ItemAutomaticDebitController {
    private final ItemAutomaticDebitService itemAutomaticDebitService;

    public ItemAutomaticDebitController(ItemAutomaticDebitService itemAutomaticDebitService) {
        this.itemAutomaticDebitService = itemAutomaticDebitService;
    }

    @Operation(summary = "Get all item automatic debits", description = "Fetches a list of all item automatic debits.")
    @GetMapping
    public ResponseEntity<List<ItemAutomaticDebitDTO>> getAllItemAutomaticDebits() {
        return ResponseEntity.ok(this.itemAutomaticDebitService.obtainAllItemAutomaticDebits());
    }

    @Operation(summary = "Create a new item automatic debit", description = "Creates a new item automatic debit with the provided details.")
    @PostMapping
    public ResponseEntity<Void> createItemAutomaticDebit(ItemAutomaticDebitDTO itemAutomaticDebitDTO) {
        try {
            this.itemAutomaticDebitService.createItemAutomaticDebit(itemAutomaticDebitDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get an item automatic debit by ID", description = "Fetches the details of an item automatic debit by its ID.")
    @GetMapping("/id/{debitId}")
    public ResponseEntity<ItemAutomaticDebitDTO> getItemAutomaticDebitById(@PathVariable("debitId") Integer id) {
        try {
            return ResponseEntity.ok(this.itemAutomaticDebitService.obtainItemAutomaticDebitById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get item automatic debits by order ID", description = "Fetches a list of item automatic debits associated with a specific order ID.")
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<ItemAutomaticDebitDTO>> getItemAutomaticDebitsByOrderId(@PathVariable String orderId) {
        List<ItemAutomaticDebitDTO> items = itemAutomaticDebitService.getItemAutomaticDebitsByOrderId(orderId);
        return ResponseEntity.ok(items);
    }
}
