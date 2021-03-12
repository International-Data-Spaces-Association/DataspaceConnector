package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.Basepaths;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

@Data
@Embeddable
public class EndpointId implements Serializable {
    
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String basePath;

    @JsonIgnore
    private UUID resourceId;

    public EndpointId() {
    }

    public EndpointId(final String basePath, final UUID resourceId) {
        this.basePath = basePath;
        this.resourceId = resourceId;
    }

    public EndpointId(final Basepaths basepaths, final UUID resourceId) {
        this.basePath = basepaths.toString();
        this.resourceId = resourceId;
    }

    @JsonProperty("href")
    public URI toUri() {
        return URI.create(basePath + "/" + resourceId.toString());
    }

    @JsonProperty("href")
    public void setUri(final URI uri) {
        final var extractedId = EndpointUtils.getEndpointIdFromPath(uri);

        this.basePath = extractedId.getBasePath();
        this.resourceId = extractedId.getResourceId();
    }
}