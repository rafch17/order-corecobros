package com.banquito.corecobros.order.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.banquito.corecobros.order.dto.AutomaticDebitPaymentRecordDTO;
import com.banquito.corecobros.order.model.AutomaticDebitPaymentRecord;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AutomaticDebitPaymentRecordMapper {
    AutomaticDebitPaymentRecordDTO toDTO(AutomaticDebitPaymentRecord automaticDebitPaymentRecord);
    AutomaticDebitPaymentRecord toPersistence(AutomaticDebitPaymentRecordDTO automaticDebitPaymentRecordDTO);
}
