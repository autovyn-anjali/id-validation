package com.validate.idvalidation.service;

import com.validate.idvalidation.dto.ValidationResponseDto;
import com.validate.idvalidation.dto.ValidationResultDto;
import com.validate.idvalidation.entity.BuyingMasterTest;
import com.validate.idvalidation.repository.BuyingMasterTestRepository;
import com.validate.idvalidation.repository.MuldmsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {

    private final MuldmsRepository muldmsRepository;

    private final BuyingMasterTestRepository buyingMasterTestRepository;

    public ValidationResponseDto validateCsv(
            MultipartFile file,
            int page,
            int size
    ) {

        String lastBuyingId = null;
        Boolean lastExistsInSource = null;
        Boolean lastExistsInTarget = null;
        String lastMspin = null;
        try {

            List<String> buyingIds = readBuyingIds(file);

            List<String> existingInSource =
                    muldmsRepository.findExistingBuyingIds(buyingIds);

            Set<String> sourceSet = new HashSet<>(existingInSource);

            List<BuyingMasterTest> targetData =
                    buyingMasterTestRepository.findByBuyingIdIn(existingInSource);

            Map<String, String> targetMap = new HashMap<>();

            for (BuyingMasterTest data : targetData) {

                if (data.getBuyingId() != null) {

                    targetMap.put(
                            data.getBuyingId(),
                            data.getMspin()
                    );
                }
            }


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
                // Store last processed values
                lastBuyingId = buyingId;
                lastExistsInSource = dto.isExistsInSource();
                lastExistsInTarget = dto.isExistsInTarget();
                lastMspin = dto.getMspin();
                fullResponse.add(dto);
                log.info("Processed buyingId: {} | existsInSource: {} | existsInTarget: {} | mspin: {}",
                        buyingId, dto.isExistsInSource(), dto.isExistsInTarget(), dto.getMspin());
            }

            // PAGINATION LOGIC

            int start = page * size;

            List<ValidationResultDto> paginatedList;

            if (start >= fullResponse.size()) {

                paginatedList = Collections.emptyList();

            } else {

                int end = Math.min(start + size, fullResponse.size());

                paginatedList = fullResponse.subList(start, end);
            }


            ValidationResponseDto response =
                    new ValidationResponseDto();

            response.setTotalRecords(fullResponse.size());

            response.setPage(page);

            response.setSize(size);

            response.setData(paginatedList);

            return response;

        } catch (Exception e) {
            log.error("API failed. Last processed values - buyingId: {}, existsInSource: {}, existsInTarget: {}, mspin: {}",
                    lastBuyingId, lastExistsInSource, lastExistsInTarget, lastMspin);
            throw new RuntimeException(
                    "Failed to process CSV file",
                    e
            );
        }
    }

    private List<String> readBuyingIds(MultipartFile file) {
        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser csvParser = new CSVParser(
                    reader,
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
            );

            // List of possible header names for buyingId
            List<String> possibleHeaders = Arrays.asList("buyingId", "buying_id");
            String buyingIdHeader = null;

            // Find the correct header present in the CSV
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            for (String header : possibleHeaders) {
                if (headerMap.containsKey(header)) {
                    buyingIdHeader = header;
                    break;
                }
            }

            if (buyingIdHeader == null) {
                throw new RuntimeException("No valid buyingId header found in CSV. Expected one of: " + possibleHeaders);
            }

            List<String> buyingIds = new ArrayList<>();
            for (CSVRecord csvRecord : csvParser) {
                String buyingId = csvRecord.get(buyingIdHeader);
                if (buyingId != null && !buyingId.trim().isEmpty()) {
                    buyingIds.add(buyingId.trim());
                }
            }
            return buyingIds;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV", e);
        }
    }
}