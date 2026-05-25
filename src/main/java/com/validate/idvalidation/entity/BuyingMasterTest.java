package com.validate.idvalidation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "buying_master_test", schema = "public")
@Getter
@Setter
public class BuyingMasterTest {

    @Id
    @Column(name = "buying_id")
    private String buyingId;

    @Column(name = "ms_pin")
    private String mspin;
}