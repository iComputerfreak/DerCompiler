package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.selection.Datatype;

import java.util.Locale;

public enum OperationType {
    //Erst die binary Operations
    MOVSLQ("MOVSLQ", false),
    ADD("ADD", true),
    AND("AND", true),
    CMP("CMP"),
    DIV("IDIV", true),
    LEA("LEA", true),
    MOV("MOV", true),
    OR("OR", true),
    ROL("ROL", true),
    ROR("ROR", true),
    SHL("SHL", true),
    SAR("SAR", true),
    SAL("SAL", true),
    SHR("SHR", true),
    SHRS("SHRS"), // arithmetic ("signed") right shift
    SUB("SUB", true),
    XCHG("XCHG", true),
    XOR("XOR", true),
    LOAD(""),
    STORE(""),
    //Dann die unary Operations
    CALL("CALL"),
    DEC("DEC"),
    MUL("MUL"),
    JA("JA"),
    JAE("JAE"),
    JB("JB"),
    JBE("JBE"),
    JE("JE"),
    JG("JG"),
    JGE("JGE"),
    JL("JL"),
    JLE("JLE"),
    JMP("JMP"),
    JNE("JNE"),
    NEG("NEG", true),
    MOD("MOD", true),
    LBL("LBL"),
    NOT(""),
    POP("POP"),
    PUSH("PUSH"),
    INC("INC", true),
    //Dann die constant Operations
    NOP("NOP"),
    RET("RET"),
    CWTL("CWTL"),
    CLTQ("CLTQ"),
    CQTO("CQTO");

    private final String syntax;
    private final boolean appendDatatype;

    OperationType(String syntax){
        this.syntax = syntax.toLowerCase(); this.appendDatatype = false;
    }

    OperationType(String syntax, boolean appendDatatype){
        this.syntax = syntax.toLowerCase();
        this.appendDatatype = appendDatatype;
    }

    public String getSyntax(){
        return syntax;
    }

    public String getAtntSyntax(Datatype datatype){
        if (appendDatatype){
            return syntax +  datatype.toString().toLowerCase();
        }
        return syntax;
    }
}
