package de.fraunhofer.isst.dataspaceconnector.controller.messages;

import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnexpectedResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
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

import java.net.URI;
import java.util.Map;

/**
 * Controller for sending description request messages.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ids")
@Tag(name = "IDS Messages", description = "Endpoints for invoke sending IDS messages")
public class DescriptionRequestMessageController {

    /**
     * Service for message handling;
     */
    private final @NonNull MessageService messageService;

    /**
     * Requests metadata from an external connector by building an DescriptionRequestMessage.
     *
     * @param recipient The target connector url.
     * @param elementId The requested element id.
     * @return The response entity.
     */
    @PostMapping("/description")
    @Operation(summary = "Send ids description request message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "417", description = "Expectation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PreAuthorize("hasPermission(#recipient, 'rw')")
    @ResponseBody
    public ResponseEntity<Object> sendDescriptionRequestMessage(
            @Parameter(description = "The recipient url.", required = true)
            @RequestParam("recipient") final URI recipient,
            @Parameter(description = "The id of the requested resource.")
            @RequestParam(value = "elementId", required = false) final URI elementId) {
        Map<String, String> response = null;
        String payload = null;
        try {
            // Send and validate description request/response message.
            response = messageService.sendDescriptionRequestMessage(recipient, elementId);
            messageService.validateDescriptionResponseMessage(response);

            // Read and process the response message.
            payload = MessageUtils.extractPayloadFromMultipartMessage(response);
            if (!Utils.isEmptyOrNull(elementId)) {
                return new ResponseEntity<>(payload, HttpStatus.OK);
            } else {
                // Get payload as component.
                final var component = messageService.getComponentFromPayload(payload);
                return new ResponseEntity<>(component, HttpStatus.OK);
            }
        } catch (MessageException exception) {
            return ControllerUtils.respondIdsMessageFailed(exception);
        } catch (MessageResponseException exception) {
            return ControllerUtils.respondReceivedInvalidResponse(exception);
        } catch (UnexpectedResponseType exception) {
            // If the response is not a description response message, show the response.
            return messageService.returnResponseMessageContent(response);
        } catch (IllegalArgumentException exception) {
            // If the response is not of type resource or base connector.
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception exception) {
            return ControllerUtils.respondGlobalException(exception);
        }
    }
}