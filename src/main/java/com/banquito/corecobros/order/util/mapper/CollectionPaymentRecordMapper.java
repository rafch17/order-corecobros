package com.banquito.corecobros.order.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.corecobros.order.dto.CollectionPaymentRecordDTO;
import com.banquito.corecobros.order.model.CollectionPaymentRecord;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CollectionPaymentRecordMapper {
    CollectionPaymentRecordDTO toDTO(CollectionPaymentRecord collectionPaymentRecord);
    CollectionPaymentRecord toPersistence(CollectionPaymentRecordDTO collectionPaymentRecordDTO);
}