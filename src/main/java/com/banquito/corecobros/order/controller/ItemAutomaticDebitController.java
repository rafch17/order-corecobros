package com.banquito.corecobros.order.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.service.ItemAutomaticDebitService;

@RestController
@RequestMapping("/api/v1/automaticDebits")
public class ItemAutomaticDebitController {
    private final ItemAutomaticDebitService itemAutomaticDebitService;

    public ItemAutomaticDebitController(ItemAutomaticDebitService itemAutomaticDebitService) {
        this.itemAutomaticDebitService = itemAutomaticDebitService;
    }

    @GetMapping
    public ResponseEntity<List<ItemAutomaticDebitDTO>> getAllItemAutomaticDebits() {
        return ResponseEntity.ok(this.itemAutomaticDebitService.obtainAllItemAutomaticDebits());
    }

    @PostMapping
    public ResponseEntity<Void> createItemAutomaticDebit(ItemAutomaticDebitDTO itemAutomaticDebitDTO) {
        try {
            this.itemAutomaticDebitService.createItemAutomaticDebit(itemAutomaticDebitDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemAutomaticDebitDTO> getItemAutomaticDebitById(Integer id) {
        try {
            return ResponseEntity.ok(this.itemAutomaticDebitService.obtainItemAutomaticDebitById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        try {
            itemAutomaticDebitService.processCsvFile(file);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<ItemAutomaticDebitDTO>> getItemAutomaticDebitsByOrderId(@PathVariable Integer orderId) {
        List<ItemAutomaticDebitDTO> items = itemAutomaticDebitService.getItemAutomaticDebitsByOrderId(orderId);
        return ResponseEntity.ok(items);
    }
}
