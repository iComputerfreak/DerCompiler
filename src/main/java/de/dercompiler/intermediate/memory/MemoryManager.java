package de.dercompiler.intermediate.memory;

import de.dercompiler.intermediate.Operand;
import firm.Entity;

/**
 * This interface describes an object that acts as a layer between the code selection/generation and the actual memory.
 * Thus, it encapsulates a specific strategy so as to where any data needed may be found.
 */
public interface MemoryManager {

    /*
       What access/data is presumably needed:
         - an outlet for MOV, CALL, and LEAVE instructions
         - a stack of data objects, each of which contains
            - the hashmap of variable id -> Operand (location)
            - the current method entity object (so that the correct number of arguments can be popped of the stack)
         ...

     */

    /**
     * Returns the memory location of the n-th argument of the current method.
     * @param n the index of the argument
     * @return the location of the n-th argument
     */
    Operand getArgument(int n);

    /**
     * Returns the memory location of the local variable given by its id.
     * @param id the id of the local variable needed
     * @return the location of the local variable
     */
    Operand getLocalVariable(int id);

    /**
     * Registers a new local variable.
     * @return The id of the new variable
     */
    int putLocalVariable(Operand source);

    /**
     * Returns the target location of the return value.
     * @return the target location of the return value
     */
    Operand getReturnValue();

    /**
     * Sets up a new activation record on top of the stack given the method entity that is called.
     * @param methodEntity the called method
     * @param target the stack position or register where the return value shall be stored after leaving the method
     * @param arguments the location of the method arguments
     */
    void enterMethod(Entity methodEntity, Operand target, Operand... arguments);
    /*
       Save current registers
            (can we determine "obsolete" registers? Or simply save 'all' registers, whatever that might be supposed to mean?)
       Push arguments to the stack or in registers
       Set up return value
            (what if it is a void function? Save the space or stick to the general pattern and have an unnecessary stack entry?)
       Set up dynamic link
        -- CALL --  Set up return address
       Without actually modifying the stack, set up local variables
            (in hash map by their id?)
    */

    /**
     * Removes the current activation record from the stack, leaving behind a well-defined state.
     */
    void leaveMethod(Operand returnValue);
    /*
        Put return value in designated location
            (return value spot in callee AR / target spot in caller AR / register)
        Pop local variables from stack: sp <- bp
            (or leave them in registers)
        -- LEAVE -- Set instruction pointer

     */
}
