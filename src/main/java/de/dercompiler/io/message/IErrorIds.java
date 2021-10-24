package de.dercompiler.io.message;

/**
 * Defines the interface for ErrorIds, it is used to generate exit codes and ErrorIds based of the message origin
 */
public interface IErrorIds {

    /**
     * @return The id of the Error
     */
    int getId();
}
