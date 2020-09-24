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
@Table(name = "us_qpp")
public class UsQPP {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account")
    private String account;
    @Column(name = "dba")
    private String dba;
    @Column(name = "new_hp_inc_location_id")
    private String locationId;
    @Column(name = "qualified_print_status")
    private String qualifiedPrintStatus;
    @Column(name = "qualified_supplies_status")
    private String qualifiedSuppliesStatus;
}
