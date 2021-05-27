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

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 * The configuration describes the configuration of a connector.
 */
@Entity
@Table(name = "configuration")
@SQLDelete(sql = "UPDATE configuration SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Configuration extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The log level.
     */
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    /**
     * The status of the connector.
     */
    @Enumerated(EnumType.STRING)
    private ConnectorStatus connectorStatus;

    /**
     * The deploy mode of the connector.
     */
    @Enumerated(EnumType.STRING)
    private ConnectorDeployMode deployMode;

    /**
     * The proxy configuration.
     */
    @OneToMany
    private List<Proxy> proxy;

    /**
     * The trust store.
     */
    private String trustStore;

    /**
     * The password of the trust store.
     */
    private String trustStorePassword;

    /**
     * The key store.
     */
    private String keyStore;

    /**
     * The key store password.
     */
    private String keyStorePassword;
}