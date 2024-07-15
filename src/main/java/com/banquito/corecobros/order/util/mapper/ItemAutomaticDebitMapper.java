package com.banquito.corecobros.order.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.corecobros.order.dto.ItemAutomaticDebitDTO;
import com.banquito.corecobros.order.model.ItemAutomaticDebit;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ItemAutomaticDebitMapper {
    ItemAutomaticDebitDTO toDTO(ItemAutomaticDebit itemAutomaticDebit);
    ItemAutomaticDebit toPersistence(ItemAutomaticDebitDTO itemAutomaticDebitDTO);
}
