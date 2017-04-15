package fi.jgke.miniplc.builder;

import fi.jgke.miniplc.exception.OperationNotSupportedException;
import fi.jgke.miniplc.exception.TypeException;
import fi.jgke.miniplc.interpreter.Context;
import fi.jgke.miniplc.interpreter.Variable;
import fi.jgke.miniplc.interpreter.VariableType;
import fi.jgke.miniplc.tokenizer.Token;
import fi.jgke.miniplc.tokenizer.TokenQueue;
import fi.jgke.miniplc.tokenizer.TokenValue;

import java.util.List;
import java.util.function.Supplier;

import static fi.jgke.miniplc.builder.Empty.empty;
import static fi.jgke.miniplc.builder.For.when;
import static fi.jgke.miniplc.builder.Or.or;

public class Builder {

    public static void parseAndExecute(TokenQueue tokenQueue, Context context) {
        statements().consume(tokenQueue).getValue(context, Object.class);
        assert tokenQueue.isEmpty();
    }

    private static Rule statements() {
        return
                or(
                        rule(
                                when(statement(), semicolon, lazy(Builder::statements)),
                                (rules, context) -> {
                                    rules.get(0).getValue(context, Object.class);
                                    rules.get(2).getValue(context, Object.class);
                                    return null;
                                }
                        ), eos, empty()
                );
    }

    private static Rule statement() {
        return
                or(
                        rule(
                                when(var, varIdent, colon, type, Maybe.maybe(assign, expression())),
                                (rules, context) -> {
                                    String name = rules.get(1).getValue(context, Token.class).getString();
                                    VariableType type = rules.get(3).getValue(context, Token.class).getVariableType();
                                    List<ConsumedRule> value = rules.get(4).getValue(context, List.class);
                                    if (!value.isEmpty()) {
                                        Variable variable = value.get(1).getValue(context, Variable.class);
                                        if (!variable.getType().equals(type))
                                            throw new TypeException(type, variable.getType());
                                        variable.setName(name);
                                        context.addVariable(variable);
                                    } else {
                                        context.addVariable(new Variable(name, type));
                                    }
                                    return null;
                                }
                        ),
                        rule(
                                when(varIdent, assign, expression()),
                                (rules, context) -> {
                                    String name = rules.get(0).getValue(context, Token.class).getString();
                                    Variable variable = context.getVariable(name);
                                    Variable newValue = rules.get(2).getValue(context, Variable.class);
                                    context.updateVariable(new Variable(name, variable.getType(), newValue.getValue()));
                                    return null;
                                }
                        ),
                        rule(
                                when(print, expression()),
                                (rules, context) -> {
                                    Variable output = rules.get(1).getValue(context, Variable.class);
                                    context.print(output.getValue());
                                    return null;
                                }
                        ),
                        rule(
                                when(read, varIdent),
                                (rules, context) -> {
                                    String name = rules.get(1).getValue(Token.class).getString();
                                    String input = context.readLine();
                                    Variable variable;

                                    /* Why, MiniPL language ;__;
                                    * read :: () -> Int
                                    * read :: () -> String
                                    */
                                    try {
                                        Integer value = Integer.parseInt(input);
                                        variable = new Variable(name, VariableType.INT, value);
                                    } catch (NumberFormatException ignored) {
                                        variable = new Variable(name, VariableType.STRING, input);
                                    }

                                    context.updateVariable(variable);
                                    return null;
                                }
                        ),
                        rule(
                                when(Assert, openbrace, expression(), closebrace),
                                (rules, context) -> {
                                    Boolean value = (Boolean)rules.get(2).getValue(context, Variable.class).getValue();
                                    assert value;
                                    return null;
                                }
                        ),
                        rule(
                                when(For, varIdent, in, expression(), range, expression(), Do, lazy(() -> statements()), end, For),
                                (rules, context) -> {
                                    String loopVariableName = rules.get(1).getValue(Token.class).getString();
                                    Integer start = (Integer)rules.get(3).getValue(context, Variable.class).getValue();
                                    Integer end = (Integer)rules.get(5).getValue(context, Variable.class).getValue();

                                    Variable loopVariable = new Variable(loopVariableName, VariableType.INT, start);
                                    context.updateVariable(loopVariable);

                                    for (Integer i = start; i <= end; i++) {
                                        context.pushFrame();

                                        rules.get(7).getValue(context, Object.class);

                                        context.popFrame();
                                        loopVariable = new Variable(loopVariableName, VariableType.INT, i+1);
                                        context.updateVariable(loopVariable);
                                    }

                                    return null;
                                }
                        )


                );
    }

    public static Rule expression() {
        return or(
                rule(
                        when(not, operand()),
                        (rules, context) -> {
                            Boolean value = (Boolean) rules.get(1).getValue(context, Variable.class).getValue();
                            return new Variable(VariableType.BOOL, !value);
                        }
                ),
                rule(
                        when(operand(), Maybe.maybe(operator(), operand())),
                        (rules, context) -> {
                            Variable left = rules.get(0).getValue(context, Variable.class);
                            List<ConsumedRule> b = rules.get(1).getValue(context, List.class);
                            if (!b.isEmpty()) {
                                Token operator = b.get(0).getValue(context, Token.class);
                                Variable right = b.get(1).getValue(context, Variable.class);
                                return handleOperation(context, left, operator, right);
                            }
                            return left;
                        }
                )
        );
    }

    private static Rule operator() {
        return or(plus, minus, times, divide, lessthan, equals);
    }

    public static Rule operand() {
        return or(
                rule(
                        when(or(intvar, stringvar, boolvar)),
                        (rules, context) -> {
                            Token token = rules.get(0).getValue(Token.class);
                            Object content = token.getContent();
                            if (content instanceof Integer)
                                return new Variable(VariableType.INT, token.getContent());
                            else if (content instanceof String)
                                return new Variable(VariableType.STRING, token.getContent());
                            else if (content instanceof Boolean)
                                return new Variable(VariableType.BOOL, token.getContent());
                            throw new UnsupportedOperationException();
                        }),
                rule(
                        when(varIdent),
                        (rules, context) -> context.getVariable(rules.get(0).getValue(Token.class).getString())),
                rule(
                        when(openbrace, lazy(Builder::expression), closebrace),
                        (rules, context) -> rules.get(1).getValue(context, Object.class)
                )

        );
    }

    private static Rule lazy(Supplier<Rule> provider) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return provider.get().matches(tokenQueue);
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                return provider.get().consume(tokenQueue);
            }

            public String str() {
                return "Lazy {}";
            }
        };
    }

    private static Rule rule(For when, Do something) {
        return new Rule() {
            @Override
            public boolean matches(TokenQueue tokenQueue) {
                return when.matches(tokenQueue);
            }

            @Override
            public ConsumedRule consume(TokenQueue tokenQueue) {
                return new ConsumedRule(when.consume(tokenQueue).getValue(List.class), something);
            }

            public String str() {
                return when.str();
            }
        };
    }

    private static Variable handleOperation(Context context, Variable left, Token operator, Variable right) {
        TokenValue op = operator.getValue();

        if (!left.getType().equals(right.getType())) {
            throw new TypeException(left.getType(), right.getType());
        }

        /* Booleans only support AND and EQUALS */
        if (left.getType().equals(VariableType.BOOL) && !(op.equals(TokenValue.AND) || op.equals(TokenValue.EQUALS))) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        /* Strings only support PLUS and EQUALS */
        if (left.getType().equals(VariableType.STRING) && !(op.equals(TokenValue.PLUS) || op.equals(TokenValue.EQUALS))) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        /* Cannot use AND with numbers */
        if (left.getType().equals(VariableType.INT) && op.equals(TokenValue.AND)) {
            throw new OperationNotSupportedException(left.getType(), operator);
        }

        Variable result;
        switch (left.getType()) {
            case INT: {
                Integer lval = (Integer) left.getValue();
                Integer rval = (Integer) right.getValue();
                switch (op) {
                    case PLUS:
                        return new Variable(VariableType.INT, lval + rval);
                    case MINUS:
                        result = new Variable(VariableType.INT, lval - rval);
                        break;
                    case TIMES:
                        result = new Variable(VariableType.INT, lval * rval);
                        break;
                    case DIVIDE:
                        result = new Variable(VariableType.INT, lval / rval);
                        break;
                    case LESSTHAN:
                        result = new Variable(VariableType.BOOL, lval < rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval == rval);
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            case STRING: {
                String lval = (String) left.getValue();
                String rval = (String) right.getValue();
                switch (op) {
                    case PLUS:
                        result = new Variable(VariableType.STRING, lval + rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval.equals(rval));
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            case BOOL: {
                Boolean lval = (Boolean) left.getValue();
                Boolean rval = (Boolean) right.getValue();
                switch (op) {
                    case AND:
                        result = new Variable(VariableType.BOOL, lval && rval);
                        break;
                    case EQUALS:
                        result = new Variable(VariableType.BOOL, lval.equals(rval));
                        break;
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
                break;
            }
            default:
                throw new OperationNotSupportedException(left.getType(), operator);
        }
        result.getValue();
        return result;
    }

    private static Rule var = new Terminal(TokenValue.VAR);
    private static Rule varIdent = new Terminal(TokenValue.IDENTIFIER);
    private static Rule colon = new Terminal(TokenValue.COLON);
    private static Rule type = new Terminal(TokenValue.TYPE);
    private static Rule assign = new Terminal(TokenValue.ASSIGN);
    private static Rule not = new Terminal(TokenValue.NOT);
    private static Rule intvar = new Terminal(TokenValue.INTVAR);
    private static Rule stringvar = new Terminal(TokenValue.STRINGVAR);
    private static Rule boolvar = new Terminal(TokenValue.BOOLVAR);
    private static Rule openbrace = new Terminal(TokenValue.OPEN_BRACE);
    private static Rule closebrace = new Terminal(TokenValue.CLOSE_BRACE);
    private static Rule plus = new Terminal(TokenValue.PLUS);
    private static Rule minus = new Terminal(TokenValue.MINUS);
    private static Rule times = new Terminal(TokenValue.TIMES);
    private static Rule divide = new Terminal(TokenValue.DIVIDE);
    private static Rule lessthan = new Terminal(TokenValue.LESSTHAN);
    private static Rule equals = new Terminal(TokenValue.EQUALS);
    private static Rule print = new Terminal(TokenValue.PRINT);
    private static Rule semicolon = new Terminal(TokenValue.SEMICOLON);
    private static Rule eos = new Terminal(TokenValue.EOS);
    private static Rule read = new Terminal(TokenValue.READ);
    private static Rule Assert = new Terminal(TokenValue.ASSERT);
    private static Rule For = new Terminal(TokenValue.FOR);
    private static Rule in = new Terminal(TokenValue.IN);
    private static Rule range = new Terminal(TokenValue.RANGE);
    private static Rule Do = new Terminal(TokenValue.DO);
    private static Rule end = new Terminal(TokenValue.END);
}
