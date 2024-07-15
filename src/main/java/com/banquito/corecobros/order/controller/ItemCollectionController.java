package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.service.ItemCollectionService;

@RestController
@RequestMapping("/api/v1/item-collection")
public class ItemCollectionController {
    private final ItemCollectionService itemCollectionService;

    public ItemCollectionController(ItemCollectionService itemCollectionService) {
        this.itemCollectionService = itemCollectionService;
    }

    @GetMapping
    public ResponseEntity<List<ItemCollectionDTO>> getAllItemCollections() {
        return ResponseEntity.ok(this.itemCollectionService.obtainAllItemCollections());
    }

    @PostMapping
    public ResponseEntity<Void> createItemCollection(ItemCollectionDTO itemCollectionDTO) {
        try {
            this.itemCollectionService.createItemCollection(itemCollectionDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemCollectionDTO> getItemCollectionById(Integer id) {
        try {
            return ResponseEntity.ok(this.itemCollectionService.obtainItemCollectionById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/item-collections/active")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByStatus() {
        return ResponseEntity.ok(this.itemCollectionService.obtainItemCollectionsByStatus());
    }


}
