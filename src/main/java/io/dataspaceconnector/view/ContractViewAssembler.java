package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers.ContractController;
import io.dataspaceconnector.exceptions.UnreachableLineException;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.utils.ErrorMessages;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a contracts.
 */
@Component
@NoArgsConstructor
public class ContractViewAssembler
        implements RepresentationModelAssembler<Contract, ContractView>, SelfLinking {
    /**
     * Construct the ContractView from a Contract.
     *
     * @param contract The contract.
     * @return The new view.
     */
    @Override
    public ContractView toModel(final Contract contract) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(contract, ContractView.class);
        view.add(getSelfLink(contract.getId()));

        final var rulesLink = WebMvcLinkBuilder
                .linkTo(methodOn(RelationControllers.ContractsToRules.class)
                .getResource(contract.getId(), null, null, null))
                .withRel("rules");
        view.add(rulesLink);

        final var resourceType = contract.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker = linkTo(methodOn(RelationControllers.ContractsToOfferedResources.class)
                    .getResource(contract.getId(), null, null, null))
                    .withRel("offers");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.ContractsToOfferedResources.class)
                                .getResource(contract.getId(), null, null, null))
                                .withRel("offers");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.ContractsToRequestedResources.class)
                                .getResource(contract.getId(), null, null, null))
                                .withRel("requests");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, ContractController.class);
    }
}
