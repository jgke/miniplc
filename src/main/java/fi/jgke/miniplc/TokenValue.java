package fi.jgke.miniplc;

public enum TokenValue {
    /* keywords */
    VAR, FOR, END, IN, DO, READ, PRINT, INT, STRING, BOOL, ASSERT,

    /* ( ) ; : := .. */
    OPEN_BRACE, CLOSE_BRACE, SEMICOLON, COLON, ASSIGN, RANGE,

    /* + - * / < = & ! */
    PLUS, MINUS, TIMES, DIVIDE, LESSTHAN, EQUALS, AND, NOT,

    /* "..." 123 */
    STRINGVAR, INTVAR, BOOLVAR,

    /* any type identifier */
    TYPE,

    /* any variable identifier */
    IDENTIFIER,

    /* End of stream */
    EOS;



    public boolean isOperator() {
        TokenValue[] values = {TokenValue.PLUS, TokenValue.MINUS, TokenValue.TIMES, TokenValue.DIVIDE,
                TokenValue.LESSTHAN, TokenValue.EQUALS, TokenValue.AND};
        for(TokenValue value : values) {
            if(this.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
