package io.dataspaceconnector.view;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.OfferedResourceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@SpringBootTest(classes = {OfferedResourceViewAssembler.class, ViewAssemblerHelper.class,
        OfferedResourceFactory.class})
public class OfferedResourceViewAssemblerTest {

    @Autowired
    private OfferedResourceViewAssembler offeredResourceViewAssembler;

    @Autowired
    private OfferedResourceFactory offeredResourceFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = offeredResourceViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = offeredResourceViewAssembler.getSelfLink(resourceId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + resourceId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> offeredResourceViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnOfferedResourceView() {
        /* ARRANGE */
        final var offeredResource = getOfferedResource();

        /* ACT */
        final var result = offeredResourceViewAssembler.toModel(offeredResource);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(offeredResource.getTitle(), result.getTitle());
        Assertions.assertEquals(offeredResource.getDescription(), result.getDescription());
        Assertions.assertEquals(offeredResource.getKeywords(), result.getKeywords());
        Assertions.assertEquals(offeredResource.getPublisher(), result.getPublisher());
        Assertions.assertEquals(offeredResource.getLanguage(), result.getLanguage());
        Assertions.assertEquals(offeredResource.getLicence(), result.getLicence());
        Assertions.assertEquals(offeredResource.getVersion(), result.getVersion());
        Assertions.assertEquals(offeredResource.getSovereign(), result.getSovereign());
        Assertions.assertEquals(offeredResource.getEndpointDocumentation(), result.getEndpointDocumentation());
        Assertions.assertEquals(offeredResource.getAdditional(), result.getAdditional());
        Assertions.assertEquals(offeredResource.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(offeredResource.getModificationDate(), result.getModificationDate());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getOfferedResourceLink(offeredResource.getId()), selfLink.get().getHref());

        final var contractsLink = result.getLink("contracts");
        assertTrue(contractsLink.isPresent());
        assertNotNull(contractsLink.get());
        assertEquals(getOfferedResourceContractsLink(offeredResource.getId()),
                contractsLink.get().getHref());

        final var representationsLink = result.getLink("representations");
        assertTrue(representationsLink.isPresent());
        assertNotNull(representationsLink.get());
        assertEquals(getOfferedResourceRepresentationsLink(offeredResource.getId()),
                representationsLink.get().getHref());

        final var catalogsLink = result.getLink("catalogs");
        assertTrue(catalogsLink.isPresent());
        assertNotNull(catalogsLink.get());
        assertEquals(getOfferedResourceCatalogsLink(offeredResource.getId()),
                catalogsLink.get().getHref());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private OfferedResource getOfferedResource() {
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("EN");
        desc.setTitle("title");
        desc.setDescription("description");
        desc.setKeywords(Collections.singletonList("keyword"));
        desc.setEndpointDocumentation(URI.create("https://endpointDocumentation.com"));
        desc.setLicence(URI.create("https://license.com"));
        desc.setPublisher(URI.create("https://publisher.com"));
        desc.setSovereign(URI.create("https://sovereign.com"));
        final var resource = offeredResourceFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);
        ReflectionTestUtils.setField(resource, "additional", additional);

        return resource;
    }

    private String getOfferedResourceLink(final UUID resourceId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + resourceId;
    }

    private String getOfferedResourceContractsLink(final UUID resourceId) {
        return WebMvcLinkBuilder.linkTo(methodOn(RelationControllers.OfferedResourcesToContracts.class)
                .getResource(resourceId, null, null, null)).toString();
    }

    private String getOfferedResourceRepresentationsLink(final UUID resourceId) {
        return linkTo(methodOn(RelationControllers.OfferedResourcesToRepresentations.class)
                .getResource(resourceId, null, null, null)).toString();
    }

    private String getOfferedResourceCatalogsLink(final UUID resourceId) {
        return linkTo(methodOn(RelationControllers.OfferedResourcesToCatalogs.class)
                .getResource(resourceId, null, null, null)).toString();
    }

}
