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

import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.model.resources.OfferedResource;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class BrokerDescTest {

    @Test
    public void equals_verify() {
        final var offeredResource = new OfferedResource();
        offeredResource.setTitle("First Resource");

        final var secondOfferedResource = new OfferedResource();
        secondOfferedResource.setTitle("Second Resource");

        EqualsVerifier.simple().forClass(BrokerDesc.class)
                .withPrefabValues(OfferedResource.class, offeredResource, secondOfferedResource)
                .verify();
    }
}