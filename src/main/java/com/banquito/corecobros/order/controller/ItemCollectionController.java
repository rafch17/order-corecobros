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
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.service.ItemCollectionService;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PUT })
@RestController
@RequestMapping("/api/v1/collections")
public class ItemCollectionController {
    private final ItemCollectionService itemCollectionService;
    private OrderRepository orderRepository;

    public ItemCollectionController(ItemCollectionService itemCollectionService, OrderRepository orderRepository) {
        this.itemCollectionService = itemCollectionService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByCounterpartAndCompany(
            @RequestParam String counterpart, @RequestParam String companyId) {
        List<ItemCollectionDTO> items = itemCollectionService.findByCounterpartAndCompany(counterpart, companyId);
        return ResponseEntity.ok(items);
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

    @GetMapping("/item-collections/{status}")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByStatus(String status) {
        return ResponseEntity.ok(this.itemCollectionService.obtainItemCollectionsByStatus(status));
    }

    // @PostMapping("/upload")
    // public ResponseEntity<Void> uploadCsvFile(@RequestParam("file") MultipartFile
    // file) {
    // try {
    // itemCollectionService.processCsvFile(file);
    // return ResponseEntity.ok().build();
    // } catch (IOException e) {
    // return ResponseEntity.status(500).build();
    // }
    // }

    @GetMapping("/active")
    public ResponseEntity<List<ItemCollectionDTO>> getActiveItemCollections() {
        List<ItemCollectionDTO> itemCollections = itemCollectionService.findActiveItemCollections();
        return ResponseEntity.ok(itemCollections);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<ItemCollectionDTO>> getItemCollectionsByOrderId(@PathVariable Integer orderId) {
        List<ItemCollectionDTO> items = itemCollectionService.getItemCollectionsByOrderId(orderId);
        return ResponseEntity.ok(items);

    }
}
