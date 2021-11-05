import java.util.*;

public class RPNCalc {
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

    public static void main (String[]args){
        
        String operation, repeat;
        int result;

        Scanner in = new Scanner(System.in);
        RPNCalc calculator = new RPNCalc();

        do{

            calculator.clearStack();
            System.out.println("ENTER RPN EXPRESSION SUCH AS  '1 2 +'");
            operation = in.nextLine();

            result = calculator.evaluateExpression(operation);
            System.out.println(" \n EQUALS: " + result);

            System.out.println("ANOTHER? [Y/N]");
            repeat = in.nextLine();

        } while (repeat.equalsIgnoreCase("y"));

    }
}