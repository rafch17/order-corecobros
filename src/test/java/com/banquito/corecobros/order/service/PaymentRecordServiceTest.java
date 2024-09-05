package com.banquito.corecobros.order.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.model.Order;
import com.banquito.corecobros.order.repository.AutomaticDebitPaymentRecordRepository;
import com.banquito.corecobros.order.repository.CollectionPaymentRecordRepository;
import com.banquito.corecobros.order.repository.OrderRepository;
import com.banquito.corecobros.order.util.mapper.AutomaticDebitPaymentRecordMapper;
import com.banquito.corecobros.order.util.mapper.CollectionPaymentRecordMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentRecordServiceTest {

    @Mock
    private CollectionPaymentRecordRepository collectionPaymentRecordRepository;

    @Mock
    private AutomaticDebitPaymentRecordRepository automaticDebitPaymentRecordRepository;

    @Mock
    private AutomaticDebitPaymentRecordMapper automaticDebitPaymentRecordMapper;

    @Mock
    private CollectionPaymentRecordMapper collectionPaymentRecordMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentRecordService paymentRecordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCollectionPaymentRecord() {
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        when(collectionPaymentRecordMapper.toPersistence(dto)).thenReturn(record);
        when(collectionPaymentRecordRepository.save(record)).thenReturn(record);

        CollectionPaymentRecord result = paymentRecordService.createCollectionPaymentRecord(dto);

        verify(collectionPaymentRecordRepository).save(record);
        assertEquals(record, result);
    }

    @Test
    public void testCreateAutomaticDebitPaymentRecord() {
        AutomaticDebitPaymentRecordDTO dto = mock(AutomaticDebitPaymentRecordDTO.class);
        AutomaticDebitPaymentRecord record = mock(AutomaticDebitPaymentRecord.class);
        when(automaticDebitPaymentRecordMapper.toPersistence(dto)).thenReturn(record);
        when(automaticDebitPaymentRecordRepository.save(record)).thenReturn(record);

        AutomaticDebitPaymentRecord result = paymentRecordService.createAutomaticDebitPaymentRecord(dto);

        verify(automaticDebitPaymentRecordRepository).save(record);
        assertEquals(record, result);
    }

    @Test
    public void testGetAll() {
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        when(collectionPaymentRecordRepository.findAll()).thenReturn(Arrays.asList(record));
        when(collectionPaymentRecordMapper.toDTO(record)).thenReturn(dto);

        List<CollectionPaymentRecordDTO> result = paymentRecordService.getAll();

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    public void testGetPaymentRecordById() {
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        when(collectionPaymentRecordRepository.findById(anyInt())).thenReturn(Optional.of(record));
        when(collectionPaymentRecordMapper.toDTO(record)).thenReturn(dto);

        CollectionPaymentRecordDTO result = paymentRecordService.getPaymentRecordById(1);

        assertEquals(dto, result);
    }

    @Test
    public void testGetPaymentRecordByIdNotFound() {
        when(collectionPaymentRecordRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> paymentRecordService.getPaymentRecordById(1));
    }

    @Test
    public void testUpdatePaymentRecord() {
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        when(collectionPaymentRecordRepository.findById(anyInt())).thenReturn(Optional.of(record));
        when(collectionPaymentRecordRepository.save(record)).thenReturn(record);
        when(collectionPaymentRecordMapper.toDTO(record)).thenReturn(dto);

        CollectionPaymentRecordDTO result = paymentRecordService.updatePaymentRecord(1, dto);

        verify(collectionPaymentRecordRepository).save(record);
        assertEquals(dto, result);
    }

    @Test
    public void testFindCollectionPaymentRecordsByAccountId() {
        Order order = mock(Order.class);
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        when(orderRepository.findByAccountId(anyString())).thenReturn(Arrays.asList(order));
        when(order.getItemCollections()).thenReturn(Arrays.asList(mock(ItemCollection.class)));
        when(collectionPaymentRecordRepository.findByItemCollectionIdIn(any())).thenReturn(Arrays.asList(record));
        when(collectionPaymentRecordMapper.toDTO(record)).thenReturn(dto);

        List<CollectionPaymentRecordDTO> result = paymentRecordService
                .findCollectionPaymentRecordsByAccountId("1");

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    public void testGetCollectionPaymentRecordsByItemCollectionId() {
        CollectionPaymentRecord record = mock(CollectionPaymentRecord.class);
        CollectionPaymentRecordDTO dto = mock(CollectionPaymentRecordDTO.class);
        when(collectionPaymentRecordRepository.findByItemCollectionId(anyInt())).thenReturn(Arrays.asList(record));
        when(collectionPaymentRecordMapper.toDTO(record)).thenReturn(dto);

        List<CollectionPaymentRecordDTO> result = paymentRecordService.getCollectionPaymentRecordsByItemCollectionId(1);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    public void testGenerateUniqueId() {
        when(automaticDebitPaymentRecordRepository.existsByUniqueId(anyString())).thenReturn(false);

        String uniqueId = paymentRecordService.generateUniqueId();

        assertEquals(10, uniqueId.length()); 
    }

}
