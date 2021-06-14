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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.App;
import io.dataspaceconnector.model.AppStore;
import io.dataspaceconnector.model.Broker;
import io.dataspaceconnector.model.ConnectorEndpoint;
import io.dataspaceconnector.model.GenericEndpoint;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Route;
import io.dataspaceconnector.services.resources.OfferedResourceService;
import io.dataspaceconnector.services.resources.OwningRelationService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains all implementations of {@link OwningRelationService}.
 */
public final class EntityLinkerService {

    /**
     * Handles the relation between app store and apps.
     */
    @Service
    @NoArgsConstructor
    public static class AppStoreAppLinker
            extends OwningRelationService<AppStore, App, AppStoreService, AppService> {

        @Override
        protected final List<App> getInternal(final AppStore owner) {
            return owner.getAppList();
        }
    }

    /**
     * Handles the relation between broker and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class BrokerOfferedResourcesLinker
            extends OwningRelationService<Broker, OfferedResource, BrokerService,
            OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Broker owner) {
            return owner.getOfferedResources();
        }
    }

    /**
     * Handles the relation between the routes and subroutes.
     */
    @Service
    @NoArgsConstructor
    public static class RouteSubrouteLinker
            extends OwningRelationService<Route, Route, RouteService, RouteService> {

        @Override
        protected final List<Route> getInternal(final Route owner) {
            return owner.getSubRoutes();
        }
    }

    /**
     * Handles the relation between the route and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class RouteOfferedResourceLinker
            extends OwningRelationService<Route, OfferedResource, RouteService,
            OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Route owner) {
            return owner.getOfferedResources();
        }
    }

    /**
     * Handles the relation between the route and start endpoint.
     */
    @Service
    @NoArgsConstructor
    public static class RouteStartEndpointLinker
            extends OwningRelationService<Route, GenericEndpoint, RouteService,
            GenericEndpointService> {

        @Override
        protected final List<GenericEndpoint> getInternal(final Route owner) {
            return (List<GenericEndpoint>) (List<?>) owner.getStartEndpoint();
        }
    }

    /**
     * Handles the relation between the route and last endpoint.
     */
    @Service
    @NoArgsConstructor
    public static class RouteLastEndpointLinker
            extends OwningRelationService<Route, ConnectorEndpoint, RouteService,
            ConnectorEndpointService> {

        @Override
        protected final List<ConnectorEndpoint> getInternal(final Route owner) {
            return (List<ConnectorEndpoint>) (List<?>) owner.getLastEndpoint();
        }
    }

}
