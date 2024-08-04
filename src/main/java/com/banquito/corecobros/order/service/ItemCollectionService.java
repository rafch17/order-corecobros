package com.banquito.corecobros.order.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import com.banquito.corecobros.order.dto.CompanyDTO;
import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.model.ItemCollection;
import com.banquito.corecobros.order.repository.ItemCollectionRepository;
import com.banquito.corecobros.order.util.mapper.ItemCollectionMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemCollectionService {
    private final ItemCollectionRepository itemCollectionRepository;
    private final ItemCollectionMapper itemCollectionMapper;
    private final WebClient webClient;
    

    public ItemCollectionService(ItemCollectionRepository itemCollectionRepository,
            ItemCollectionMapper itemCollectionMapper, WebClient webClient, WebClient.Builder webClientBuilder) {
        this.itemCollectionRepository = itemCollectionRepository;
        this.itemCollectionMapper = itemCollectionMapper;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api/v1/companies").build();
    }

    public List<ItemCollectionDTO> findByCounterpartAndCompany(String counterpart, String companyId) {
        CompanyDTO company = this.webClient.get()
                .uri("/{uniqueId}", companyId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CompanyDTO.class)
                .block();

        if (company != null) {
            log.info("Compañia encontrada: {}", company);
            if (company.getUniqueId().equals(companyId) && company.getRuc().equals(counterpart)) {
                log.info("Contrpartida encontrada: {}", counterpart);
                List<ItemCollection> itemCollections = itemCollectionRepository.findByCounterpart(counterpart);
                return itemCollections.stream()
                        .filter(item -> "PEN".equals(item.getStatus()))
                        .map(this.itemCollectionMapper::toDTO)
                        .collect(Collectors.toList());
            } else {
                log.info("Contrapartida o companyId no encontradas");
            }
        } else {
            log.info("No se encontro ninguna compañia con uniqueId: {}", companyId);
        }
        return List.of();
    }

    public void createItemCollection(ItemCollectionDTO dto) {
        ItemCollection itemCollection = this.itemCollectionMapper.toPersistence(dto);
        ItemCollection savedItemCollection = this.itemCollectionRepository.save(itemCollection);
        log.info("Se creo la orden: {}", savedItemCollection);
    }

    public List<ItemCollectionDTO> obtainAllItemCollections() {
        log.info("Va a retornar todas las ordenes");
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findAll();
        return itemCollections.stream().map(s -> this.itemCollectionMapper.toDTO(s)).collect(Collectors.toList());
    }

    public ItemCollectionDTO obtainItemCollectionById(Integer id) {
        ItemCollection itemCollection = this.itemCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        return this.itemCollectionMapper.toDTO(itemCollection);
    }

    public void updateItemCollection(Integer id, String status) {
        ItemCollection itemCollection = this.itemCollectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la orden con el ID " + id));
        itemCollection.setStatus(status);
        this.itemCollectionRepository.save(itemCollection);
    }

    public List<ItemCollectionDTO> obtainItemCollectionsByCounterpartAndStatus(String counterpart, String status) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByCounterpartAndStatus(counterpart,
                status);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> obtainItemCollectionsByStatus(String status) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus(status);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> findActiveItemCollections() {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByStatus("ACT");
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public List<ItemCollectionDTO> getItemCollectionsByOrderId(Integer id) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByOrderId(id);
        return itemCollections.stream().map(this.itemCollectionMapper::toDTO).collect(Collectors.toList());
    }

    public ItemCollectionDTO findByCounterpart(String counterpart) {
        List<ItemCollection> itemCollections = this.itemCollectionRepository.findByCounterpart(counterpart);
        if (itemCollections.isEmpty()) {
            throw new RuntimeException("No se encontró la orden con la contrapartida " + counterpart);
        }
        return this.itemCollectionMapper.toDTO(itemCollections.get(0));
    }
    public void processCsvFile(MultipartFile file, Integer orderId) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().build())) {
            for (CSVRecord csvRecord : csvParser) {
                String debtorName = csvRecord.get("debtorName");
                String counterpart = csvRecord.get("counterpart");
                String collectionAmount = csvRecord.get("collectionAmount");

                ItemCollectionDTO dto = new ItemCollectionDTO();
                dto.setOrderId(orderId);
                dto.setDebtorName(debtorName);
                dto.setCounterpart(counterpart);
                dto.setCollectionAmount(new BigDecimal(collectionAmount));
                dto.setStatus("PEN");

                this.createItemCollection(dto);
            }
            log.info("Archivo CSV procesado con éxito.");
        } catch (IOException e) {
            log.error("Error procesando el archivo CSV", e);
            throw e;
        }
    }
}
