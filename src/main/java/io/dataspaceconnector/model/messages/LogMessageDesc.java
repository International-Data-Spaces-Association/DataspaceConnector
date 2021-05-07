package io.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LogMessageDesc extends MessageDesc {
    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     */
    public LogMessageDesc(final URI recipient) {
        super(recipient);
    }
}
