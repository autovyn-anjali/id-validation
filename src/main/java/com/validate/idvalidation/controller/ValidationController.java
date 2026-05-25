package com.validate.idvalidation.controller;

import com.validate.idvalidation.dto.ValidationResponseDto;
import com.validate.idvalidation.service.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
public class ValidationController {

    private final ValidationService validationService;

    @PostMapping("/upload")
    public ResponseEntity<ValidationResponseDto> uploadCsv(

            @RequestPart("file")
            MultipartFile file,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        ValidationResponseDto response = validationService.validateCsv(file , page , size );

        return ResponseEntity.ok(response);
    }
}