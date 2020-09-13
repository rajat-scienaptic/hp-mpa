package com.mpa.model;

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
@Table(name = "mexico_qpp")
public class MEXICO_QPP {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account")
    private String account;
    @Column(name = "print_status")
    private String printStatus;
    @Column(name = "supply_status")
    private String supplyStatus;
    @Column(name = "total_revenue")
    private String totalRevenue;
    @Column(name = "dba_or_legal_business_name")
    private String dbaOrLegalBusinessName;
    @Column(name = "rfc")
    private String rfc;
    @Column(name = "locationId")
    private String locationId;
    @Column(name = "commercial_consumer")
    private String commercialConsumer;
    @Column(name = "relationship")
    private String relationship;
    @Column(name = "comment")
    private String comment;
    @Column(name = "country")
    private String country;
}
