package de.dercompiler.util;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerErrorIds;

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
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(LexerErrorIds.BUFFER_TOO_SMALL, "Buffer too small; cannot look ahead %d items".formatted(lookAhead));
        } else if (this.length < (lookAhead - 1)) {
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(LexerErrorIds.BUFFER_TOO_MUCH_LOOKAHEAD, "Buffer has not enough items; cannot look ahead %d positions".formatted(lookAhead));
        }

        return this.elements.get((this.nextIndex + lookAhead) % this.capacity);
    }

    public T pop() {
        if (this.isEmpty()) {
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(LexerErrorIds.BUFFER_UNDERFLOW, "Buffer ran out of elements");
        }
        T res = this.elements.get(nextIndex);
        // array entry does not need to be "deleted", it will probably be overwritten anyway.

        this.nextIndex = (this.nextIndex + 1) % this.capacity;
        this.length--;
        return res;
    }

    public void push(T element) {
        //must not overwrite
        if (this.isFull()) {
            new OutputMessageHandler(MessageOrigin.LEXER).printErrorAndExit(LexerErrorIds.BUFFER_OVERFLOW, "Buffer overflow");
        }

        if (this.elements.size() <= tailIndex) {
            // sets element, shifting all following elements to the right
            this.elements.add(this.tailIndex, element);
        } else {
            // replaces element at position that has been set before
            this.elements.set(this.tailIndex, element);
        }

        this.tailIndex = (this.tailIndex + 1) % this.capacity;
        this.length++;
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public boolean isFull() {
        // equivalent: !this.isEmpty() && this.nextIndex == this.tailIndex
        return this.length == this.capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getLength() {
        return length;
    }

    public void clear() {
        this.length = 0;
        this.nextIndex = this.tailIndex;
    }
}
