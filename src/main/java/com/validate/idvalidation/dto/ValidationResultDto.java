package com.validate.idvalidation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationResultDto {

    private String buyingId;

    private boolean existsInSource;

    private boolean existsInTarget;

    private String mspin;
}