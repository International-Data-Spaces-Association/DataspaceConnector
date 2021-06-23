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

import io.dataspaceconnector.controller.configurations.RouteControllers;
import io.dataspaceconnector.model.Route;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a route.
 */
@Component
public class RouteViewAssembler
        implements RepresentationModelAssembler<Route, RouteView>, SelfLinking {


    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                RouteControllers.RouteController.class);
    }

    @Override
    public final RouteView toModel(final Route route) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(route,
                RouteView.class);
        view.add(getSelfLink(route.getId()));

        final var subroutes = linkTo(methodOn(RouteControllers.RoutesToSubroutes.class)
                .getResource(route.getId(), null, null))
                .withRel("routes");
        view.add(subroutes);

        final var offeredResources =
                linkTo(methodOn(RouteControllers.RoutesToOfferedResources.class)
                        .getResource(route.getId(), null, null))
                        .withRel("resources");
        view.add(offeredResources);

        final var startEndpoint =
                linkTo(methodOn(RouteControllers.RoutesToStartEndpoint.class)
                        .getResource(route.getId(), null, null))
                        .withRel("endpoints");
        view.add(startEndpoint);

        final var lasEndpoint =
                linkTo(methodOn(RouteControllers.RoutesToLastEndpoint.class)
                        .getResource(route.getId(), null, null))
                        .withRel("endpoints");
        view.add(lasEndpoint);

        return view;
    }
}