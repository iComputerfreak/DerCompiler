package de.dercompiler.lexer;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.ArrayList;
import java.util.List;

public class RingBuffer<T> {
    private List<T> elements;
    private int nextIndex;
    private int tailIndex;
    private int length;
    private int capacity;

    public RingBuffer(int capacity) {
        this.elements = new ArrayList<>(capacity);
        this.nextIndex = 0;
        this.tailIndex = 0;
        this.length = 0;
        this.capacity = capacity;
    }

    public T peek() {
        return this.peek(0);
    }

    public T peek(int lookAhead) {
        if (lookAhead >= this.capacity) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.BUFFER_TOO_SMALL, "Buffer too small; cannot look ahead %d items".formatted(lookAhead));
        } else if (this.length < (lookAhead - 1)) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.BUFFER_TOO_MUCH_LOOKAHEAD, "Buffer has not enough items; cannot look ahead %d positions".formatted(lookAhead));
        }

        return this.elements.get((this.nextIndex + lookAhead) % this.capacity);
    }

    public T pop() {
        if (this.length == 0) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.BUFFER_UNDERFLOW, "Buffer ran out of elements");
        }
        T res = this.elements.get(nextIndex);
        this.nextIndex = (this.nextIndex + 1) % this.capacity;
        this.length--;
        return res;
    }

    public void push(T element) {
        //must not overwrite
        if (this.length > 0 && this.nextIndex == this.tailIndex) {
            new OutputMessageHandler(MessageOrigin.LEXER, System.err).printErrorAndExit(LexerErrorIds.BUFFER_OVERFLOW, "Buffer overflow");
        }

        this.elements.add(this.tailIndex, element);

        this.tailIndex = (this.tailIndex + 1) % this.capacity;
        this.length++;
    }


}
