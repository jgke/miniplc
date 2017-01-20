package fi.jgke.miniplc;

public enum TokenValue {
    /* keywords */
    VAR, FOR, END, IN, DO, READ, PRINT, INT, STRING, BOOL, ASSERT,
    
    /* ( ) ; : := */
    OPEN_BRACE, CLOSE_BRACE, SEMICOLON, COLON, ASSIGN,
    
    /* + - * / < = & ! */
    PLUS, MINUS, TIMES, DIVIDE, LESSTHAN, EQUALS, AND, NOT,
    
    /* "..." 123 */
    STRINGVAR, INTVAR, BOOLVAR,

    /* any type identifier */
    TYPE,

    /* any variable identifier */
    IDENTIFIER
}
