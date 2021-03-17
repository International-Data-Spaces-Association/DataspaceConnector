package de.fraunhofer.isst.dataspaceconnector.controller.ids;

import de.fraunhofer.isst.dataspaceconnector.services.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for sending ids connector update messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class ConnectorUpdateMessageController {

    /**
     * The service for communication with the ids broker.
     */
    private final @NonNull IDSBrokerService brokerService;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Sending an ids connector update message with the current connector as payload.
     *
     * @param recipient The url of the recipient.
     * @return The response message or an error.
     */
    @PostMapping("/connector/update")
    @Operation(summary = "Connector update message", description = "Can be used for registering or "
            + "updating the connector at an IDS broker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    public ResponseEntity<Object> sendConnectorUpdateMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final String recipient) {
        try {
            // Update the config model.
            connectorService.updateConfigModel();

            // Send the connector update message.
            final var response = brokerService.updateSelfDescriptionAtBroker(recipient);
            final var responseToString = Objects.requireNonNull(response.body()).string();
            return new ResponseEntity<>(responseToString, HttpStatus.OK);
        } catch (ConfigurationUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (NullPointerException | IOException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        }
    }
}