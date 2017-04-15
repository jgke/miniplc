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
        statements().consume(tokenQueue).execute(context);
        assert tokenQueue.isEmpty();
    }

    private static Rule statements() {
        return
                or(
                        rule(
                                when(statement(), semicolon, lazy(Builder::statements)),
                                (rules, context) -> {
                                    rules.get(0).execute(context);
                                    rules.get(2).execute(context);
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
                                    String name = rules.get(1).getToken().getString();
                                    VariableType type = rules.get(3).getToken().getVariableType();
                                    List<ConsumedRule> value = rules.get(4).getList(context);
                                    if (!value.isEmpty()) {
                                        Variable variable = value.get(1).getVariable(context);
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
                                    String name = rules.get(0).getToken().getString();
                                    Variable variable = context.getVariable(name);
                                    Variable newValue = rules.get(2).getVariable(context);
                                    context.updateVariable(new Variable(name, variable.getType(), newValue.getValue()));
                                    return null;
                                }
                        ),
                        rule(
                                when(print, expression()),
                                (rules, context) -> {
                                    Variable output = rules.get(1).getVariable(context);
                                    context.print(output.getValue());
                                    return null;
                                }
                        ),
                        rule(
                                when(read, varIdent),
                                (rules, context) -> {
                                    String name = rules.get(1).getToken().getString();
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
                                    Boolean value = (Boolean)rules.get(2).getVariable(context).getValue();
                                    assert value;
                                    return null;
                                }
                        ),
                        rule(
                                when(For, varIdent, in, expression(), range, expression(), Do, lazy(() -> statements()), end, For),
                                (rules, context) -> {
                                    String loopVariableName = rules.get(1).getToken().getString();
                                    Integer start = (Integer)rules.get(3).getVariable(context).getValue();
                                    Integer end = (Integer)rules.get(5).getVariable(context).getValue();

                                    Variable loopVariable = new Variable(loopVariableName, VariableType.INT, start);
                                    context.updateVariable(loopVariable);

                                    for (Integer i = start; i <= end; i++) {
                                        context.pushFrame();

                                        rules.get(7).execute(context);

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
                            Boolean value = (Boolean) rules.get(1).getVariable(context).getValue();
                            return new Variable(VariableType.BOOL, !value);
                        }
                ),
                rule(
                        when(operand(), Maybe.maybe(operator(), operand())),
                        (rules, context) -> {
                            Variable left = rules.get(0).getVariable(context);
                            List<ConsumedRule> b = rules.get(1).getList(context);
                            if (!b.isEmpty()) {
                                Token operator = b.get(0).getToken();
                                Variable right = b.get(1).getVariable(context);
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
                            Token token = rules.get(0).getToken();
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
                        (rules, context) -> context.getVariable(rules.get(0).getToken().getString())),
                rule(
                        when(openbrace, lazy(Builder::expression), closebrace),
                        (rules, context) -> rules.get(1).getVariable(context)
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
                return new ConsumedRule(when.consume(tokenQueue).getList(null), something);
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

        switch (left.getType()) {
            case INT: {
                Integer leftValue = (Integer) left.getValue();
                Integer rightValue = (Integer) right.getValue();
                switch (op) {
                    case PLUS:
                        return new Variable(VariableType.INT, leftValue + rightValue);
                    case MINUS:
                        return new Variable(VariableType.INT, leftValue - rightValue);
                    case TIMES:
                        return new Variable(VariableType.INT, leftValue * rightValue);
                    case DIVIDE:
                        return new Variable(VariableType.INT, leftValue / rightValue);
                    case LESSTHAN:
                        return new Variable(VariableType.BOOL, leftValue < rightValue);
                    case EQUALS:
                        return new Variable(VariableType.BOOL, leftValue == rightValue);
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
            }
            case STRING: {
                String leftValue = (String) left.getValue();
                String rightValue = (String) right.getValue();
                switch (op) {
                    case PLUS:
                        return new Variable(VariableType.STRING, leftValue + rightValue);
                    case EQUALS:
                        return new Variable(VariableType.BOOL, leftValue.equals(rightValue));
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
            }
            case BOOL: {
                Boolean leftValue = (Boolean) left.getValue();
                Boolean rightValue = (Boolean) right.getValue();
                switch (op) {
                    case AND:
                        return new Variable(VariableType.BOOL, leftValue && rightValue);
                    case EQUALS:
                        return new Variable(VariableType.BOOL, leftValue.equals(rightValue));
                    default:
                        throw new OperationNotSupportedException(left.getType(), operator);
                }
            }
            default:
                throw new OperationNotSupportedException(left.getType(), operator);
        }
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
