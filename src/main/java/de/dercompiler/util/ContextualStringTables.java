package de.dercompiler.util;

import de.dercompiler.semantic.StringTable;
import de.dercompiler.semantic.Symbol;

public class ContextualStringTables {

    private StringTable programStringTable;
    private StringTable classStringTable;
    private StringTable methodStringTable;

    public ContextualStringTables(StringTable programStringTable, StringTable classStringTable, StringTable methodStringTable) {
        this.programStringTable = programStringTable;
        this.classStringTable = classStringTable;
        this.methodStringTable = methodStringTable;
    }

    public StringTable getProgramStringTable() {
        return programStringTable;
    }

    public void setProgramStringTable(StringTable programStringTable) {
        this.programStringTable = programStringTable;
    }

    public StringTable getClassStringTable() {
        return classStringTable;
    }

    public void setClassStringTable(StringTable classStringTable) {
        this.classStringTable = classStringTable;
    }

    public StringTable getMethodStringTable() {
        return methodStringTable;
    }

    public void setMethodStringTable(StringTable methodStringTable) {
        this.methodStringTable = methodStringTable;
    }

    public Symbol findOrInsertClass(String name) {
        de.dercompiler.semantic.Symbol result = null;

        if (methodStringTable != null && methodStringTable.containsClass(name)) {
            return methodStringTable.findOrInsertClass(name);
        }

        if (classStringTable != null && classStringTable.containsClass(name)) {
            return classStringTable.findOrInsertClass(name);
        }

        if (programStringTable != null && programStringTable.containsClass(name)) {
            return programStringTable.findOrInsertClass(name);
        }

        return new Symbol(name, null, null);
    }

    public Symbol findOrInsertMethod(String name) {
        Symbol result = null;

        if (methodStringTable != null && methodStringTable.containsMethod(name)) {
            return methodStringTable.findOrInsertClass(name);
        }

        if (classStringTable != null && classStringTable.containsMethod(name)) {
            return classStringTable.findOrInsertClass(name);
        }

        if (programStringTable != null && programStringTable.containsMethod(name)) {
            return programStringTable.findOrInsertClass(name);
        }

        return new Symbol(name, null, null);
    }

    public Symbol findOrInsertVariable(String name) {
        Symbol result = null;

        if (methodStringTable != null && methodStringTable.containsVariable(name)) {
            return methodStringTable.findOrInsertClass(name);
        }

        if (classStringTable != null && classStringTable.containsVariable(name)) {
            return classStringTable.findOrInsertClass(name);
        }

        if (programStringTable != null && programStringTable.containsVariable(name)) {
            return programStringTable.findOrInsertClass(name);
        }

        return new Symbol(name, null, null);
    }
}
