package com.banquito.corecobros.order.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.corecobros.order.dto.ItemCollectionDTO;
import com.banquito.corecobros.order.model.ItemCollection;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemCollectionMapper {
    ItemCollectionDTO toDTO(ItemCollection itemCollection);
    ItemCollection toPersistence(ItemCollectionDTO itemCollectionDTO);
}
