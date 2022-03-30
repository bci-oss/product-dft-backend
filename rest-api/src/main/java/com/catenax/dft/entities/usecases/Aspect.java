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

package com.catenax.dft.entities.usecases;

import com.catenax.dft.enums.OptionalIdentifierKeyEnum;
import com.catenax.dft.validators.AspectValidation;
import com.catenax.dft.validators.OptionalIdentifierKeyValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@AspectValidation
public class Aspect {

    private String uuid;

    private String processId;

    @NotBlank(message = "part_instance_id cannot be empty")
    private String partInstanceId;

    @NotBlank(message = "manufacturing_date cannot be empty")
    private String manufacturingDate;

    private String manufacturingCountry;

    @NotBlank(message = "manufacturing_part_id cannot be empty")
    private String manufacturerPartId;

    private String customerPartId;

    @NotBlank(message = "classification cannot be empty")
    private String classification;

    @NotBlank(message = "name_at_manufacturer cannot be empty")
    private String nameAtManufacturer;

    private String nameAtCustomer;

    @OptionalIdentifierKeyValidation
    private String optionalIdentifierKey;

    private String optionalIdentifierValue;
}
