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
package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exceptions.MessageBuilderException;
import io.dataspaceconnector.exceptions.MessageEmptyException;
import io.dataspaceconnector.exceptions.MessageRequestException;
import io.dataspaceconnector.exceptions.RdfBuilderException;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.exceptions.VersionNotSupportedException;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.ids.messages.dto.ContractAgreementMessageDesc;
import io.dataspaceconnector.ids.messages.dto.ContractRejectionMessageDesc;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.messages.MessageResponseService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.messages.types.ContractRejectionService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import io.dataspaceconnector.services.usagecontrol.RuleValidator;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.PersistenceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This @{@link ContractRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@Log4j2
@SupportedMessageType(ContractRequestMessageImpl.class)
@RequiredArgsConstructor
public class ContractRequestHandler implements MessageHandler<ContractRequestMessageImpl> {

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * Service for ids contract rejection messages.
     */
    private final @NonNull ContractRejectionService rejectionService;

    /**
     * Service for ids contract agreement messages.
     */
    private final @NonNull ContractAgreementService agreementService;

    /**
     * Service for resolving elements and its parents/children.
     */
    private final @NonNull EntityDependencyResolver dependencyResolver;

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for validating rule content.
     */
    private final @NonNull RuleValidator ruleValidator;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The request message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final ContractRequestMessageImpl message,
                                         final MessagePayload payload) {
        // Validate incoming message.
        try {
            agreementService.validateIncomingMessage(message);
        } catch (MessageEmptyException exception) {
            return responseService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return responseService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        // Read message payload as string.
        String payloadAsString;
        try {
            payloadAsString = MessageUtils.getPayloadAsString(payload);
        } catch (MessageRequestException exception) {
            return responseService.handleMessagePayloadException(exception, messageId, issuer);
        }

        // Check the contract's content.
        return processContractRequest(payloadAsString, messageId, issuer);
    }

    /**
     * Checks if the contract request content by the consumer complies with the contract offer by
     * the provider.
     *
     * @param payload   The message payload containing a contract request.
     * @param messageId The message id of the incoming message.
     * @param issuer    The issuer connector extracted from the incoming message.
     * @return A message response to the requesting connector.
     */
    public MessageResponse processContractRequest(final String payload, final URI messageId,
                                                  final URI issuer) throws RuntimeException {
        try {
            // Deserialize string to contract object.
            final var request = deserializationService.getContractRequest(payload);

            // Get all rules of the contract request.
            final var rules = ContractUtils.extractRulesFromContract(request);
            if (rules.isEmpty()) {
                // Return rejection message if the contract request is missing rules.
                return responseService.handleMissingRules(request, messageId, issuer);
            }

            final var targetRuleMap = ContractUtils.getTargetRuleMap(rules);
            if (targetRuleMap.containsKey(null)) {
                // Return rejection message if the rules are missing targets.
                return responseService.handleMissingTargetInRules(request, messageId, issuer);
            }

            final var targetList = new ArrayList<URI>();
            // Retrieve matching contract offers to compare the content.
            for (final var target : targetRuleMap.keySet()) {
                final List<Contract> contracts;
                try {
                    contracts = dependencyResolver.getContractOffersByArtifactId(target);
                } catch (ResourceNotFoundException exception) {
                    return responseService.handleResourceNotFoundException(exception, target,
                            issuer, messageId);
                }

                // Abort negotiation if no contract offer could be found.
                if (contracts.isEmpty()) {
                    return responseService.handleMissingContractOffers(request, messageId, issuer);
                }

                // Abort negotiation if no contract offer for the issuer connector could be found.
                final var validContracts
                        = ContractUtils.removeContractsWithInvalidConsumer(contracts, issuer);
                if (validContracts.isEmpty()) {
                    return responseService.handleMissingContractOffers(request, messageId, issuer);
                }

                var valid = false;
                try {
                    valid = ruleValidator.validateRulesOfRequest(validContracts, targetRuleMap,
                            target);
                } catch (IllegalArgumentException e) {
                    return responseService.handleMalformedRules(e, payload, issuer, messageId);
                }

                if (!valid) {
                    return rejectContract(issuer, messageId);
                }
                targetList.add(target);
            }

            return acceptContract(request, issuer, messageId, targetList);
        } catch (IllegalArgumentException e) {
            return responseService.handleIllegalArgumentException(e, payload, issuer, messageId);
        } catch (Exception e) {
            // NOTE: Should not be reached.
            return responseService.handleMessageProcessingFailed(e, payload, issuer, messageId);
        }
    }

    /**
     * Accept contract by building a contract agreement and sending it as payload within a
     * contract agreement message.
     *
     * @param request   The contract request object from the data consumer.
     * @param issuer    The issuer connector id.
     * @param messageId The correlation message id.
     * @param targets   List of requested targets.
     * @return The message response to the requesting connector.
     */
    private MessageResponse acceptContract(final ContractRequest request, final URI issuer,
                                           final URI messageId, final List<URI> targets) {
        ContractAgreement agreement = null;
        URI agreementId;
        try {
            // Turn the accepted contract request into a contract agreement and persist it.
            agreement = persistenceService.buildAndSaveContractAgreement(request, targets, issuer);
            agreementId = agreement.getId();
        } catch (ConstraintViolationException | PersistenceException exception) {
            return responseService.handleAgreementPersistenceException(exception, agreement,
                    issuer, messageId);
        }

        try {
            // Build ids response message.
            final var desc = new ContractAgreementMessageDesc(issuer, messageId);
            final var header = agreementService.buildMessage(desc);
            if (log.isDebugEnabled()) {
                log.debug("Contract request accepted. [agreementId=({})]", agreementId);
            }

            // Send ids response message.
            return BodyResponse.create(header, agreement.toRdf());
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
                | RdfBuilderException e) {
            return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     *
     * @param issuer    The issuer connector.
     * @param messageId The correlation message id.
     * @return A contract rejection message.
     */
    private MessageResponse rejectContract(final URI issuer, final URI messageId) {
        try {
            // Build ids response message.
            final var desc = new ContractRejectionMessageDesc(issuer, messageId);
            final var header = (RejectionMessage) rejectionService.buildMessage(desc);

            // Send ids response message.
            return ErrorResponse.create(header, "Contract rejected.");
        } catch (MessageBuilderException | IllegalStateException | ConstraintViolationException
                | RdfBuilderException e) {
            return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }
}
