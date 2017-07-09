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

/**
 * Syntax definitions for the language
 */
public class Syntax {
    /*
     * <program> ::= <statement> ";" <statements>
     */
    public static Rule program() {
        return lazy(() -> rule(StatementsHandlers::executeStatements,
                               statement(), Semicolon, statements())
        );
    }

    /*
     * <statements> ::= <statement> ";" <statements>
     *               |  epsilon
     */
    public static Rule statements() {
        return lazy(() -> any(
                rule(StatementsHandlers::executeStatements,
                     statement(), Semicolon, statements()),
                empty()
        ));
    }

    /*
     * <statement> ::=  "var" <identifier> ":" <type> <maybe_assign>
     *              |   <identifier> ":=" <expression>
     *              |   "for" <identifier> "in" <expression> ".." <expression> "do"
     *                     <statements> "end" "for"
     *              |   "read" <identifier>
     *              |   "print" <expression>
     *              |   "assert" "(" <expression> ")"
     */
    public static Rule statement() {
        return lazy(() -> any(
                rule(StatementHandlers::createVariable,
                     Var, Identifier, Colon, Type, maybe_assign()),
                rule(StatementHandlers::updateVariable,
                     Identifier, Assign, expression()),
                rule(StatementHandlers::printExpression,
                     Print, expression()),
                rule(StatementHandlers::readVariable,
                     Read, Identifier),
                rule(StatementHandlers::assertExpression,
                     Assert, OpenBrace, expression(), CloseBrace),
                rule(StatementHandlers::forLoop,
                     For, Identifier, In, expression(), Range, expression(), Do, statements(), End, For)
        ));
    }

    /*
     * <maybe_assign> ::= ":=" <expression>
     *                 |  epsilon
     */
    private static Rule maybe_assign() {
        return lazy(() -> any(
                all(Assign, expression()),
                empty()
        ));
    }

    /*
     * <expression> ::= <unaryOperator> <operand>
     *               | <operand> <maybe_operand>
     */
    public static Rule expression() {
        return lazy(() -> any(
                rule(ExpressionHandlers::handleNot,
                     Not, operand()),
                rule(ExpressionHandlers::handleOperation,
                     operand(), maybe_operand())
        ));
    }

    /*
     * <maybe_operand> ::= <binaryOperator> <operand>
     *                  |  epsilon
     */
    private static Rule maybe_operand() {
        return lazy(() -> any(
                all(operator(), operand()),
                empty()
        ));
    }

    /*
     * operator ::= "+" | "-" | "*" | "/" | "<" | "=" | "&"
     */
    public static Rule operator() {
        return any(Plus, Minus, Times, Divide, LessThan, Equals, And);
    }

    /*
     * <operand> ::=  <intConstant> | <stringConstant> | <boolConstant>
     *            |   <identifier>
     *            |   "(" expr ")"
     */
    public static Rule operand() {
        return lazy(() -> any(
                rule(OperandHandlers::handleConstant,
                     any(IntConst, StringConst, BoolConst)),
                rule(OperandHandlers::handleIdentifier,
                     Identifier),
                rule(OperandHandlers::handleExpression,
                     OpenBrace, expression(), CloseBrace)
        ));
    }
}
