package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public interface Operand {

    String getIdentifier();

    String getIdentifier(Datatype datatype);
}
