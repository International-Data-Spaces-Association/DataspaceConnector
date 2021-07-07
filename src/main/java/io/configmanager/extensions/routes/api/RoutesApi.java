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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public interface RoutesApi {
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
}
