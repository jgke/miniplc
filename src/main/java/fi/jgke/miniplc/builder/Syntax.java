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
        return lazy(() ->
                any(
                        rule(
                                all(statement(), Semicolon, statements()),
                                (rules, context) -> {
                                    rules.get(0).execute(context);
                                    rules.get(2).execute(context);
                                    return null;
                                }),
                        empty()
                ));
    }

    public static Rule statement() {
        return
                any(
                        rule(
                                all(Var, Identifier, Colon, Type, maybe(Assign, expression())),
                                SyntaxHandlers::createVariable),
                        rule(
                                all(Identifier, Assign, expression()),
                                SyntaxHandlers::updateVariable),
                        rule(
                                all(Print, expression()),
                                SyntaxHandlers::printExpression),
                        rule(
                                all(Read, Identifier),
                                SyntaxHandlers::readVariable),
                        rule(
                                all(Assert, OpenBrace, expression(), CloseBrace),
                                SyntaxHandlers::assertExpression),
                        rule(
                                all(For, Identifier, In, expression(), Range, expression(), Do, statements(), End, For),
                                SyntaxHandlers::forLoop)
                );
    }

    public static Rule expression() {
        return lazy(() -> any(
                rule(
                        all(Not, operand()),
                        SyntaxHandlers::handleNot),
                rule(
                        all(operand(), maybe(operator(), operand())),
                        SyntaxHandlers::handleOperation)
        ));
    }

    public static Rule operator() {
        return any(Plus, Minus, Times, Divide, LessThan, Equals, And);
    }

    public static Rule operand() {
        return any(
                rule(
                        all(any(IntConst, StringConst, BoolConst)),
                        SyntaxHandlers::handleConstant),
                rule(
                        all(Identifier),
                        SyntaxHandlers::handleIdentifier),
                rule(
                        all(OpenBrace, expression(), CloseBrace),
                        (rules, context) -> rules.get(1).getVariable(context))
        );
    }
}
