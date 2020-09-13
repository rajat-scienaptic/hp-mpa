package com.mpa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "mpa_accounts_data_change_logs")
public class MpaAccountsDataChangeLogs {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data_id")
    private Integer dataId;
    @Column(name = "account")
    private String account;
    @Column(name = "dba_or_legal_business_name")
    private String dbaOrLegalBusinessName;
    @Column(name = "store_front_name")
    private String storeFrontName;
    @Column(name = "partner_id")
    private String partnerId;
    @Column(name = "location_id")
    private String locationId;
    @Column(name = "mpa_number")
    private String mpaNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    @Column(name = "mpa_date")
    private Date mpaDate;
    @Column(name = "pbm")
    private String pbm;
    @Column(name = "contact")
    private String contact;
    @Column(name = "email")
    private String email;
    @Column(name = "country")
    private String country;
    @Column(name = "market_places")
    private String marketPlaces;
    @Column(name = "status")
    private String status;
    @Column(name = "user_name")
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    @Column(name = "last_modified_timestamp")
    private LocalDateTime lastModifiedTimestamp;
}

