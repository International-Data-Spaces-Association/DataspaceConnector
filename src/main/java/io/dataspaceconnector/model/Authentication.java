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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The authentication is used for authorizing for example by the proxy or the data source.
 */
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class Authentication {
    /**
     * The primary key of the authentication.
     */
    @Id
    @GeneratedValue
    @JsonIgnore
    @ToString.Exclude
    @SuppressWarnings("PMD.ShortVariable")
    private Long id;

    /**
     * The username for the authentication.
     */
    private String username;

    /**
     * The password for the authentication.
     */
    private String password;
}