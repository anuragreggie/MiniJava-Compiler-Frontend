package minijava;

import org.antlr.v4.runtime.ParserRuleContext;

class Utils {
    public static void errorMessage(ParserRuleContext ctx, String payload){
        System.err.println("\n" + "Error: " + payload + " (line " + ctx.getStart().getLine() + ")");
    }

}