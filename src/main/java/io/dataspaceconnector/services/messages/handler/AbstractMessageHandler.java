package io.dataspaceconnector.services.messages.handler;

import java.util.Objects;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.messages.handler.camel.dto.Request;
import io.dataspaceconnector.services.messages.handler.camel.dto.Response;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMessageHandler<T extends Message> implements MessageHandler<T> {

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * This message implements the logic that is needed to handle the message. As it returns the
     * input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The request message.
     * @param payload The message payload.
     * @return The response message.
     * @throws RuntimeException If the response body failed to be build.
     */
    public MessageResponse handleMessage(final T message,
                                         final MessagePayload payload) throws RuntimeException {
        final var result = template.send(getHandlerRouteDirect(),
                ExchangeBuilder.anExchange(context)
                        .withBody(new Request(message, payload))
                        .build());

        final var response = result.getIn().getBody(Response.class);
        if (response != null) {
            return BodyResponse.create(response.getHeader(), response.getBody());
        } else {
            final var errorResponse = result.getIn().getBody(ErrorResponse.class);
            return Objects.requireNonNullElseGet(errorResponse,
                    () -> ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                            "Could not process request.",
                            connectorService.getConnectorId(),
                            connectorService.getOutboundModelVersion()));
        }
    }

    /**
     * Returns the direct-component-reference to this handler's Camel route.
     *
     * @return the route reference.
     */
    protected abstract String getHandlerRouteDirect();

}
