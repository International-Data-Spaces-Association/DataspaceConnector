package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.daps.ClaimsException;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Abstract class for building, sending, and processing ids messages.
 */
@Service
public abstract class AbstractMessageService<D extends MessageDesc> {
    /**
     * The logging service.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageService.class);

    /**
     * Service for ids communication.
     */
    @Autowired
    private IDSHttpService idsHttpService;

    /**
     * Service for the current connector configuration.
     */
    @Autowired
    private IdsConnectorService connectorService;

    /**
     * Service for ids deserialization.
     */
    @Autowired
    private DeserializationService deserializationService;

    /**
     * Build ids message with params.
     *
     * @param recipient The message recipient.
     * @param desc      Type-specific message parameter.
     * @return An ids message.
     * @throws ConstraintViolationException If the ids message could not be built.
     */
    public abstract Message buildMessage(URI recipient, D desc) throws ConstraintViolationException;

    /**
     * Build and sent a multipart message with header and payload.
     *
     * @param recipient The message's recipient.
     * @param desc      Type-specific message parameter.
     * @param payload   The message's payload.
     * @return The response as map.
     * @throws MessageException If message building, sending, or processing failed.
     */
    public Map<String, String> sendMessage(final URI recipient, final D desc, final String payload)
            throws MessageException {
        try {
            final var header = buildMessage(recipient, desc);
            final var body = MessageUtils.buildIdsMultipartMessage(header, payload);
            LOGGER.info(String.valueOf(body));

            // Send message and check response.
            return idsHttpService.sendAndCheckDat(body, recipient);
        } catch (ConstraintViolationException exception) {
            LOGGER.warn("Ids message header could not be built. [exception=({})]", exception.getMessage());
            throw new MessageBuilderException("Ids message header could not be built.", exception);
        } catch (ClaimsException exception) {
            LOGGER.debug("Invalid DAT in incoming message. [exception=({})]",
                    exception.getMessage());
            throw new MessageResponseException("Invalid DAT in incoming message.", exception);
        } catch (FileUploadException | IOException exception) {
            LOGGER.warn("Message could not be sent. [exception=({})]", exception.getMessage());
            throw new MessageNotSentException("Message could not be sent.", exception);
        }
    }

    /**
     * Getter for ids connector service.
     *
     * @return The service class.
     */
    public IdsConnectorService getConnectorService() {
        return connectorService;
    }

    /**
     * Getter for ids deserialization service.
     *
     * @return The service class.
     */
    public DeserializationService getDeserializer() {
        return deserializationService;
    }
}