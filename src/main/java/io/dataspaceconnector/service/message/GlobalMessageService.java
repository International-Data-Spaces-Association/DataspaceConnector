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
package io.dataspaceconnector.service.message;

import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.common.DeserializeException;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.http.SendMessageException;
import de.fraunhofer.ids.messaging.protocol.http.ShaclValidatorException;
import de.fraunhofer.ids.messaging.protocol.multipart.UnknownResponseException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import de.fraunhofer.ids.messaging.requests.exceptions.NoTemplateProvidedException;
import de.fraunhofer.ids.messaging.requests.exceptions.RejectionException;
import de.fraunhofer.ids.messaging.requests.exceptions.UnexpectedPayloadException;
import io.dataspaceconnector.service.message.type.NotificationService;
import io.dataspaceconnector.util.ControllerUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * Service for sending ids messages.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class GlobalMessageService {

    /**
     * The service for communication with an ids broker.
     */
    private final @NotNull IDSBrokerService brokerSvc;

    /**
     * Service for sending notification messages.
     */
    private final @NonNull NotificationService notificationSvc;

    /**
     * Send connector update message.
     *
     * @param recipient The recipient.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendConnectorUpdateMessage(final URI recipient)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.updateSelfDescriptionAtBroker(recipient));
    }

    /**
     * Send connector update message and validate received response.
     *
     * @param recipient The recipient.
     * @return True if the message was successfully processed by the recipient, false if not.
     */
    public boolean sendAndValidateConnectorUpdateMessage(final URI recipient) {
        try {
            final var response = sendConnectorUpdateMessage(recipient);
            if (response.isPresent()) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Successfully registered connector. [url=(%s)]",
                            recipient));
                }
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Send connector unavailable message.
     *
     * @param recipient The recipient.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendConnectorUnavailableMessage(final URI recipient)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.unregisterAtBroker(recipient));
    }

    /**
     * Send resource update message.
     *
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendResourceUpdateMessage(final URI recipient,
                                                                   final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.updateResourceAtBroker(recipient, resource));
    }

    /**
     * Send resource update message and validate received response.
     *
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return True if the message was successfully processed by the recipient, false if not.
     */
    public boolean sendAndValidateResourceUpdateMessage(final URI recipient,
                                                        final Resource resource) {
        try {
            final var response = sendResourceUpdateMessage(recipient, resource);
            if (response.isPresent()) {
                if (log.isInfoEnabled()) {
                    log.info(String.format("Successfully registered resource. [resourceId=(%s), "
                            + "url=(%s)]", resource.getId(), recipient));
                }
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * Send resource unavailable message.
     *
     * @param recipient The recipient.
     * @param resource  The ids resource that should be updated.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendResourceUnavailableMessage(final URI recipient,
                                                                        final Resource resource)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.removeResourceFromBroker(recipient, resource));
    }

    /**
     * Send query message and validate received response.
     *
     * @param recipient The recipient.
     * @param query     The query statement.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendQueryMessage(final URI recipient, final String query)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.queryBroker(recipient, query, QueryLanguage.SPARQL,
                QueryScope.ALL, QueryTarget.BROKER));
    }

    /**
     * Send query message and validate received response.
     *
     * @param recipient The recipient.
     * @param term      The search term.
     * @param limit     The limit value.
     * @param offset    The offset value.
     * @return Optional of message container providing the received ids response.
     */
    public Optional<MessageContainer<?>> sendFullTextSearchMessage(
            final URI recipient, final String term, final int limit, final int offset)
            throws MultipartParseException, ClaimsException, DapsTokenManagerException, IOException,
            NoTemplateProvidedException, ShaclValidatorException, SendMessageException,
            UnexpectedPayloadException, SerializeException, DeserializeException,
            RejectionException, UnknownResponseException {
        return Optional.of(brokerSvc.fullTextSearchBroker(recipient, term, QueryScope.ALL,
                QueryTarget.BROKER, limit, offset));
    }

    /**
     * Validates response. Returns response entity with status code 200 if a
     * MessageProcessedNotificationMessage has been received, responds with the message's content
     * if not.
     *
     * @param response The response container.
     * @param msgType  Expected message type.
     * @return ResponseEntity with status code.
     */
    public ResponseEntity<Object> validateResponse(final Optional<MessageContainer<?>> response,
                                                   final Class<?> msgType) {
        if (response.isEmpty()) {
            return ControllerUtils.respondReceivedInvalidResponse();
        }

        final var header = response.get().getUnderlyingMessage();
        final var payload = response.get().getReceivedPayload();
        if (header.getClass().equals(msgType)) {
            return new ResponseEntity<>(payload, HttpStatus.OK);
        }

        // If response message is not of predefined type.
        final var content = notificationSvc.getResponseContent(header, payload);
        return ControllerUtils.respondWithContent(content);
    }
}
