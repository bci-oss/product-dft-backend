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
 */

package com.catenax.dft.entities.database;

import com.catenax.dft.enums.ProgressStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "process_report")
@Entity
@Data
@Cacheable(value = false)
public class ProcessReportEntity implements Serializable {
    @Id
    @Column(name = "process_id")
    private String processId;
    @Column(name = "csv_type")
    private String csvType;
    @Column(name = "number_of_items")
    private int numberOfItems;
    @Column(name = "number_of_failed_items")
    private int numberOfFailedItems;
    @Column(name = "number_of_succeeded_items")
    private int numberOfSucceededItems;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressStatusEnum status;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Convert(converter = ListToStringConverter.class)
    @Column(name = "bpn_numbers")
    private List<String> bpnNumbers;

    @Column(name = "type_of_access")
    private String typeOfAccess;
}
