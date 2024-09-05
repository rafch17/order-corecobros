package com.banquito.corecobros.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;
import com.banquito.corecobros.order.service.ItemAutomaticDebitService;

public class ItemAutomaticDebitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemAutomaticDebitService itemAutomaticDebitService;

    @InjectMocks
    private ItemAutomaticDebitController itemAutomaticDebitController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemAutomaticDebitController).build();
    }

    @Test
    public void testGetAllItemAutomaticDebits() throws Exception {
        ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
        when(itemAutomaticDebitService.obtainAllItemAutomaticDebits()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/order-microservice/api/v1/automaticDebits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(itemAutomaticDebitService, times(1)).obtainAllItemAutomaticDebits();
    }

    @Test
    public void testCreateItemAutomaticDebit() throws Exception {
        doNothing().when(itemAutomaticDebitService).createItemAutomaticDebit(any(ItemAutomaticDebitDTO.class));

        mockMvc.perform(post("/order-microservice/api/v1/automaticDebits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1}"))
                .andExpect(status().isOk());

        verify(itemAutomaticDebitService, times(1)).createItemAutomaticDebit(any(ItemAutomaticDebitDTO.class));
    }

    @Test
    public void testGetItemAutomaticDebitById() throws Exception {
        ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
        dto.setId(1);
        when(itemAutomaticDebitService.obtainItemAutomaticDebitById(anyInt())).thenReturn(dto);

        mockMvc.perform(get("/order-microservice/api/v1/automaticDebits/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        verify(itemAutomaticDebitService, times(1)).obtainItemAutomaticDebitById(1);
    }

    @Test
    public void testGetItemAutomaticDebitsByOrderId() throws Exception {
        ItemAutomaticDebitDTO dto = new ItemAutomaticDebitDTO();
        when(itemAutomaticDebitService.getItemAutomaticDebitsByOrderId(anyString())).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/order-microservice/api/v1/automaticDebits/by-order/orderId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(itemAutomaticDebitService, times(1)).getItemAutomaticDebitsByOrderId("orderId");
    }

    @Test
    public void testGetPaymentRecordsByUniqueId() throws Exception {
        AutomaticDebitPaymentRecord record = new AutomaticDebitPaymentRecord();
        when(itemAutomaticDebitService.findRecordsByUniqueId(anyString())).thenReturn(Arrays.asList(record));

        mockMvc.perform(get("/order-microservice/api/v1/automaticDebits/item-automatic-debits/order/uniqueId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(itemAutomaticDebitService, times(1)).findRecordsByUniqueId("uniqueId");
    }

}
