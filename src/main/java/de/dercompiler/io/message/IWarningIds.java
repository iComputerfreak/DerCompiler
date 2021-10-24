package de.dercompiler.io.message;

/**
 * Defines the Interface for WarningIds, it is used to generate exit-codes and WarningIds based of the MessageOrigin
 */
public interface IWarningIds extends IErrorIds {
    /**
     * @return the id of the warning
     */
    @Override
    int getId();
}
