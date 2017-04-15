package fi.jgke.miniplc.tokenizer;

public enum TokenValue {
    /* keywords */
    VAR, FOR, END, IN, DO, READ, PRINT, INT, STRING, BOOL, ASSERT,

    /* ( ) ; : := .. */
    OPEN_BRACE, CLOSE_BRACE, SEMICOLON, COLON, ASSIGN, RANGE,

    /* + - * / < = & ! */
    PLUS, MINUS, TIMES, DIVIDE, LESS_THAN, EQUALS, AND, NOT,

    /* "..." 123 */
    STRING_CONST, INT_CONST, BOOL_CONST,

    /* any type identifier */
    TYPE,

    /* any variable identifier */
    IDENTIFIER,

    /* end of stream */
    EOS
}
