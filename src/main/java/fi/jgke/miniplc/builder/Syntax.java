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

import fi.jgke.miniplc.builder.handlers.ExpressionHandlers;
import fi.jgke.miniplc.builder.handlers.OperandHandlers;
import fi.jgke.miniplc.builder.handlers.StatementHandlers;
import fi.jgke.miniplc.builder.handlers.StatementsHandlers;

import static fi.jgke.miniplc.builder.BaseRules.*;
import static fi.jgke.miniplc.builder.Terminal.*;

public class Syntax {
    public static Rule statements() {
        return lazy(() ->
                any(
                        rule(
                                all(statement(), Semicolon, statements()),
                                StatementsHandlers::executeStatements),
                        empty()
                ));
    }

    public static Rule statement() {
        return lazy(() ->
                any(
                        rule(
                                all(Var, Identifier, Colon, Type, maybe(Assign, expression())),
                                StatementHandlers::createVariable),
                        rule(
                                all(Identifier, Assign, expression()),
                                StatementHandlers::updateVariable),
                        rule(
                                all(Print, expression()),
                                StatementHandlers::printExpression),
                        rule(
                                all(Read, Identifier),
                                StatementHandlers::readVariable),
                        rule(
                                all(Assert, OpenBrace, expression(), CloseBrace),
                                StatementHandlers::assertExpression),
                        rule(
                                all(For, Identifier, In, expression(), Range, expression(), Do, statements(), End, For),
                                StatementHandlers::forLoop)
                ));
    }

    public static Rule expression() {
        return lazy(() ->
                any(
                        rule(
                                all(Not, operand()),
                                ExpressionHandlers::handleNot),
                        rule(
                                all(operand(), maybe(operator(), operand())),
                                ExpressionHandlers::handleOperation)
                ));
    }

    public static Rule operator() {
        return any(Plus, Minus, Times, Divide, LessThan, Equals, And);
    }

    public static Rule operand() {
        return lazy(() ->
                any(
                        rule(
                                all(any(IntConst, StringConst, BoolConst)),
                                OperandHandlers::handleConstant),
                        rule(
                                all(Identifier),
                                OperandHandlers::handleIdentifier),
                        rule(
                                all(OpenBrace, expression(), CloseBrace),
                                OperandHandlers::handleExpression)
                ));
    }
}
