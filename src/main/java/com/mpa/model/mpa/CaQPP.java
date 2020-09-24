package com.mpa.model.mpa;

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
@Table(name = "ca_qpp")
public class CaQPP {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "company_legal_name")
    private String companyLegalName;
    @Column(name = "dba")
    private String dba;
    @Column(name = "hpi_location_id")
    private String locationId;
    @Column(name = "qualified_print_hw_status")
    private String qualifiedPrintStatus;
    @Column(name = "qualified_supplies_status")
    private String qualifiedSuppliesStatus;
}
