package com.mpa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class MpaAccountsDTO {
    private Integer id;
    private String account;
    private String rfc;
    private String dbaOrLegalBusinessName;
    private String storeFrontName;
    private String partnerId;
    private String locationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private Date mpaDate;
    private String mpaNumber;
    private String country;
    private String marketPlaces;
    private String status;
}
