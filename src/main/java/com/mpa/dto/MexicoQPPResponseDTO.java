package com.mpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MexicoQPPResponseDTO {
    private String account;
    private String printStatus;
    private String supplyStatus;
    private String legalBusinessName;
    private String rfc;
    private String locationId;
    private String country;
}
