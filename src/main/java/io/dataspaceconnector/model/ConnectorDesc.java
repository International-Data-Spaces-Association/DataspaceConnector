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
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describing connector's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectorDesc extends AbstractDescription<Connector> {

    /**
     * The access url of the connector.
     */
    private URI accessUrl;

    /**
     * The title of the connector.
     */
    private String title;

    /**
     * The registration status of the connector.
     */
    private RegistrationStatus registrationStatus;

}