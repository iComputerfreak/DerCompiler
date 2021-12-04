package de.dercompiler.ast.statement;

import de.dercompiler.ast.ASTDefinition;
import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.Variable;
import de.dercompiler.ast.visitor.ASTNodeVisitor;
import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class LocalVariableDeclarationStatement extends Statement implements ASTDefinition {

    Variable internalVar;
    Type type;
    Expression valueExpression;
    private de.dercompiler.semantic.type.Type refType;
    int nodeId;
    
    private firm.Type firmType;

    public LocalVariableDeclarationStatement(SourcePosition position, Type type, String identifier, Expression valueExpression) {
        super(position);
        internalVar = new Variable(position, identifier);
        internalVar.setDefinition(this);
        this.type = type;
        this.valueExpression = valueExpression;
        nodeId = -1;
    }

    public Type getType() {
        return type;
    }

    public de.dercompiler.semantic.type.Type getRefType() {
        return this.refType;
    }
    
    public void setFirmType(firm.Type firmType) {
        this.firmType = firmType;
    }
    
    @Override
    public firm.Type getFirmType() {
        return firmType;
    }

    @Override
    public ClassDeclaration getClassDeclaration() {
        return getSurroundingMethod().getSurroundingClass();
    }

    public void setRefType(de.dercompiler.semantic.type.Type refType) {
        this.refType = refType;
    }

    public String getIdentifier() {
        return internalVar.getName();
    }

    public Expression getExpression() {
        return valueExpression;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof LocalVariableDeclarationStatement lvds) {
            return type.syntaxEquals(lvds.type)
                    && internalVar.syntaxEquals(lvds.internalVar)
                    && valueExpression.syntaxEquals(lvds.valueExpression);
        }
        return false;
    }

    public void setNodeId(int id) {
        nodeId = id;
    }

    public int getNodeId() {
        return nodeId;
    }

    public Variable getVariable() {
        return internalVar;
    }

    @Override
    public void accept(ASTStatementVisitor astStatementVisitor) {
        astStatementVisitor.visitLocalVariableDeclarationStatement(this);
    }

    public boolean isIdSet() {
        return nodeId > 0;
    }
}
