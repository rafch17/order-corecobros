package com.banquito.corecobros.order.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.service.PaymentRecordService;

public class PaymentRecordControllerTest {

    @Mock
    private PaymentRecordService paymentRecordService;

    @InjectMocks
    private PaymentRecordController paymentRecordController;

    private CollectionPaymentRecordDTO recordDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recordDTO = new CollectionPaymentRecordDTO();
        // Inicializar recordDTO con valores de prueba si es necesario
    }

    @Test
    void testUpdateCollectionPaymentRecord() {
        Integer id = 1;
        when(paymentRecordService.updatePaymentRecord(id, recordDTO)).thenReturn(recordDTO);

        ResponseEntity<CollectionPaymentRecordDTO> response = paymentRecordController.updateCollectionPaymentRecord(id,
                recordDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(recordDTO, response.getBody());
    }

    @Test
    void testGetPaymentRecordsByAccountId() {
        String accountId = "1";
        List<CollectionPaymentRecordDTO> records = Arrays.asList(recordDTO);
        when(paymentRecordService.findCollectionPaymentRecordsByAccountId(accountId)).thenReturn(records);

        List<CollectionPaymentRecordDTO> response = paymentRecordController.getPaymentRecordsByAccountId(accountId);

        assertEquals(records, response);
    }

    @Test
    void testGetCollectionPaymentRecordsByItemCollectionId() {
        Integer itemCollectionId = 1;
        List<CollectionPaymentRecordDTO> records = Arrays.asList(recordDTO);
        when(paymentRecordService.getCollectionPaymentRecordsByItemCollectionId(itemCollectionId)).thenReturn(records);

        ResponseEntity<List<CollectionPaymentRecordDTO>> response = paymentRecordController
                .getCollectionPaymentRecordsByItemCollectionId(itemCollectionId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(records, response.getBody());
    }

}
