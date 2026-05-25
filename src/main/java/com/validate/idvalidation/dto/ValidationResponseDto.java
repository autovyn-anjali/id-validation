package com.validate.idvalidation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidationResponseDto {

    private int totalRecords;

    private int page;

    private int size;

    private List<ValidationResultDto> data;
}