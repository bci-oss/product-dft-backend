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

package com.catenax.dft.usecases.csvhandler;


import com.catenax.dft.entities.SubmodelFileRequest;
import com.catenax.dft.entities.csv.CsvContent;
import com.catenax.dft.enums.CsvTypeEnum;
import com.catenax.dft.exceptions.DftException;
import com.catenax.dft.usecases.csvhandler.aspectrelationship.MapToAspectRelationshipCsvHandlerUseCase;
import com.catenax.dft.usecases.csvhandler.aspects.MapToAspectCsvHandlerUseCase;
import com.catenax.dft.usecases.processreport.ProcessReportUseCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CsvHandlerOrchestrator {

    private static final List<String> ASPECT_COLUMNS = Stream.of(
            "UUID",
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
    private static final List<String> ASPECT_RELATIONSHIP_COLUMNS = Stream.of(
            "parent_UUID",
            "parent_part_instance_id",
            "parent_manufacturer_part_id",
            "parent_optional_identifier_key",
            "parent_optional_identifier_value",
            "UUID",
            "part_instance_id",
            "manufacturer_part_id",
            "optional_identifier_key",
            "optional_identifier_value",
            "lifecycle_context",
            "quantity_number",
            "measurement_unit_lexical_value",
            "datatype_URI",
            "assembled_on")
            .collect(Collectors.toList());

    private final MapToAspectCsvHandlerUseCase aspectStarterUseCase;
    private final MapToAspectRelationshipCsvHandlerUseCase aspectRelationshipStarterUseCase;
    private final ProcessReportUseCase processReportUseCase;

    public CsvHandlerOrchestrator(MapToAspectCsvHandlerUseCase aspectStarterUseCase,
                                  MapToAspectRelationshipCsvHandlerUseCase aspectRelationshipStarterUseCase,
                                  ProcessReportUseCase processReportUseCase) {
        this.aspectStarterUseCase = aspectStarterUseCase;
        this.aspectRelationshipStarterUseCase = aspectRelationshipStarterUseCase;
        this.processReportUseCase = processReportUseCase;
    }

    public static int getAspectColumnSize(){
        return ASPECT_COLUMNS.size();
    }

    public static int getAspectRelationshipColumnSize(){
        return ASPECT_RELATIONSHIP_COLUMNS.size();
    }

    @SneakyThrows
    public void execute(CsvContent csvContent, String processId, SubmodelFileRequest submodelFileRequest) {
        if (ASPECT_COLUMNS.equals(csvContent.getColumns())) {
            processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.ASPECT, csvContent.getRows().size(),submodelFileRequest.getBpnNumbers(),submodelFileRequest.getTypeOfAccess());
            log.debug("I'm an ASPECT file. Unpacked and ready to be processed.");
            
            csvContent.getRows().parallelStream().forEach(input ->{
            	
            	aspectStarterUseCase.init(submodelFileRequest);
            	aspectStarterUseCase.run(input, processId);
            	});
            
            processReportUseCase.finishBuildAspectProgressReport(processId);
        } else if (ASPECT_RELATIONSHIP_COLUMNS.equals(csvContent.getColumns())) {
            processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.ASPECT_RELATIONSHIP, csvContent.getRows().size(),submodelFileRequest.getBpnNumbers(),submodelFileRequest.getTypeOfAccess());
            log.debug("I'm an ASPECT RELATIONSHIP file. Unpacked and ready to be processed.");
            
            csvContent.getRows().parallelStream().forEach(input -> {
            	aspectRelationshipStarterUseCase.init(submodelFileRequest);
            	aspectRelationshipStarterUseCase.run(input, processId);
            });
            
            processReportUseCase.finishBuildChildAspectProgressReport(processId);
        } else {
            processReportUseCase.unknownProcessReport(processId);
            throw new DftException("Unrecognized CSV content. I don't know what to do with you");
        }
    }
}