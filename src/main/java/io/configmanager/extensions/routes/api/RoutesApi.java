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
package io.configmanager.extensions.routes.api;

import io.configmanager.util.enums.RouteDeployMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;


public interface RoutesApi {
    /**
     * Creates a new app route.
     * @param description The description of the new app route.
     * @return The response message or an error.
     */
    @PostMapping(value = "/approute", produces = "application/ld+json")
    @Operation(summary = "Creates a new app route")
    @ApiResponse(responseCode = "200", description = "Created a new app route")
    @ApiResponse(responseCode = "400", description = "Can not create an app route")
    ResponseEntity<String> createAppRoute(@RequestParam("description") String description);

    /**
     * Deletes the given app route.
     * @param routeId The ID of the route to be deleted.
     * @return The response message or an error.
     */
    @DeleteMapping(value = "/approute", produces = "application/ld+json")
    @Operation(summary = "Deletes the given app route")
    @ApiResponse(responseCode = "200", description = "Successfully deleted the app route")
    @ApiResponse(responseCode = "400", description = "Can not delete the app route")
    ResponseEntity<String> deleteAppRoute(@RequestParam("routeId") URI routeId);

    /**
     * Returns the given app route.
     * @param routeId The ID of the route the information is needed for.
     * @return The response message or an error.
     */
    @GetMapping(value = "/approute", produces = "application/ld+json")
    @Operation(summary = "Returns the given app route")
    @ApiResponse(responseCode = "200", description = "Successfully get the app route")
    @ApiResponse(responseCode = "400", description = "Can not find the app route")
    @ApiResponse(responseCode = "500", description = "Internal Server error")
    ResponseEntity<String> getAppRoute(@RequestParam("routeId") URI routeId);

    /**
     * This method creates a route step for an app route with the given parameters.
     *
     * @param routeId ID of the route.
     * @param startId ID of the start endpoint.
     * @param startCoordinateX X-coordinate of the start endpoint.
     * @param startCoordinateY Y-coordinate of the start endpoint.
     * @param endID ID of the last endpoint.
     * @param endCoordinateX X-coordinate of the last endpoint.
     * @param endCoordinateY Y-coordinate of the last endpoint.
     * @param resourceId ID of the resource.
     * @return The response message or an error.
     */
    @PostMapping(value = "/approute/step", produces = "application/ld+json")
    @Operation(summary = "Creates a new subroute for the app route")
    @ApiResponse(responseCode = "200",
            description = "Successfully created a new subroute for the app route")
    @ApiResponse(responseCode = "400", description = "Can not create the route step")
    ResponseEntity<String> createAppRouteStep(@RequestParam(value = "routeId") URI routeId,
                                              @RequestParam(value = "startId") URI startId,
                                              @RequestParam(value = "startCoordinateX")
                                                      int startCoordinateX,
                                              @RequestParam(value = "startCoordinateY")
                                                      int startCoordinateY,
                                              @RequestParam(value = "endId") URI endID,
                                              @RequestParam(value = "endCoordinateX")
                                                      int endCoordinateX,
                                              @RequestParam(value = "endCoordinateY")
                                                      int endCoordinateY,
                                              @RequestParam(value = "resourceId", required = false)
                                                      URI resourceId);

    /**
     * Returns for a route step the specific endpoint information.
     * @param routeId ID of the route.
     * @param endpointId ID of the endpoint.
     * @return The response message or an error.
     */
    @GetMapping(value = "/approute/step/endpoint/info", produces = "application/ld+json")
    @Operation(summary = "Returns for a route step the specific endpoint information")
    @ApiResponse(responseCode = "200", description = "Returned the endpoint information")
    @ApiResponse(responseCode = "400", description = "Can not find the endpoint information")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getEndpointInformation(@RequestParam(value = "routeId") URI routeId,
                                                  @RequestParam(value = "endpointId")
                                                          URI endpointId);

    /**
     * Updates the route deploy method for all routes.
     * @param routeDeployMethod The route deploy method to switch to.
     * @return The response message or an error.
     */
    @PutMapping(value = "/route/deploymethod", produces = "application/ld+json")
    @Operation(summary = "Updates the route deploy method for all routes")
    @ApiResponse(responseCode = "200", description = "Successfully updated the route deploy method")
    @ApiResponse(responseCode = "400", description = "Can not update the route deploy method")
    ResponseEntity<String> updateRouteDeployMethod(
            @RequestParam("deployMethod") RouteDeployMethod routeDeployMethod);

    /**
     * Returns the route deploy method for all routes.
     * @return The response message or an error.
     */
    @GetMapping(value = "/route/deploymethod", produces = "application/ld+json")
    @Operation(summary = "Returns the route deploy method for all routes")
    @ApiResponse(responseCode = "200",
            description = "Successfully returned the route deploy method")
    @ApiResponse(responseCode = "400", description = "Can not find the route deploy method")
    ResponseEntity<String> getRouteDeployMethod();

    /**
     * Returns all app routes.
     * @return The response message or an error.
     */
    @GetMapping(value = "/approutes", produces = "application/ld+json")
    @Operation(summary = "Returns all app routes")
    @ApiResponse(responseCode = "200", description = "Returns the list of the app routes")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getAppRoutes();

    /**
     * Save route related errors.
     * @param policy The policy.
     * @return The response message or an error.
     */
    @PostMapping(value = "/route/error", produces = "application/ld+json")
    @Operation(summary = "Save route related errors in the ConfigManager-backend")
    @ApiResponse(responseCode = "200", description = "Saved Route-Error in ConfigManager-backend.")
    ResponseEntity<String> setRouteError(@RequestBody String policy);

    /**
     * Get new route related errors.
     * @return The response message or an error.
     */
    @GetMapping(value = "/route/error", produces = "application/ld+json")
    @Operation(summary = "Get new route related errors")
    @ApiResponse(responseCode = "200", description = "Loaded and returned cached Route-Errors.")
    ResponseEntity<String> getRouteErrors();

    /**
     * Creates a generic endpoint (Backend-Connection).
     * @param accessURL The access-URL entered in the GUI.
     * @param sourceType The chosen source-Type.
     * @param username The username needed for the Backend-Connection.
     * @param password The password needed for the Backend-Connection.
     * @return The response message or an error.
     */
    @PostMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Created a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not create the generic endpoint")
    ResponseEntity<String> createGenericEndpoint(@RequestParam(value = "accessURL") URI accessURL,
                                                 @RequestParam(value = "sourceType")
                                                         String sourceType,
                                                 @RequestParam(value = "username", required = false)
                                                         String username,
                                                 @RequestParam(value = "password", required = false)
                                                         String password);

    /**
     * Returns a list of generic endpoints.
     * @return The response message or an error.
     */
    @GetMapping(value = "/generic/endpoints", produces = "application/ld+json")
    @Operation(summary = "Returns a list of generic endpoints")
    @ApiResponse(responseCode = "200", description = "Returned a list of generic endpoints")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getGenericEndpoints();

    /**
     * Deletes a generic endpoint.
     * @param endpointId The id of the endpoint to be deleted.
     * @return The response message or an error.
     */
    @DeleteMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Deletes a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Deleted a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not delete the generic endpoint")
    ResponseEntity<String> deleteGenericEndpoint(@RequestParam(value = "endpointId")
                                                         URI endpointId);

    /**
     * Updates a generic endpoint.
     * @param id The ID of the endpoint to be updated.
     * @param accessURL The access-URL entered in the GUI.
     * @param sourceType The chosen source-Type.
     * @param username The username needed for the Backend-Connection.
     * @param password The password needed for the Backend-Connection.
     * @return The response message or an error.
     */
    @PutMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Updates a generic endpoint")
    @ApiResponse(responseCode = "200", description = "Updated a generic endpoint")
    @ApiResponse(responseCode = "400", description = "Can not update the generic endpoint")
    ResponseEntity<String> updateGenericEndpoint(@RequestParam(value = "id") URI id,
                                                 @RequestParam(value = "accessURL",
                                                         required = false) URI accessURL,
                                                 @RequestParam(value = "sourceType",
                                                         required = false) String sourceType,
                                                 @RequestParam(value = "username",
                                                         required = false) String username,
                                                 @RequestParam(value = "password",
                                                         required = false) String password);

    /**
     * Creates a new connector endpoint for the connector.
     * @param accessUrl The access-URL entered in the GUI.
     * @param sourceType The chosen source-Type.
     * @return The response message or an error.
     */
    @PostMapping(value = "/connector/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a new connector endpoint for the connector")
    @ApiResponse(responseCode = "200",
            description = "Successfully created the connector endpoint for the connector")
    ResponseEntity<String> createConnectorEndpoint(@RequestParam("accessUrl") URI accessUrl,
                                                   @RequestParam(value = "sourceType",
                                                           required = false) String sourceType);
}