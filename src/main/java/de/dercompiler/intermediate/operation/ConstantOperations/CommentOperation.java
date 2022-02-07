package de.dercompiler.intermediate.operation.ConstantOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.OperationType;

import java.util.StringJoiner;

public class CommentOperation extends ConstantOperation {

    private String comment;
    private final String NEWLINE = "\n";
    private final String COMMENT_INTEL = "#";
    private final String COMMENT_Atnt_START = "/*";
    private final String COMMENT_Atnt       = "* ";
    private final String COMMENT_Atnt_END   = "*/";

    public CommentOperation(String comment) {
        super(OperationType.COMMENT, false);
        this.comment = comment;
    }

    @Override
    public Operand[] getArgs() {
        return new Operand[0];
    }

    @Override
    public String getIntelSyntax() {
        return COMMENT_INTEL + comment.replace(NEWLINE, NEWLINE + COMMENT_INTEL);
    }

    @Override
    public String getAtntSyntax() {
        StringJoiner joiner = new StringJoiner("\n"+COMMENT_Atnt, COMMENT_Atnt_START, COMMENT_Atnt_END);
        for (String line : comment.split("\n")) joiner.add(line);
        return joiner.toString();

    }
}
