package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import de.fraunhofer.isst.ids.framework.messages.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import okhttp3.MultipartBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtifactRequestMessageService extends MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactRequestMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private final SerializerProvider serializerProvider;
    private final ResourceService resourceService;
    private URI recipient, artifactId, contractId;

    @Autowired
    public ArtifactRequestMessageService(ConfigurationContainer configurationContainer,
        TokenProvider tokenProvider, IDSHttpService idsHttpService, SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService) {
        super(idsHttpService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
        this.serializerProvider = serializerProvider;
        this.resourceService = requestedResourceService;
    }

    @Override
    public Message buildHeader() throws MessageBuilderException {
        return new ArtifactRequestMessageBuilder()
            ._issued_(Util.getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._requestedArtifact_(artifactId)
            ._securityToken_(tokenProvider.getTokenJWS())
            ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
            ._transferContract_(contractId)
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient, URI artifactId, URI contractId) {
        this.recipient = recipient;
        this.artifactId = artifactId;
        this.contractId = contractId;
    }

    /**
     * Saves the data string to the internal database.
     *
     * @param response   The data resource as string.
     * @param resourceId The resource uuid.
     * @throws java.lang.Exception if any.
     */
    public void saveData(String response, UUID resourceId) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        String header = map.get("header");
        String payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, ArtifactResponseMessage.class);
        } catch (Exception e) {
            throw new Exception("Rejection Message received: " + payload);
        }

        try {
            resourceService.addData(resourceId, payload);
        } catch (Exception e) {
            throw new Exception("Data could not be saved: " + e.getMessage());
        }
    }
}
