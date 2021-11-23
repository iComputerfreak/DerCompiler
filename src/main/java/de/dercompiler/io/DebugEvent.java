package de.dercompiler.io;

import de.dercompiler.io.message.IErrorIds;
import de.dercompiler.io.message.MessageOrigin;

public class DebugEvent {

    private final MessageOrigin origin;
    private final IErrorIds errorId;
    private final String message;

    public DebugEvent(MessageOrigin origin, IErrorIds ids, String message) {
        this.origin = origin;
        this.errorId = ids;
        this.message = message;
    }

    public MessageOrigin getOrigin() {
        return origin;
    }

    public IErrorIds getErrorId() {
        return errorId;
    }

    public String getMessage() {
        return message;
    }
}
