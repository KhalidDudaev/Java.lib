package lib;

/**
 * Created by BOSS on 12.06.2015.
 */

//import com.sun.org.apache.bcel.internal.generic.Select;

//import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

//import java.math.BigDecimal;
//import java.math.MathContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorString {

    private static String paren                 = "(?<paren>\\((?>[^\\(\\)]+|\\k<paren>)*\\))";
    private static String parenin               = "^\\((?<parenin>.*?)\\)$";
    private static String trg                   = "(?<trg>sin|cos|tan|atan)";
    private static String decimal               = "(?:(?:\\-)?\\d+(?:\\.\\d+)?)";
    private static String arg1                  = "(?<arg1>" + decimal + ")";
    private static String arg2                  = "(?<arg2>" + decimal + ")";
    private static String add_sub               = "(?<addsub>\\+|\\-)";
    private static String mux_div               = "(?<muxdiv>\\*|/)";

    private static Pattern p_paren               = Pattern.compile(paren);
    private static Pattern p_pareninn            = Pattern.compile(parenin);
    private static Pattern p_trig                = Pattern.compile(trg + "\\s*" + arg1);
    private static Pattern p_term                = Pattern.compile(arg1 + "\\s*" + mux_div + "\\s*" + arg2);
    private static Pattern p_expr                = Pattern.compile(arg1 + "\\s*" + add_sub + "\\s*" + arg2);
//    private static MathContext mc                   = new MathContext(16);
//    private static BigDecimal dec                = new BigDecimal("0");




    public static String calculate (String expr) {
        expr                                    = group(expr);
        expr                                    = func(expr);
        expr                                    = mux_div(expr);
        expr                                    = add_sub(expr);
        //expr = expr - -2.7755575615628914E-17
        //-5.551115123125783E-17
        if (Double.parseDouble(expr) < 1E-16){
            expr = "0.0";
        }
        return expr;
    }

    private static String group (String expr){
        Matcher m_paren                         = p_paren.matcher(expr);
        if (m_paren.find()){
            Matcher m_pareninn                  = p_pareninn.matcher(m_paren.group("paren"));
            if(m_pareninn.find()) {
                expr                            = m_paren.replaceFirst(calculate(m_pareninn.group("parenin")));
            }
            expr = group(expr);
        }
        return expr;
    }

    private static String func (String expr){
        Matcher m_trig              = p_trig.matcher(expr);
        String a                        = "";
        String trig                     = "";

        if (m_trig.find()) {
            a       = m_trig.group("arg1");
            trig    = m_trig.group("trg");
            switch (trig) {
                case "sin":
                    expr = m_trig.replaceFirst(Double.toString(Math.sin(Double.parseDouble(a))));
                    break;
                case "cos":
                    expr = m_trig.replaceFirst(Double.toString(Math.cos(Double.parseDouble(a))));
                    break;
                case "tan":
                    expr = m_trig.replaceFirst(Double.toString(Math.tan(Double.parseDouble(a))));
                    break;
                case "atan":
                    expr = m_trig.replaceFirst(Double.toString(Math.atan(Double.parseDouble(a))));
                    break;
            }
            expr = func(expr);
        }
        return expr;
    }

    private static String mux_div (String expr){
        Matcher m_term              = p_term.matcher(expr);
        String a                        = "";
        String b                        = "";
        String muxdiv                   = "";

        if (m_term.find()) {
            a = m_term.group("arg1");
            b = m_term.group("arg2");
            muxdiv = m_term.group("muxdiv");
            switch (muxdiv) {
                case "*":
                    expr = m_term.replaceFirst(Double.toString(Double.parseDouble(a) * Double.parseDouble(b)));
                    break;
                case "/":
                    expr = m_term.replaceFirst(Double.toString(Double.parseDouble(a) / Double.parseDouble(b)));
                    break;
            }
            expr = mux_div(expr);
        }
        return expr;
    }

    private static String add_sub (String expr){
        Matcher m_expr              = p_expr.matcher(expr);
        String a                        = "";
        String b                        = "";
        String addsub                   = "";

        if (m_expr.find()) {
            a = m_expr.group("arg1");
            b = m_expr.group("arg2");
            addsub = m_expr.group("addsub");
            switch (addsub) {
                case "+":
                    expr = m_expr.replaceFirst(Double.toString(Double.parseDouble(a) + Double.parseDouble(b)));
                    break;
                case "-":
                    expr = m_expr.replaceFirst(Double.toString(Double.parseDouble(a) - Double.parseDouble(b)));
                    break;
            }
            expr = add_sub(expr);
        }
        return expr;
    }

}
