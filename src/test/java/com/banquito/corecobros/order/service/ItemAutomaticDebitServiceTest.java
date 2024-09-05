package com.banquito.corecobros.order.service;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.ItemAutomaticDebitRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.ItemAutomaticDebitMapper;

public class ItemAutomaticDebitServiceTest {

    @Mock
    private ItemAutomaticDebitRepository itemAutomaticDebitRepository;

    @Mock
    private ItemAutomaticDebitMapper mapper;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ItemAutomaticDebitService itemAutomaticDebitService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    public void testCreateItemAutomaticDebit() {
        ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
        dto.setId(1);
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(mapper.toPersistence(dto)).thenReturn(item);
        when(itemAutomaticDebitRepository.save(item)).thenReturn(item);

        itemAutomaticDebitService.createItemAutomaticDebit(dto);

        verify(itemAutomaticDebitRepository, times(1)).save(item);
    }

    @Test
    public void testObtainAllItemAutomaticDebits() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(itemAutomaticDebitRepository.findAll()).thenReturn(Arrays.asList(item));
        when(mapper.toDTO(item)).thenReturn(new ItemAutomaticDebitDTO());

        List<ItemAutomaticDebitDTO> result = itemAutomaticDebitService.obtainAllItemAutomaticDebits();

        verify(itemAutomaticDebitRepository, times(1)).findAll();
        assert result.size() == 1;
    }

    @Test
    public void testObtainItemAutomaticDebitById() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(itemAutomaticDebitRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(mapper.toDTO(item)).thenReturn(new ItemAutomaticDebitDTO());

        ItemAutomaticDebitDTO result = itemAutomaticDebitService.obtainItemAutomaticDebitById(1);

        verify(itemAutomaticDebitRepository, times(1)).findById(1);
        assert result != null;
    }

    @Test
    public void testUpdateItemAutomaticDebit() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        item.setStatus("PEN"); 

        when(itemAutomaticDebitRepository.findById(anyInt())).thenReturn(Optional.of(item));

        itemAutomaticDebitService.updateItemAutomaticDebit(1, "PAG");

        verify(itemAutomaticDebitRepository, times(1)).save(item);

        assert "PAG".equals(item.getStatus());
    }

    @Test
    public void testObtainItemAutomaticDebitsByStatus() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(itemAutomaticDebitRepository.findByStatus(anyString())).thenReturn(Arrays.asList(item));
        when(mapper.toDTO(item)).thenReturn(new ItemAutomaticDebitDTO());

        List<ItemAutomaticDebitDTO> result = itemAutomaticDebitService.obtainItemAutomaticDebitsByStatus("PAG");

        verify(itemAutomaticDebitRepository, times(1)).findByStatus("PAG");
        assert result.size() == 1;
    }

    @Test
    public void testGenerateUniqueId() {
        when(itemAutomaticDebitRepository.existsByUniqueId(anyString())).thenReturn(false);

        String uniqueId = itemAutomaticDebitService.generateUniqueId();

        assert uniqueId != null && !uniqueId.isEmpty();
    }

    @Test
    public void testGetItemAutomaticDebitsByOrderId() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(itemAutomaticDebitRepository.findByOrderUniqueId(anyString())).thenReturn(Arrays.asList(item));
        when(mapper.toDTO(item)).thenReturn(new ItemAutomaticDebitDTO());

        List<ItemAutomaticDebitDTO> result = itemAutomaticDebitService.getItemAutomaticDebitsByOrderId("40");

        verify(itemAutomaticDebitRepository, times(1)).findByOrderUniqueId("40");
        assert result.size() == 1;
    }

    @Test
    public void testGetItemsByOrderIdAndStatus() {
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(itemAutomaticDebitRepository.findByOrderIdAndStatus(anyInt(), anyString()))
                .thenReturn(Arrays.asList(item));
        when(mapper.toDTO(item)).thenReturn(new ItemAutomaticDebitDTO());

        List<ItemAutomaticDebitDTO> result = itemAutomaticDebitService.getItemsByOrderIdAndStatus(1, "PAG");

        verify(itemAutomaticDebitRepository, times(1)).findByOrderIdAndStatus(1, "PAG");
        assert result.size() == 1;
    }

    @Test
    public void testUpdateItem() {
        ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        when(mapper.toPersistence(dto)).thenReturn(item);

        itemAutomaticDebitService.updateItem(dto);

        verify(itemAutomaticDebitRepository, times(1)).save(item);
    }

    @Test
    public void testFindRecordsByUniqueId() {
        Order order = new Order();
        ItemAutomaticDebit item = new ItemAutomaticDebit();
        AutomaticDebitPaymentRecord record = new AutomaticDebitPaymentRecord();
        when(orderRepository.findByUniqueId(anyString())).thenReturn(order);
        when(itemAutomaticDebitRepository.findByOrder(order)).thenReturn(Arrays.asList(item));
        when(automaticDebitPaymentRecordRepository.findByItemAutomaticDebit(item)).thenReturn(Arrays.asList(record));

        List<AutomaticDebitPaymentRecord> result = itemAutomaticDebitService.findRecordsByUniqueId("KCV0050548");

        verify(orderRepository, times(1)).findByUniqueId("KCV0050548");
        assert result.size() == 1;
    }

}
