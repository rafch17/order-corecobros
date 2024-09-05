package com.banquito.corecobros.order.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.ItemCollectionMapper;

@ExtendWith(MockitoExtension.class)
public class ItemCollectionServiceTest {

    @Mock
    private ItemCollectionRepository itemCollectionRepository;

    @Mock
    private ItemCollectionMapper itemCollectionMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private ItemCollectionService itemCollectionService;

    @BeforeEach
    public void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    public void testCreateItemCollection() {
        ItemCollectionDTO dto = new ItemCollectionDTO();
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionMapper.toPersistence(dto)).thenReturn(itemCollection);
        when(itemCollectionRepository.save(itemCollection)).thenReturn(itemCollection);

        itemCollectionService.createItemCollection(dto);

        verify(itemCollectionRepository).save(itemCollection);
    }

    @Test
    public void testObtainAllItemCollections() {
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findAll()).thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        List<ItemCollectionDTO> result = itemCollectionService.obtainAllItemCollections();

        assertEquals(1, result.size());
        verify(itemCollectionRepository).findAll();
    }

    @Test
    public void testObtainItemCollectionById() {
        Integer id = 1;
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findById(id)).thenReturn(Optional.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        ItemCollectionDTO result = itemCollectionService.obtainItemCollectionById(id);

        assertEquals(itemCollectionDTO, result);
        verify(itemCollectionRepository).findById(id);
    }

    @Test
    public void testUpdateItemCollection() {
        Integer id = 1;
        String status = "PAG";
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findById(id)).thenReturn(Optional.of(itemCollection));

        itemCollectionService.updateItemCollection(id, status);

        assertEquals(status, itemCollection.getStatus());
        verify(itemCollectionRepository).save(itemCollection);
    }

    @Test
    public void testObtainItemCollectionsByCounterpartAndStatus() {
        String counterpart = "1798765432001";
        String status = "PEN";
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findByCounterpartAndStatus(counterpart, status))
                .thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        List<ItemCollectionDTO> result = itemCollectionService.obtainItemCollectionsByCounterpartAndStatus(counterpart,
                status);

        assertEquals(1, result.size());
        verify(itemCollectionRepository).findByCounterpartAndStatus(counterpart, status);
    }

    @Test
    public void testObtainItemCollectionsByStatus() {
        String status = "PEN";
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findByStatus(status)).thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        List<ItemCollectionDTO> result = itemCollectionService.obtainItemCollectionsByStatus(status);

        assertEquals(1, result.size());
        verify(itemCollectionRepository).findByStatus(status);
    }

    @Test
    public void testFindActiveItemCollections() {
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findByStatus("APR")).thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        List<ItemCollectionDTO> result = itemCollectionService.findActiveItemCollections();

        assertEquals(1, result.size());
        verify(itemCollectionRepository).findByStatus("APR");
    }

    @Test
    public void testGetItemCollectionsByOrderId() {
        Integer id = 1;
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findByOrderId(id)).thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        List<ItemCollectionDTO> result = itemCollectionService.getItemCollectionsByOrderId(id);

        assertEquals(1, result.size());
        verify(itemCollectionRepository).findByOrderId(id);
    }

    @Test
    public void testFindByCounterpart() {
        String counterpart = "1798765432001";
        ItemCollection itemCollection = new ItemCollection();
        when(itemCollectionRepository.findByCounterpart(counterpart)).thenReturn(List.of(itemCollection));

        ItemCollectionDTO itemCollectionDTO = new ItemCollectionDTO();
        when(itemCollectionMapper.toDTO(itemCollection)).thenReturn(itemCollectionDTO);

        ItemCollectionDTO result = itemCollectionService.findByCounterpart(counterpart);

        assertEquals(itemCollectionDTO, result);
        verify(itemCollectionRepository).findByCounterpart(counterpart);
    }

}
