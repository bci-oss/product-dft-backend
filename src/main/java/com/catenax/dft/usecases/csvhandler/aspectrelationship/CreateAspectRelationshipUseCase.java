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

package com.catenax.dft.usecases.csvhandler.aspectrelationship;

import java.util.List;

import org.springframework.stereotype.Service;

import com.catenax.dft.entities.SubmodelJsonRequest;
import com.catenax.dft.entities.aspectrelationship.AspectRelationshipRequest;
import com.catenax.dft.enums.CsvTypeEnum;
import com.catenax.dft.usecases.processreport.ProcessReportUseCase;

@Service
public class CreateAspectRelationshipUseCase {
	private final MapFromAspectRelationshipRequestUseCase useCase;
	private final ProcessReportUseCase processReportUseCase;

	public CreateAspectRelationshipUseCase(MapFromAspectRelationshipRequestUseCase useCase,
			ProcessReportUseCase processReportUseCase) {
		this.useCase = useCase;
		this.processReportUseCase = processReportUseCase;
	}

	public void createAspects(SubmodelJsonRequest<AspectRelationshipRequest> aspectInputs, String processId) {
		List<AspectRelationshipRequest> rowData = aspectInputs.getRowData();
		processReportUseCase.startBuildProcessReport(processId, CsvTypeEnum.ASPECT_RELATIONSHIP, rowData.size(),aspectInputs.getBpnNumbers(),aspectInputs.getTypeOfAccess());

		for (int i = 0; i < rowData.size(); i++) {
			AspectRelationshipRequest aspect = rowData.get(i);
			aspect.setRowNumber(i);
			aspect.setProcessId(processId);
			aspect.setBpnNumbers(aspectInputs.getBpnNumbers());
			useCase.run(aspect, processId);
		}

		processReportUseCase.finishBuildChildAspectProgressReport(processId);
	}

}
