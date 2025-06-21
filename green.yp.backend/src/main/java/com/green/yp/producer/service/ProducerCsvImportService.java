package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.*;
import com.green.yp.exception.SystemException;
import com.green.yp.producer.service.dataimp.ProducerImportService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerCsvImportService {

  private final ProducerImportService importService;

  @Value("${green.yp.import.threads:5}")
  private int importThreads;

  @Transactional
  public List<UUID> importProducersFromCsv(MultipartFile file, UUID lineOfBusinessId) {

    List<UUID> createdProducerIds = new ArrayList<>();

    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      CSVParser csvParser =
          CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).build().parse(reader);

      List<CSVRecord> records = csvParser.getRecords();

      var customThreadPool = new ForkJoinPool(importThreads);
      customThreadPool.submit(() ->
      records.parallelStream()
          .filter(csvRecord -> csvRecord.getRecordNumber() != 1)
              .map(csvRecord -> new ProducerImportService.ProducerCsvRecord(
                      csvRecord.getRecordNumber(),
                      csvRecord.get(0),
                      csvRecord.get(1),
                      csvRecord.get(2),
                      csvRecord.get(3),
                      csvRecord.get(4),
                      csvRecord.get(5),
                      csvRecord.get(6),
                      csvRecord.get(7),
                      csvRecord.get(8),
                      StringUtils.truncate(csvRecord.get(9), 12)))
              .forEach( parsedRecord -> {
                  try{
                    UUID createdProducerId = importService.importProducer(lineOfBusinessId, parsedRecord);
                    createdProducerIds.add(createdProducerId);
                    log.info("Imported : {}", parsedRecord);
                } catch (Exception e) {
                  log.error("Error processing {}", parsedRecord, e);
                  throw new SystemException(
                      "Failed to process csv record: " + parsedRecord.recordNumber(), e);
                }
              }));
    }catch (SystemException se){
      throw se;
    }
    catch (Exception e) {
      log.error("Error processing CSV file", e);
      throw new SystemException("Failed to process CSV file: " + e.getMessage(), e);
    }
    return createdProducerIds;
  }
}