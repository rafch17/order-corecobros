package com.banquito.corecobros.order.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.service.ItemCollectionService;

@WebMvcTest(ItemCollectionController.class)
public class ItemCollectionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ItemCollectionService itemCollectionService;

        @Test
        public void testGetItemCollectionsByCounterpartAndCompany() throws Exception {
                when(itemCollectionService.findByCounterpartAndCompany(anyString(), anyString()))
                                .thenReturn(Arrays.asList(new ItemCollectionDTO()));

                mockMvc.perform(get("/order-microservice/api/v1/collections/search")
                                .param("counterpart", "1798765432001")
                                .param("companyId", "JXM0025321"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetAllItemCollections() throws Exception {
                when(itemCollectionService.obtainAllItemCollections())
                                .thenReturn(Arrays.asList(new ItemCollectionDTO()));

                mockMvc.perform(get("/order-microservice/api/v1/collections"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetItemCollectionById() throws Exception {
                when(itemCollectionService.obtainItemCollectionById(anyInt()))
                                .thenReturn(new ItemCollectionDTO());

                mockMvc.perform(get("/order-microservice/api/v1/collections/id/1"))
                                .andExpect(status().isOk());

                when(itemCollectionService.obtainItemCollectionById(anyInt()))
                                .thenThrow(new RuntimeException());

                mockMvc.perform(get("/order-microservice/api/v1/collections/id/1"))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testGetItemCollectionsByStatus() throws Exception {
                when(itemCollectionService.obtainItemCollectionsByStatus(anyString()))
                                .thenReturn(Arrays.asList(new ItemCollectionDTO()));

                mockMvc.perform(get("/order-microservice/api/v1/collections/item-collections/status/testStatus"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetActiveItemCollections() throws Exception {
                when(itemCollectionService.findActiveItemCollections())
                                .thenReturn(Arrays.asList(new ItemCollectionDTO()));

                mockMvc.perform(get("/order-microservice/api/v1/collections/active"))
                                .andExpect(status().isOk());
        }

        @Test
        public void testGetItemCollectionsByOrderId() throws Exception {
                when(itemCollectionService.getItemCollectionsByOrderId(anyInt()))
                                .thenReturn(Arrays.asList(new ItemCollectionDTO()));

                mockMvc.perform(get("/order-microservice/api/v1/collections/by-order/1"))
                                .andExpect(status().isOk());
        }
}
