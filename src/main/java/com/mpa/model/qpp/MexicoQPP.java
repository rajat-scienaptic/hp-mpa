package com.mpa.model.qpp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "account_summary")
public class MexicoQPP {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account")
    private String account;
    @Column(name = "prints_status")
    private String printsStatus;
    @Column(name = "supplies_status")
    private String suppliesStatus;
    @Column(name = "legal_business_name")
    private String legalBusinessName;
    @Column(name = "rfc")
    private String rfc;
    @Column(name = "location_id")
    private String locationId;
}
