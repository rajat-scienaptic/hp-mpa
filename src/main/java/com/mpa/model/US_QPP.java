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
@Table(name = "us_qpp")
public class US_QPP {
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
}
