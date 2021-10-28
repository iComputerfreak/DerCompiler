package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.ast.type.TypeRest;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
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

    public AbstractExpression parseExpression() {
        return precedenceParser.parseExpression();
    }

    public AbstractExpression parseUnaryExpression() {
        IToken token = lexer.nextToken();
        if (nextIsPrimary(lexer.peek(0))) {
            return parsePrimaryExpression();
        }
        if (token instanceof Token t) {
            switch (t) {
                case QUESTION_MARK: {
                    lexer.nextToken();
                    return new LogicalNotExpression(parseUnaryExpression());
                }
                case MINUS: {
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
        while ((token = lexer.peek(0)) instanceof Token t) {
            switch (t) {
                case DOT: {
                    if(lexer.peek(2) instanceof Token t2 && t2 == L_PAREN) {
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
        return new MethodeInvocationOnObject(expression, arguments);
    }

    public Arguments parseArguments() {
        Arguments arguments = new Arguments();
        IToken token = lexer.peek(0);

        if (token instanceof Token t && t == R_PAREN) return arguments;

        do {
            arguments.addArgument(parseExpression());
            if ((token = lexer.peek(0)) instanceof Token t && t != R_PAREN) {
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
        IToken token = lexer.peek(0);
        if (token instanceof IdentifierToken ident) {
            if (lexer.peek(1) instanceof Token t && t == L_PAREN) {
                expect(L_PAREN);
                Arguments arguments = parseArguments();
                expect(R_PAREN);
                //we create a ThisValue out of nowhere, because methods only can be invoked on other objects or the own object(this)
                expression = new MethodeInvocationOnObject(new ThisValue(), arguments);
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
                    if (lexer.peek(2) instanceof Token t2) {
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
        } while (lexer.peek(0) == L_SQUARE_BRACKET);
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
