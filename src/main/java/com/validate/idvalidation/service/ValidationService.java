package com.validate.idvalidation.service;

import com.validate.idvalidation.dto.ValidationResponseDto;
import com.validate.idvalidation.dto.ValidationResultDto;
import com.validate.idvalidation.entity.BuyingMasterTest;
import com.validate.idvalidation.repository.BuyingMasterTestRepository;
import com.validate.idvalidation.repository.MuldmsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final MuldmsRepository muldmsRepository;

    private final BuyingMasterTestRepository buyingMasterTestRepository;

    public ValidationResponseDto validateCsv(
            MultipartFile file,
            int page,
            int size
    ) {

        try {

            List<String> buyingIds = readBuyingIds(file);

            List<String> existingInSource =
                    muldmsRepository.findExistingBuyingIds(buyingIds);

            Set<String> sourceSet = new HashSet<>(existingInSource);

            List<BuyingMasterTest> targetData =
                    buyingMasterTestRepository.findByBuyingIdIn(existingInSource);

            Map<String, String> targetMap = targetData.stream()
                    .collect(Collectors.toMap(
                            BuyingMasterTest::getBuyingId,
                            BuyingMasterTest::getMspin
                    ));

            List<ValidationResultDto> fullResponse = new ArrayList<>();

            for (String buyingId : buyingIds) {

                ValidationResultDto dto = new ValidationResultDto();

                dto.setBuyingId(buyingId);

                boolean existsInSourceTable =
                        sourceSet.contains(buyingId);

                dto.setExistsInSource(existsInSourceTable);

                if (existsInSourceTable &&
                        targetMap.containsKey(buyingId)) {

                    dto.setExistsInTarget(true);

                    dto.setMspin(targetMap.get(buyingId));

                } else {

                    dto.setExistsInTarget(false);

                    dto.setMspin(null);
                }

                fullResponse.add(dto);
            }

            // PAGINATION LOGIC

            int start = page * size;

            int end = Math.min(start + size, fullResponse.size());

            List<ValidationResultDto> paginatedList =
                    fullResponse.subList(start, end);

            ValidationResponseDto response =
                    new ValidationResponseDto();

            response.setTotalRecords(fullResponse.size());

            response.setPage(page);

            response.setSize(size);

            response.setData(paginatedList);

            return response;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to process CSV file",
                    e
            );
        }
    }

    private List<String> readBuyingIds(MultipartFile file) {

        try {

            Reader reader =
                    new InputStreamReader(file.getInputStream());

            CSVParser csvParser =
                    new CSVParser(
                            reader,
                            CSVFormat.DEFAULT
                                    .builder()
                                    .setHeader()
                                    .setSkipHeaderRecord(true)
                                    .build()
                    );

            List<String> buyingIds = new ArrayList<>();

            for (CSVRecord csvRecord : csvParser) {

                String buyingId =
                        csvRecord.get("buyingId");

                if (buyingId != null &&
                        !buyingId.trim().isEmpty()) {

                    buyingIds.add(buyingId.trim());
                }
            }

            return buyingIds;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to read CSV",
                    e
            );
        }
    }
}