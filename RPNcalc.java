import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;


class RPNCalc {
    public Stack<Integer> stack;

        public RPNCalc(){
            stack = new Stack<Integer>(); //new stack in constructor
        }


    public void clearStack(){
        stack.clear();
    }
    public boolean isOperator(String token)
    {
        return ( token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/") || token.equals("%") );

    }
    public int evaluateOperation(char operation, int a, int b)
    {
        int result = 0;

        switch (operation)
        {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            case '%':
                result = a % b;
                break;
        }

        return result;
    }
    public int evaluateExpression(String operation){

        int a, b, result = 0;
        String sign;
        Scanner parse = new Scanner(operation);

        while(parse.hasNext()){
        sign = parse.next();
        if (isOperator(sign)){
            b = (stack.pop()).intValue();
            a = (stack.pop()).intValue();
            result = evaluateOperation(sign.charAt(0),a,b);
            stack.push(new Integer(result));
        }
        else {
            stack.push(new Integer(Integer.parseInt(sign)));
        }   

        }

        return result;
    }
}
    
enum Token{
    //definindo operações de tokenizáveis bem simples, só as quatro operações básicas e números inteiros

    TK_SUM ("\\+"),
    TK_SUB ("-"),
    TK_MUL ("\\*"),
    TK_DIV ("/"),
    INTEGER ("\\d+");
    private final Pattern pattern;

    Token(String regex) {
        pattern = Pattern.compile("^" + regex);
    }
    
    int endOfMatch(String s){
        Matcher m = pattern.matcher(s);

        if(m.find()){
            return m.end();
        }
        return -1;
    }


}
class Lexer {
    private StringBuilder input = new StringBuilder();
    private Token token;
    private String lexema;
    private boolean exausthed = false;
    private String errorMessage = "";
    private Set<Character> blankChars = new HashSet<Character>();

    public Lexer(String expression) {
        //change file path to added string block
        // try (Stream<String> st = Files.lines(Paths.get(filePath))) {
        //     st.forEach(input::append);
        // } catch (IOException ex) {
        //     exausthed = true;
        //     errorMessage = "Could not read file: " + filePath;
        //     return;
        // }



        blankChars.add('\r');
        blankChars.add('\n');
        blankChars.add((char) 8);
        blankChars.add((char) 9);
        blankChars.add((char) 11);
        blankChars.add((char) 12);
        blankChars.add((char) 32);

        moveAhead();
    }

    public void moveAhead() {
        if (exausthed) {
            return;
        }

        if (input.length() == 0) {
            exausthed = true;
            return;
        }

        ignoreWhiteSpaces();

        if (findNextToken()) {
            return;
        }

        exausthed = true;

        if (input.length() > 0) {
            errorMessage = "Unexpected symbol: '" + input.charAt(0) + "'";
        }
    }

    private void ignoreWhiteSpaces() {
        int charsToDelete = 0;

        while (blankChars.contains(input.charAt(charsToDelete))) {
            charsToDelete++;
        }

        if (charsToDelete > 0) {
            input.delete(0, charsToDelete);
        }
    }

    private boolean findNextToken() {
        for (Token t : Token.values()) {
            int end = t.endOfMatch(input.toString());

            if (end != -1) {
                token = t;
                lexema = input.substring(0, end);
                input.delete(0, end);
                return true;
            }
        }

        return false;
    }

    public Token currentToken() {
        return token;
    }

    public String currentLexema() {
        return lexema;
    }

    public boolean isSuccessful() {
        return errorMessage.isEmpty();
    }

    public String errorMessage() {
        return errorMessage;
    }

    public boolean isExausthed() {
        return exausthed;
    }
    public static void lexicalAnalyser(Lexer lexer){
        System.out.println("Lexical Analysis");
        System.out.println("-----------------");
        while (!lexer.isExausthed()) {
            System.out.printf("%-18s :  %s \n",lexer.currentLexema() , lexer.currentToken());
            lexer.moveAhead();
        }

        if (lexer.isSuccessful()) {
            System.out.println("Ok! :D");
        } else {
            System.out.println(lexer.errorMessage());
        }
    }
}



public static void main (String[]args){
    
    String operation, repeat;
    int result;

    Scanner in = new Scanner(System.in);
    RPNCalc calculator = new RPNCalc();

    do{

        calculator.clearStack();
        System.out.println("ENTER RPN EXPRESSION SUCH AS  '1 2 +'");
        
        operation = in.nextLine();
        Lexer l = new Lexer(operation);
        lexicalAnalyser(l);

        result = calculator.evaluateExpression(operation);
        System.out.println(" \n EQUALS: " + result);

        System.out.println("ANOTHER? [Y/N]");
        repeat = in.nextLine();

    } while (repeat.equalsIgnoreCase("y"));

}
