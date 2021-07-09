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
package io.dataspaceconnector.model.endpoint;

import java.net.URI;

import org.springframework.stereotype.Component;

/**
 * Creates and updates connector endpoints.
 */
@Component
public class ConnectorEndpointFactory
        extends EndpointFactory<ConnectorEndpoint, ConnectorEndpointDesc> {

    /**
     * Default absolute path.
     */
    private static final URI DEFAULT_ACCESS_URL = URI.create("https://path");

    /**
     * @param desc The description passed to the factory.
     * @return The new connector endpoint.
     */
    @Override
    protected ConnectorEndpoint initializeEntity(final ConnectorEndpointDesc desc) {
        return new ConnectorEndpoint();
    }
}