package com.validate.idvalidation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sh_tv_eval", schema = "muldms")
@Getter
@Setter
public class ShTvEvl {

    @Id
    @Column(name = "buying_num")
    private String buyingNum;
}