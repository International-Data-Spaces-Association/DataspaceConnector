package io.dataspaceconnector.view;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.model.Catalog;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import io.dataspaceconnector.controller.resources.ResourceControllers.CatalogController;
import lombok.NoArgsConstructor;

/**
 * Assembles the REST resource for a catalog.
 */
@Component
@NoArgsConstructor
public class CatalogViewAssembler
        implements RepresentationModelAssembler<Catalog, CatalogView>, SelfLinking {
    /**
     * Construct the CatalogView from a Catalog.
     * @param catalog The catalog.
     * @return The new view.
     */
    @Override
    public CatalogView toModel(final Catalog catalog) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(catalog, CatalogView.class);
        view.add(getSelfLink(catalog.getId()));

        final var offeredResLink = WebMvcLinkBuilder
                .linkTo(methodOn(RelationControllers.CatalogsToOfferedResources.class)
                .getResource(catalog.getId(), null, null, null))
                .withRel("offers");
        view.add(offeredResLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, CatalogController.class);
    }
}
