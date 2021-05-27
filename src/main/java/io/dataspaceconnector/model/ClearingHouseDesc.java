/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;
/**
 * Describing the clearing house's properties.
 */
@Data
@NoArgsConstructor
public class ClearingHouseDesc extends AbstractDescription<ClearingHouse> {

    /**
     * The access url of the clearing house.
     */
    private URI accessUrl;

    /**
     * The title of the clearing house.
     */
    private String title;

    /**
     * The status of registration.
     */
    private RegisterStatus registerStatus;
}