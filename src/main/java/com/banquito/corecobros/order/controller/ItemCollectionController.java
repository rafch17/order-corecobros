package com.banquito.corecobros.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.service.ItemCollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/api/v1/collections")
@Tag(name = "ItemCollection", description = "APIs related to Item Collections")
public class ItemCollectionController {
    private final ItemCollectionService itemCollectionService;

    public ItemCollectionController(ItemCollectionService itemCollectionService) {
        this.itemCollectionService = itemCollectionService;
    }

    @Operation(summary = "Search item collections by counterpart and company", description = "Fetches item collections based on counterpart and company ID.")
    @GetMapping("/search")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByCounterpartAndCompany(
            @RequestParam String counterpart, @RequestParam String companyId) {
        List<ItemCollectionDTO> items = itemCollectionService.findByCounterpartAndCompany(counterpart, companyId);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get all item collections", description = "Fetches a list of all item collections.")
    @GetMapping
    public ResponseEntity<List<ItemCollectionDTO>> getAllItemCollections() {
        return ResponseEntity.ok(this.itemCollectionService.obtainAllItemCollections());
    }

    @Operation(summary = "Create a new item collection", description = "Creates a new item collection with the provided details.")
    @PostMapping
    public ResponseEntity<Void> createItemCollection(ItemCollectionDTO itemCollectionDTO) {
        try {
            this.itemCollectionService.createItemCollection(itemCollectionDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get an item collection by ID", description = "Fetches the details of an item collection by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ItemCollectionDTO> getItemCollectionById(Integer id) {
        try {
            return ResponseEntity.ok(this.itemCollectionService.obtainItemCollectionById(id));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get item collections by status", description = "Fetches a list of item collections based on their status.")
    @GetMapping("/item-collections/{status}")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByStatus(String status) {
        return ResponseEntity.ok(this.itemCollectionService.obtainItemCollectionsByStatus(status));
    }

    @Operation(summary = "Get active item collections", description = "Fetches a list of all active item collections.")
    @GetMapping("/active")
    public ResponseEntity<List<ItemCollectionDTO>> getActiveItemCollections() {
        List<ItemCollectionDTO> itemCollections = itemCollectionService.findActiveItemCollections();
        return ResponseEntity.ok(itemCollections);
    }

    @Operation(summary = "Get item collections by order ID", description = "Fetches a list of item collections associated with a specific order ID.")
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByOrderId(@PathVariable Integer orderId) {
        List<ItemCollectionDTO> items = itemCollectionService.getItemCollectionsByOrderId(orderId);
        return ResponseEntity.ok(items);
    }
}
