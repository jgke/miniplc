/*
 * Copyright 2017 Jaakko Hannikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fi.jgke.miniplc.builder;

import static fi.jgke.miniplc.builder.BaseRules.*;
import static fi.jgke.miniplc.builder.Terminal.*;

public class Syntax {
    public static Rule statements() {
        return
                any(
                        rule(
                                all(statement(), semicolon, lazy(Syntax::statements)),
                                (rules, context) -> {
                                    rules.get(0).execute(context);
                                    rules.get(2).execute(context);
                                    return null;
                                }),
                        empty()
                );
    }

    public static Rule statement() {
        return
                any(
                        rule(
                                all(var, identifier, colon, type, maybe(assign, expression())),
                                SyntaxHandlers::createVariable),
                        rule(
                                all(identifier, assign, expression()),
                                SyntaxHandlers::updateVariable),
                        rule(
                                all(print, expression()),
                                SyntaxHandlers::printExpression),
                        rule(
                                all(read, identifier),
                                SyntaxHandlers::readVariable),
                        rule(
                                all(Assert, openBrace, expression(), closeBrace),
                                SyntaxHandlers::assertExpression),
                        rule(
                                all(For, identifier, in, expression(), range, expression(), Do, lazy(Syntax::statements), end, For),
                                SyntaxHandlers::forLoop)
                );
    }

    public static Rule expression() {
        return any(
                rule(
                        all(not, operand()),
                        SyntaxHandlers::handleNot),
                rule(
                        all(operand(), maybe(operator(), operand())),
                        SyntaxHandlers::handleOperation)
        );
    }

    public static Rule operator() {
        return any(plus, minus, times, divide, lessThan, equals, and);
    }

    public static Rule operand() {
        return any(
                rule(
                        all(any(intConst, stringConst, boolConst)),
                        SyntaxHandlers::handleConstant),
                rule(
                        all(identifier),
                        SyntaxHandlers::handleIdentifier),
                rule(
                        all(openBrace, lazy(Syntax::expression), closeBrace),
                        (rules, context) -> rules.get(1).getVariable(context))
        );
    }
}
