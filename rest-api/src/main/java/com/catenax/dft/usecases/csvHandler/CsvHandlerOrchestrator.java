/*
 * Copyright 2022 CatenaX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.catenax.dft.usecases.csvHandler;


import com.catenax.dft.entities.csv.CsvContent;
import com.catenax.dft.enums.CsvTypeEnum;
import com.catenax.dft.usecases.csvHandler.aspects.MapToAspectCsvHandlerUseCase;
import com.catenax.dft.usecases.csvHandler.childAspects.MapToChildAspectCsvHandlerUseCase;
import com.catenax.dft.usecases.processReport.ProcessReportUseCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CsvHandlerOrchestrator {

    private final MapToAspectCsvHandlerUseCase aspectStarterUseCase;
    private final MapToChildAspectCsvHandlerUseCase childAspectStarterUseCase;
    private final ProcessReportUseCase processReportUseCase;


    private final List<String> ASPECT_COLUMNS = Stream.of(
                    "uuid",
                    "part_instance_id",
                    "manufacturing_date",
                    "manufacturing_country",
                    "manufacturer_part_id",
                    "customer_part_id",
                    "classification",
                    "name_at_manufacturer",
                    "name_at_customer",
                    "optional_identifier_key",
                    "optional_identifier_value")
            .collect(Collectors.toList());
    private final List<String> CHILD_ASPECT_COLUMNS = Stream.of(
                    "parent_part_instance_id",
                    "lifecycle_context",
                    "quantity_number",
                    "measurement_unit_lexical_value")
            .collect(Collectors.toList());


    public CsvHandlerOrchestrator(MapToAspectCsvHandlerUseCase aspectStarterUseCase, MapToChildAspectCsvHandlerUseCase childAspectStarterUseCase,
                                  ProcessReportUseCase processReportUseCase) {
        this.aspectStarterUseCase = aspectStarterUseCase;
        this.childAspectStarterUseCase = childAspectStarterUseCase;
        this.processReportUseCase = processReportUseCase;
    }

    @SneakyThrows
    public void execute(CsvContent csvContent, String processId) {
        if (ASPECT_COLUMNS.equals(csvContent.getColumns())) {

            processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.ASPECT, csvContent.getRows().size(), LocalDateTime.now());
            log.info("I'm an ASPECT file. Unpacked and ready to be processed.");
            csvContent.getRows().parallelStream().forEach(input -> aspectStarterUseCase.run(input, processId));
            processReportUseCase.finishBuildAspectProgressReport(processId);


        } else if (CHILD_ASPECT_COLUMNS.equals(csvContent.getColumns())) {

            processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.CHILD_ASPECT, csvContent.getRows().size(), LocalDateTime.now());
            log.info("I'm an CHILD ASPECT file. Unpacked and ready to be processed.");
            csvContent.getRows().parallelStream().forEach(input -> childAspectStarterUseCase.run(input, processId));
            processReportUseCase.finishBuildChildAspectProgressReport(processId);

        } else {
            processReportUseCase.unknownProcessReport(processId, LocalDateTime.now());
            throw new Exception("I don't know what to do with you");
        }
    }
}
