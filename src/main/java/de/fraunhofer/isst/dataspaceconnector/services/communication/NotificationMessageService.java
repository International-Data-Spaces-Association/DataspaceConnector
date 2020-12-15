package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService extends MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private URI recipient;

    @Autowired
    public NotificationMessageService(ConfigurationContainer configurationContainer,
        TokenProvider tokenProvider, IDSHttpService idsHttpService) {
        super(idsHttpService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Message buildHeader() throws MessageException {
        return new NotificationMessageBuilder()
            ._issued_(Util.getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getTokenJWS())
            ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient) {
        this.recipient = recipient;
    }
}
