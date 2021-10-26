package de.dercompiler.actions;

/**
 * Represents an action that the compiles can do
 */
public abstract class Action {

    /**
     * Executes the action
     */
    public abstract void run();

    /**
     * Shows help regarding this specific action
     */
    public abstract void help();

    /**
     * The ID of this action
     * @return The ID of this action
     */
    public abstract String actionId();
}
