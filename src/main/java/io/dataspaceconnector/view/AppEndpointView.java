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
import io.dataspaceconnector.model.AppEndpointType;
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
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime modificationDate;

    /**
     * The documentation for the endpoint.
     */
    private URI endpointDocumentation;

    /**
     * The information for the endpoint.
     */
    private String endpointInformation;

    /**
     * The inbound path.
     */
    private String inboundPath;

    /**
     * The outbound path.
     */
    private String outboundPath;

    /**
     * The access url of the endpoint.
     */
    private URI accessURL;

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