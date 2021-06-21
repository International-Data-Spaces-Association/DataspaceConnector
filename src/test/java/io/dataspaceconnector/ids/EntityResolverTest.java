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
package io.dataspaceconnector.ids;

import java.net.URI;
import java.util.UUID;

import io.dataspaceconnector.ids.BlockingArtifactReceiver;
import io.dataspaceconnector.ids.EntityResolver;
import io.dataspaceconnector.resources.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.model.core.Agreement;
import io.dataspaceconnector.model.core.Artifact;
import io.dataspaceconnector.model.core.ArtifactImpl;
import io.dataspaceconnector.model.core.Catalog;
import io.dataspaceconnector.model.core.Contract;
import io.dataspaceconnector.model.core.ContractRule;
import io.dataspaceconnector.model.core.OfferedResource;
import io.dataspaceconnector.model.core.OfferedResourceDesc;
import io.dataspaceconnector.model.core.Representation;
import io.dataspaceconnector.ids.builder.core.base.DeserializationService;
import io.dataspaceconnector.ids.builder.IdsArtifactBuilder;
import io.dataspaceconnector.ids.builder.IdsCatalogBuilder;
import io.dataspaceconnector.ids.builder.IdsContractBuilder;
import io.dataspaceconnector.ids.builder.IdsRepresentationBuilder;
import io.dataspaceconnector.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.resources.AgreementService;
import io.dataspaceconnector.resources.ArtifactService;
import io.dataspaceconnector.resources.CatalogService;
import io.dataspaceconnector.resources.ContractService;
import io.dataspaceconnector.resources.RepresentationService;
import io.dataspaceconnector.resources.ResourceService;
import io.dataspaceconnector.resources.RuleService;
import io.dataspaceconnector.model.core.AllowAccessVerifier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = { EntityResolver.class})
public class EntityResolverTest {

    @MockBean
    private ArtifactService artifactService;

    @MockBean
    private RepresentationService representationService;

    @MockBean
    private ResourceService<OfferedResource, OfferedResourceDesc> offerService;

    @MockBean
    private  CatalogService catalogService;

    @MockBean
    private  ContractService contractService;

    @MockBean
    private  RuleService ruleService;

    @MockBean
    private  AgreementService agreementService;

    @MockBean
    private IdsCatalogBuilder catalogBuilder;

    @MockBean
    private IdsResourceBuilder<OfferedResource> offerBuilder;

    @MockBean
    private IdsArtifactBuilder artifactBuilder;

    @MockBean
    private IdsRepresentationBuilder representationBuilder;

    @MockBean
    private IdsContractBuilder contractBuilder;

    @MockBean
    private AllowAccessVerifier allowAccessVerifier;

    @MockBean
    private BlockingArtifactReceiver artifactReceiver;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private EntityResolver resolver;

    private UUID resourceId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

    @Test
    public void getEntityById_null_throwsResourceNotFoundException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> resolver.getEntityById(null));
    }

    @Test
    public void getEntityById_validArtifact_returnArtifact() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/artifacts/" + resourceId);
        final var resource = getArtifact();
        Mockito.doReturn(resource).when(artifactService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validRepresentation_returnRepresentation() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/representations/" + resourceId);
        final var resource = getRepresentation();
        Mockito.doReturn(resource).when(representationService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validOfferedResource_returnOfferedResource() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/offers/" + resourceId);
        final var resource = getOfferedResource();
        Mockito.doReturn(resource).when(offerService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validCatalog_returnCatalog() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/catalogs/" + resourceId);
        final var resource = getCatalog();
        Mockito.doReturn(resource).when(catalogService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validContract_returnContract() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/contracts/" + resourceId);
        final var resource = getContract();
        Mockito.doReturn(resource).when(contractService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validRule_returnRule() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/rules/" + resourceId);
        final var resource = getRule();
        Mockito.doReturn(resource).when(ruleService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_validAgreement_returnAgreement() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/agreements/" + resourceId);
        final var resource = getAgreement();
        Mockito.doReturn(resource).when(agreementService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertEquals(resource, result);
    }

    @Test
    public void getEntityById_malformedAgreement_() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/someWhereIdontKnow/" + resourceId);

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> resolver.getEntityById(resourceUri));
    }

    /**
     * Utilities
     */

    private Artifact getArtifact() {
        final var output = new ArtifactImpl();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    private Representation getRepresentation() {
        final var output = new Representation();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private OfferedResource getOfferedResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Catalog getCatalog() {
        final var constructor = Catalog.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Contract getContract() {
        final var constructor = Contract.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private ContractRule getRule() {
        final var constructor = ContractRule.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Agreement getAgreement() {
        final var constructor = Agreement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }
}