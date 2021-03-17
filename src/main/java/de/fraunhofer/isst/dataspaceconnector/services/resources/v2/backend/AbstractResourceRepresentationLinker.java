package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

public abstract class AbstractResourceRepresentationLinker<T extends Resource>
        extends BaseUniDirectionalLinkerService<T, Representation, ResourceService<T, ?>,
                RepresentationService> {
    protected AbstractResourceRepresentationLinker() {
        super();
    }

    @Override
    protected List<Representation> getInternal(final Resource owner) {
        return owner.getRepresentations();
    }
}

@Service
final class OfferedResourceRepresentation
        extends AbstractResourceRepresentationLinker<OfferedResource> {
    public OfferedResourceRepresentation() {
        super();
    }
}

@Service
final class RequestedResourceRepresentation
        extends AbstractResourceRepresentationLinker<RequestedResource> {
    public RequestedResourceRepresentation() {
        super();
    }
}