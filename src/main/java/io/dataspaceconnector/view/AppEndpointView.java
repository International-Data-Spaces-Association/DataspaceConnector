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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.model.endpoint.AppEndpointType;
import io.dataspaceconnector.model.endpoint.EndpointType;
import io.dataspaceconnector.view.util.ViewConstants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 * A DTO for controlled exposing of app information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "endpoints", itemRelation = "endpoint")
public class AppEndpointView extends RepresentationModel<AppEndpointView> {

    /**
     * The endpoint type.
     */
    private final EndpointType type = EndpointType.APP;

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime modificationDate;

    /**
     * The location information.
     */
    private URI location;

    /**
     * The documentation of the endpoint.
     */
    private URI docs;

    /**
     * The information of the endpoint.
     */
    private String info;

    /**
     * The file name extension of the data.
     */
    private String mediaType;

    /**
     * The port number of the app endpoint.
     */
    private int appEndpointPort;

    /**
     * The protocol of the app endpoint.
     */
    private String appEndpointProtocol;

    /**
     * The used language.
     */
    private String language;

    /**
     * The type of the app endpoint.
     */
    private AppEndpointType appEndpointType;
}
