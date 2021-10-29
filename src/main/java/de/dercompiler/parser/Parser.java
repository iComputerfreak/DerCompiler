package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.IdentifierToken;
import de.dercompiler.lexer.token.IntegerToken;
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
        // ClassDeclaration*
        List<ClassDeclaration> classes = new ArrayList<>();
        while (lexer.peek().type() == CLASS) {
            classes.add(parseClassDeclaration());
        }
        if (lexer.nextToken().type() != EOF) {
            logger.printErrorAndExit(ParserErrorIds.EXPECTED_CLASS_DECLARATION, "Expected class declaration");
        }
        return new Program(classes);
    }
    
    public ClassDeclaration parseClassDeclaration() {
        // class IDENT { ClassMember* }
        expect(CLASS);
        IdentifierToken identifier = expectIdentifier();
        expect(L_CURLY_BRACKET);
        List<ClassMember> members = new ArrayList<>();
        // While our next token is not the '}' token
        while (lexer.peek().type() != R_CURLY_BRACKET) {
            members.add(parseClassMember());
        }
        // Consume the R_CURLY_BRACKET
        lexer.nextToken();
        return new ClassDeclaration(identifier.getIdentifier(), members);
    }
    
    public ClassMember parseClassMember() {
        // MainMethod ->    public static void IDENT ( Type IDENT )
        // Field ->         public Type IDENT ;
        // Method ->        public Type IDENT ( Parameters? ) MethodRest? Block
        if (lexer.peek().type() == PUBLIC) {
            // MainMethod
            if (lexer.peek(1).type() == STATIC) {
                return parseMainMethod();
            }
            // Check if a Type token follows
            IToken type = lexer.peek(1).type();
            if (type == INT_TYPE || type == BOOLEAN_TYPE || type == VOID || type instanceof IdentifierToken) {
                if (lexer.peek(2).type() instanceof IdentifierToken) {
                    // Field
                    if (lexer.peek(3).type() == SEMICOLON) {
                        return parseField();
                    }
                    // Method
                    if (lexer.peek(3).type() == L_PAREN) {
                        return parseMethod();
                    }
                }
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_PUBLIC_KEYWORD, "Expected 'public' keyword");
        return null;
    }
    
    public Field parseField() {
        // public Type IDENT ;
        expect(PUBLIC);
        Type type = parseType();
        IdentifierToken fieldName = expectIdentifier();
        expect(SEMICOLON);
        return new Field(type, fieldName.getIdentifier());
    }
    
    public MainMethod parseMainMethod() {
        // public static void IDENT ( Type IDENT ) MethodRest? Block
        expect(PUBLIC);
        expect(STATIC);
        expect(VOID);
        IdentifierToken name = expectIdentifier();
        expect(L_PAREN);
        Type paramType = parseType();
        IdentifierToken paramName = expectIdentifier();
        expect(R_PAREN);
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new MainMethod(name.getIdentifier(), paramType, paramName.getIdentifier(), methodRest, block);
    }
    
    public Method parseMethod() {
        // public Type IDENT ( Parameters? ) MethodRest? Block
        expect(PUBLIC);
        Type type = parseType();
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        // Check if we have parameters
        // First2(Parameters) = First2(Parameter) = First2(Type) u {IDENT} = First2(BasicType) x {IDENT}
        // = {int IDENT, boolean IDENT, void IDENT, IDENT IDENT}
        IToken t = lexer.peek().type();
        Parameters params = null;
        if (t == INT_TYPE || t == BOOLEAN_TYPE || t == VOID || t instanceof IdentifierToken) {
            if (lexer.peek(1).type() instanceof IdentifierToken) {
                params = parseParameters();
            }
        }
        expect(R_PAREN);
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new Method(type, ident.getIdentifier(), params, methodRest, block);
    }
    
    public MethodRest parseMethodRest() {
        // throws IDENT
        expect(THROWS);
        IdentifierToken ident = expectIdentifier();
        return new MethodRest(ident.getIdentifier());
    }

    public Parameters parseParameters() {
        // Parameter ParametersRest
        Parameter p = parseParameter();
        ParametersRest rest = parseParametersRest();
        return new Parameters(p, rest);
    }

    public ParametersRest parseParametersRest() {
        // (, Parameter ParametersRest)?
        if (lexer.peek().type() == COMMA) {
            expect(COMMA);
            Parameter p = parseParameter();
            ParametersRest rest = parseParametersRest();
            return new ParametersRest(p, rest);
        }
        // If there is no rest, we return null
        return null;
    }

    public Parameter parseParameter() {
        // Type IDENT
        Type type = parseType();
        IdentifierToken ident = expectIdentifier();
        return new Parameter(type, ident.getIdentifier());
    }

    public Type parseType() {
        // BasicType TypeRest
        BasicType type = parseBasicType();
        TypeRest rest = parseTypeRest();
        return new Type(type, rest);
    }
    
    public TypeRest parseTypeRest() {
        // ([] TypeRest)?
        if (lexer.peek().type() == L_SQUARE_BRACKET) {
            expect(L_SQUARE_BRACKET);
            expect(R_SQUARE_BRACKET);
            TypeRest rest = parseTypeRest();
            return new TypeRest(rest);
        }
        // If there is no rest, we return null
        return null;
    }
    
    public BasicType parseBasicType() {
        // int | boolean | void | IDENT
        IToken t = lexer.nextToken().type();
        if (t instanceof IdentifierToken ident) {
            return new CustomType(ident.getIdentifier());
        }
        if (t instanceof Token token) {
            switch (token) {
                case INT_TYPE:
                    return new IntType();
                case BOOLEAN_TYPE:
                    return new BooleanType();
                case VOID:
                    return new VoidType();
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_BASIC_TYPE,
                "Expected 'int', 'boolean', 'void' or an identifier.");
        return null;
    }
    
    public BasicBlock parseBasicBlock() {
        // TODO: Implement
        return null;
    }

    /**
     * Checks, if the lexer's next token matches with the given token and consumes it.
     * Otherwise, prints an error and exits the program.
     * @param t The token to check for.
     */
    private void expect(Token t) {
        // TODO: Read position and add it to the error output
        if (lexer.nextToken().type() != t) {
            logger.printErrorAndExit(ParserErrorIds.EXPECTED_TOKEN, "Expected " + t.toString() + ".");
        }
    }

    /**
     * Checks if the lexer's next token is an identifier.
     * @return The IdentifierToken, if there is one, otherwise the function prints an error and exits without returning anything.
     */
    private IdentifierToken expectIdentifier() {
        // TODO: Read position and add it to the error output
        TokenOccurrence t = lexer.nextToken();
        if (t.type() instanceof IdentifierToken) {
            return (IdentifierToken) t.type();
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_IDENTIFIER, "Identifier expected.");
        return null;
    }

    public AbstractExpression parseExpression() {
        return precedenceParser.parseExpression();
    }

    public AbstractExpression parseUnaryExpression() {
        IToken token = lexer.nextToken().type();
        if (nextIsPrimary(lexer.peek().type())) {
            return parsePrimaryExpression();
        }
        if (token instanceof Token t) {
            switch (t) {
                case QUESTION_MARK -> {
                    lexer.nextToken();
                    return new LogicalNotExpression(parseUnaryExpression());
                }
                case MINUS -> {
                    lexer.nextToken();
                    return new NegativeExpression(parseUnaryExpression());
                }
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_PRIMARY_EXPRESSION, "Expected Primary Expression, such as Variable, Constant or MethodInvocation!");
        return new ErrorExpression();
    }

    public AbstractExpression parsePostfixExpression() {
        AbstractExpression expression = parsePrimaryExpression();
        IToken token;
        while ((token = lexer.peek().type()) instanceof Token t) {
            switch (t) {
                case DOT: {
                    if(lexer.peek(2).type() instanceof Token t2 && t2 == L_PAREN) {
                        expression = parseMethodInvocation(expression);
                    } else {
                        expression = parseFieldAccess(expression);
                    }
                }
                case L_SQUARE_BRACKET: {
                    expression = parseFieldAccess(expression);
                }
                default: {
                    return expression;
                }
            }
        }
        return expression;
    }

    public AbstractExpression parseMethodInvocation(AbstractExpression expression) {
        expect(DOT);
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        Arguments arguments = parseArguments();
        expect(R_PAREN);
        return new MethodInvocationOnObject(expression, arguments);
    }

    public Arguments parseArguments() {
        Arguments arguments = new Arguments();
        IToken token = lexer.peek().type();

        if (token instanceof Token t && t == R_PAREN) return arguments;

        do {
            arguments.addArgument(parseExpression());
            if ((token = lexer.peek().type()) instanceof Token t && t != R_PAREN) {
                expect(COMMA);
            } else {
                break;
            }
        } while(true);
        //token is at this point one behind the lexer, if we removed last a comma, we miss at leased one Argument
        if (token instanceof Token t && t == COMMA) {
            logger.printErrorAndExit(ParserErrorIds.EXPECTED_ARGUMENT, "Argument expect after Token \",\"!" + lexer.getPosition());
        }
        return arguments;
    }

    public AbstractExpression parseFieldAccess(AbstractExpression expression) {
        expect(DOT);
        IdentifierToken ident = expectIdentifier();
        return new FieldAccess(expression, ident.getIdentifier());
    }

    public AbstractExpression parseArrayAccess(AbstractExpression expression) {
        expect(L_SQUARE_BRACKET);
        AbstractExpression arrayPosition = parseExpression();
        expect(R_SQUARE_BRACKET);
        return new ArrayAccess(expression, arrayPosition);
    }

    private boolean nextIsPrimary(IToken token) {
        if (token instanceof IntegerToken) return true;
        if (token instanceof IdentifierToken) return true;
        if (token instanceof Token t) {
            return switch (t) {
                case NULL, FALSE, TRUE, THIS, NEW, L_PAREN -> true;
                default -> false;
            };
        }
        //this should not be possible, because all instances of IToken are checked
        return false;
    }

    public AbstractExpression parsePrimaryExpression() {
        AbstractExpression expression = new ErrorExpression();
        IToken token = lexer.peek().type();
        if (token instanceof IdentifierToken ident) {
            if (lexer.peek(1).type() == L_PAREN) {
                expect(L_PAREN);
                Arguments arguments = parseArguments();
                expect(R_PAREN);
                //we create a ThisValue out of nowhere, because methods only can be invoked on other objects or the own object(this)
                expression = new MethodInvocationOnObject(new ThisValue(), arguments);
            } else {
                expression = new Variable(ident.getIdentifier());
            }
        }
        if (token instanceof IntegerToken i) {
            expression = new IntegerValue(i.getValue());
        }
        if (token instanceof Token t) {
            switch(t) {
                case NULL: {
                    expression = new NullValue();
                } break;
                case FALSE: {
                    expression = new BooleanValue(false);
                } break;
                case TRUE: {
                    expression = new BooleanValue(true);
                } break;
                case THIS: {
                    expression = new ThisValue();
                } break;
                case L_PAREN: {
                    expect(L_PAREN);
                    expression = parseExpression();
                    expect(R_PAREN);
                } break;
                case NEW: {
                    if (lexer.peek(2).type() instanceof Token t2) {
                        if (t2 == L_PAREN) {
                            expression = parseNewObjectExpression();
                        } else if (t2 == L_SQUARE_BRACKET) {
                            expression = parseNewArrayExpression();
                        } else {
                            logger.printErrorAndExit(ParserErrorIds.EXPECTED_OBJECT_INSTANTIATION, "Expected a object instantiation, in line " + lexer.getPosition() + "!");
                        }
                    }
                }
                default: {
                    logger.printErrorAndExit(ParserErrorIds.EXPECTED_PRIMARY_TYPE, "Expected primary-type, no primary type starts with token: " + lexer.peek(0) + " in line: " + lexer.getPosition() + "!");
                }
            }
        }
        return expression;
    }

    public AbstractExpression parseNewArrayExpression() {
        expect(NEW);
        BasicType type = parseBasicType();
        int dimension = 0;
        do {
            expect(L_SQUARE_BRACKET);
            expect(R_SQUARE_BRACKET);
            dimension++;
        } while (lexer.peek().type() == L_SQUARE_BRACKET);
        return new NewArrayExpression(type, dimension);
    }

    public AbstractExpression parseNewObjectExpression() {
        expect(NEW);
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        expect(R_PAREN);
        return new NewObjectExpression(new CustomType(ident.getIdentifier()));
    }

}
