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
package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.EndpointControllers;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for a generic endpoint.
 */
@Component
public class GenericEndpointViewAssembler
        implements RepresentationModelAssembler<GenericEndpoint, GenericEndpointView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                EndpointControllers.GenericEndpointController.class);
    }

    @Override
    public final GenericEndpointView toModel(final GenericEndpoint genericEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(genericEndpoint,
                GenericEndpointView.class);
        view.add(getSelfLink(genericEndpoint.getId()));

        return view;
    }
}
