package com.mpa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDataDTO {
    private Integer id;
    private String storeFrontName;
    private String dbaOrLegalBusinessName;
    private String partnerId;
    private String locationId;
    private String rfc;
    private String status;
    private String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date mpaDate;
    private String mpaNumber;
    private List<String> existingCountryMarketPlaces;
    private List<String> upcomingCountryMarketPlaces;
}
