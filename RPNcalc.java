import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.lang.Character.*;


public class RPNCalc {
	
    public Stack<Integer> stack;
    public ArrayList<String> ids;
    public ArrayList<String> values;

        public RPNCalc(){
            stack = new Stack<Integer>(); //new stack in constructor
            ids = new ArrayList<String>(); 
            values = new ArrayList<String>();
            //ao inves de um hashmap, deixei dois arraylists e só depois vi, mas teria problema? Caso sim, posso refazer :D
        }


    public void clearStack(){
        stack.clear();
    }
    
    public boolean isOperator(String token){
    	
        return ( token.equals("+") || token.equals("-") ||
                token.equals("*") || token.equals("/") || token.equals("%"));

    }
    
    public int evaluateOperation(char operation, int a, int b){
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
    	//feito de forma a aceitar qq notação de entrada (além do rpn). É de boas deixar assim ou precisaria fixar a notação?
    	//caso necessário, posso deixar o método implementado de forma ordenada, com um string split e ordenado pela ordem das entradas
    	//fiz assim por achar essa forma mais fail-proof
    	
        int a, b, result = 0;
        String sign;
        Scanner parse = new Scanner(operation);

        while(parse.hasNext()){
	        sign = parse.next();
	//        System.out.println("sign: "+sign);
	        if (isOperator(sign)){
	            b = (stack.pop()).intValue();
	            a = (stack.pop()).intValue();
	            result = evaluateOperation(sign.charAt(0),a,b);
	            stack.push(new Integer(result));
	        }
	        else {
	        	//trata a identificação. A priori, assume um número como é o default da calculadora.
	        	boolean isNumber = true;
	        	
	        	//checa a String, se cada char for numérico, confirmo que é um número e trato como tal.
	        	//Se não, assumo que é uma variável e tratarei como tal.
	        	for(int i = 0; i< sign.length();i++) {
	        		char c = sign.charAt(0);
	        		if (!Character.isDigit(c)) {
	        			isNumber = false;
	        		}
	        	}
	        	
	        	//se for um número, trata normal
	        	if (isNumber) {
	        		stack.push(new Integer(Integer.parseInt(sign)));
	        	} else {
	        		//um if sign = variavel, checa se ela está definida, joga returnNumber e mantém o bonde andando
	        		if(ids.contains(sign)) {
	            		sign = returnNumber(sign);
	            		stack.push(new Integer(Integer.parseInt(sign)));
	        		} else {
	        			throw new calcError("undefinede variable: " + sign);
	        		}
	        	}        	
	            
	        }   

        }

        return result;
    }
    
    //assumo que a variável identificada fica guardada na memória interna da calculadora e, por isso, está nesta classe. 
    public void addVariable(String operation) {
    	String [] result = operation.split("=");
//		System.out.println(Arrays.toString(result));
    	if(ids.isEmpty()||!ids.contains(result[0])) {
    		ids.add(result[0]);
    		values.add(result[1]);
//    		System.out.println("Ids, values: "+ids.get(0)+"/"+values.get(0));
    	} else {
    		int index = ids.indexOf(result[0]);
    		values.set(index, result[1]);
    	}
    	
    	return;
    }
    
    public String returnNumber (String caracter) {
    	//retorna o número associado a variável, em formato string que a calculadora processa
    	String number = "";
    	if(ids.contains(caracter)) {
    		int index = ids.indexOf(caracter);
    		if(!(values.size()<index)) {
    			number = values.get(index);
    		}
    	}
    	
    	return number;
    }
    

	public static void main (String[]args){
	
	    String op1, op2, sign, operation, selector, repeat;
	    int result;
	
	    Scanner in = new Scanner(System.in);
	    RPNCalc calculator = new RPNCalc(); //depois trocar isso
	
	    do{
	
	        calculator.clearStack();
	       
	        do {
	        	 System.out.println("ENTER RPN EXPRESSION (A) OR ATTRIBUTE A VALUE (B)? [A/B]");
	             selector = in.nextLine();
	             
	        	 if (selector.equalsIgnoreCase("a")) {
	        		 
	        		 System.out.println("RPN");
	        		 
		             op1 = in.nextLine();
		             op2 = in.nextLine();
		             sign = in.nextLine();
		             operation = op1+" "+op2+" "+sign;
	                 
	                 Lexer l = new Lexer(operation);               
	                 l.lexicalAnalyser(l);
	                 
//	                 AnLex al = new AnLex(operation);
//	                 al.toList();
//	
	                 result = calculator.evaluateExpression(operation);
	                 System.out.println(" \n EQUALS: " + result);
	                 
	             } else if (selector.equalsIgnoreCase("b")) {
	            	
	            	 System.out.println("Identifier");
	            	 operation = in.nextLine();
	            	 //se pa colocar isso aqui dentro do analisador léxicos
	            	 if(operation.contains("=")) {
	             		calculator.addVariable(operation);
	             	 }
	            	 
	            	 Lexer l = new Lexer(operation);
	            	 l.lexicalAnalyser(l);
	            	 
	           
	             } else {
	            	 System.out.println(" \n AGAIN ");
	
	             }
	        	 
	        } while (!selector.equalsIgnoreCase("a") || !selector.equalsIgnoreCase("b"));    
	
	        System.out.println("ANOTHER? [Y/N]");
	        repeat = in.nextLine();
	
	    } while (repeat.equalsIgnoreCase("y"));
	
	}
}

class calcError extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public calcError(String msg) {
		super(msg);
	}
}

//Analisador Léxico: Implementei um analisador léxico similar ao do professor no intuito de um double check e ver se meu próprio analisador estava perfomando de forma satisfatória e, até agora, pareceu que sim.

enum Token{
    //definindo operações de tokenizáveis bem simples, só as quatro operações básicas e números inteiros
	//adicionei poucas operações apenas para ver se o analisador léxico conseguiria testar os mesmos casos da RPNCalc feita anteriormente
	//reconheço que poderia ser mais completo, mas, por enquanto, é o que consegui testar pelo prazo. 

    TK_SUM ("\\+"),
    TK_SUB ("-"),
    TK_MUL ("\\*"),
    TK_DIV ("/"),
    INTEGER ("\\d+"),
    EOF ("\t\n\r\f "),
	CHAR ("\\w");
	
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
//    	System.out.println("moving ahead");
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
            System.out.println("end: "+ end);

            if (end != -1) {
                token = t;
                System.out.println("token: "+token);
                lexema = input.substring(0, end);
                input.delete(0, end);
                return true;
            }
            System.out.println("Lexema: "+lexema);
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
    
//    public void tire() {
//    	this.exausthed = false;
//    }
//    public void move() {
//    	this.exausthed = true;
//    }
    
    public static void lexicalAnalyser(Lexer lexer){
        System.out.println("Lexical Analysis");
        System.out.println("-----------------");
        while (!lexer.isExausthed()) {
            System.out.printf(lexer.currentLexema() , lexer.currentToken());
            lexer.moveAhead();
        }

        if (lexer.isSuccessful()) {
            System.out.println("Ok! :D");
        } else {
            System.out.println(lexer.errorMessage());
        }
    }
}


//Analisador-presente implementado

class Evaluator{
	
	private static final String NUM_REGEX = "(\\d)+"; // short for [0-9]
	private static final String OP_REGEX = "(\\+|-|\\*|/)"; // recognizes as an operation
	private static final String PLUS_REGEX = "(\\+)"; // for plus operation recognition
	private static final String MINUS_REGEX = "(\\-)"; // for minus operation recognition
	private static final String SLASH_REGEX = "(/)"; // for div operation recognition
	private static final String STAR_REGEX = "(\\*)"; // for mult operation recognition
	private static final String CHAR_REGEX = "(\\w)"; //short for word
	
	public static boolean isNum(String expression) {
		return expression.matches(NUM_REGEX);
	}
	
	public static boolean isWord(String expression) {
		return expression.matches(CHAR_REGEX);
	}
	
	public static boolean isOP(String expression) {
		return expression.matches(OP_REGEX);
	}
	
	public static boolean isPlus(String expression) {
		return expression.matches(PLUS_REGEX);
	}
	
	public static boolean isMinus(String expression) {
		return expression.matches(MINUS_REGEX);
	}
	
	public static boolean isSlash(String expression) {
		return expression.matches(SLASH_REGEX);
	}
	
	public static boolean isStar(String expression) {
		return expression.matches(STAR_REGEX);
	}
	
	public static Token getOPTokenType(String expression) {
		Token tokenType = null;
		if(isPlus(expression)) {
			tokenType = Token.TK_SUM;
		}
		else if(isMinus(expression)) {
			tokenType =  Token.TK_SUB;
		}
		else if(isSlash(expression)) {
			tokenType =  Token.TK_MUL;
		}
		else if(isStar(expression)) {
			tokenType = Token.TK_DIV;
		}
		
		return tokenType;
	}
	
}

class Tokenizer{
	public static final String TOKENIZER_DELIMITER = "\t\n\r\f ";

	public final Token type; // token type
	public final String expr; // token value

	public Tokenizer (Token type, String value) {
		this.type = type;
		this.expr = value;
	}
	public String toString() {
		return "Token [type=" + this.type + ", lexeme=" + this.expr + "]";
	}
}

class AnLex {
	//analisador léxico
	
	private final String expression;
	private final List<Tokenizer> tokens = new ArrayList<>();

	public AnLex(String expression) {
		this.expression = expression;
	}
	
	public List<Tokenizer> toList(){
		return this.toList(this.expression);
	}

	
	public List<Tokenizer> toList(String expression){
		StringTokenizer tokenizer = new StringTokenizer(expression, Tokenizer.TOKENIZER_DELIMITER); 
		// processing each tokenized word
		while (tokenizer.hasMoreElements()) {
			String tokenStr = tokenizer.nextToken();
			this.tokens.add(this.getToken(tokenStr));
		}
		this.tokens.add(new Tokenizer(Token.EOF, "")); // EOF
		
		return this.tokens;
	}
	
	private Tokenizer getToken(String expr) {
		Tokenizer ret = null;
		if(Evaluator.isNum(expr)) {
			ret = new Tokenizer(Token.INTEGER, expr);
		}
		else if(Evaluator.isOP(expr)) {
			ret = new Tokenizer(Evaluator.getOPTokenType(expr), expr);
		}
		else {
			throw new LexError("Unexpected character: "+expr);
		}
		return ret;
	}
	
}

class LexError extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public LexError(String msg) {
		super(msg);
	}
}

//dentre muitas das dúvidas, uma das que mais tive foi sobre a implementação parcial, pois como não houve correções parciais, não sei se estava no caminho certo
//e como quis aproveitar o código já feito, não busquei refazer a rpn calc de acordo com o modelo parser-lexer que está no programa do professor
//este programa faz basciamente a mesma coisa, de forma análoga, mas organizado de forma bastante diferente e, por isso minha dúvida.

