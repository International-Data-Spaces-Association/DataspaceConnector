package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.RequestedResource;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link RequestedResource}.
 */
@Repository
public interface RequestedResourcesRepository extends RemoteEntityRepository<RequestedResource> {
}
