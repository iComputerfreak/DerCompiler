package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.AbstractExpression;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.type.TypeRest;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.LexerErrorIds;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.IdentifierToken;
import de.dercompiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;

import static de.dercompiler.lexer.token.Token.*;

public class Parser {

    Lexer lexer;
    PrecedenceParser precedenceParser;
    private final OutputMessageHandler logger;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.precedenceParser = new PrecedenceParser(lexer, this);
        this.logger = new OutputMessageHandler(MessageOrigin.PARSER, System.err);
    }
    
    public Program parseProgram() {
        List<ClassDeclaration> classes = new ArrayList<>();
        while (lexer.nextToken() == CLASS) {
            classes.add(parseClassDeclaration());
        }
        if (lexer.nextToken() != EOF) {
            logger.printErrorAndExit(ParserErrorIds.TODO, "Expected class declaration");
        }
        return new Program(classes);
    }
    
    public ClassDeclaration parseClassDeclaration() {
        expect(CLASS);
        IdentifierToken identifier = expectIdentifier();
        expect(L_CURLY_BRACKET);
        List<ClassMember> members = new ArrayList<>();
        // While our next token is not the '}' token
        while (lexer.peek(0) != R_CURLY_BRACKET) {
            members.add(parseClassMember());
        }
        return new ClassDeclaration(identifier.getIdentifier(), members);
    }
    
    public ClassMember parseClassMember() {
        // MainMethod ->    public static void IDENT ( Type IDENT )
        // Field ->         public Type IDENT ;
        // Method ->        public Type IDENT ( Parameters? ) MethodRest? Block
        if (lexer.peek(0) == PUBLIC) {
            // MainMethod
            if (lexer.peek(1) == STATIC) {
                return parseMainMethod();
            }
            // Check if a Type token follows
            IToken type = lexer.peek(1);
            if (type == INT_TYPE || type == BOOLEAN_TYPE || type == VOID || type instanceof IdentifierToken) {
                if (lexer.peek(2) instanceof IdentifierToken) {
                    // Field
                    if (lexer.peek(3) == SEMICOLON) {
                        return parseField();
                    }
                    // Method
                    if (lexer.peek(3) == L_PAREN) {
                        return parseMethod();
                    }
                }
            }
        }
        // TODO: Error message
        logger.printErrorAndExit(ParserErrorIds.TODO, "TODO");
        return null;
    }
    
    public MainMethod parseMainMethod() {
        // TODO: Implement
        return null;
    }
    
    public Field parseField() {
        // TODO: Implement
        return null;
    }
    
    public Method parseMethod() {
        // TODO: Implement
        return null;
    }
    
    public MethodRest parseMethodRest() {
        // TODO: Implement
        return null;
    }

    public Parameters parseParameters() {
        // TODO: Implement
        return null;
    }

    public ParametersRest parseParametersRest() {
        // TODO: Implement
        return null;
    }

    public Parameter parseParameter() {
        // TODO: Implement
        return null;
    }

    public Type parseType() {
        // TODO: Implement
        return null;
    }
    
    public TypeRest parseTypeRest() {
        // TODO: Implement
        return null;
    }
    
    public BasicType parseBasicType() {
        // TODO: Implement
        return null;
    }

    /**
     * Checks, if the lexer's next token matches with the given token. Otherwise, prints an error and exits the program.
     * @param t The token to check for.
     */
    private void expect(Token t) {
        // TODO: Read position and add it to the error output
        if (lexer.nextToken() != t) {
            logger.printErrorAndExit(ParserErrorIds.TODO, "Expected " + t.toString() + ".");
        }
    }

    /**
     * Checks if the lexer's next token is an identifier.
     * @return The IdentifierToken, if there is one, otherwise the function prints an error and exits without returning anything.
     */
    private IdentifierToken expectIdentifier() {
        // TODO: Read position and add it to the error output
        IToken t = lexer.nextToken();
        if (t instanceof IdentifierToken) {
            return (IdentifierToken) t;
        }
        logger.printErrorAndExit(ParserErrorIds.TODO, "Identifier expected.");
        return null;
    }

    public AbstractExpression parseUnaryExp() {
        return null;
    }

}
