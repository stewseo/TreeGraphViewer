package ExpressionTree;

import java.util.Stack;

public class Precedence {

    public static int prec(char c) {
        if (c == '*' || c == '/') {
            return 3;
        }

        if (c == '+' || c == '-') {
            return 4;
        }
        if (c == '~') {
            return 7;
        }

        if (c == '&') {
            return 8;
        }

        if (c == '^') {
            return 9;
        }

        if (c == '|') {
            return 10;
        }

        return Integer.MAX_VALUE;
    }
    public static boolean isOperand(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }


    public String infixToPostfix(String infix) {
        StringBuilder result = new StringBuilder(new String(""));
        // base case
        if (infix == null || infix.length() == 0) {
            return infix;
        }

        // create an empty stack for storing operators
        Stack<Character> s = new Stack<>();

        // create a string to store the postfix expression
        StringBuilder postfix = new StringBuilder();

        // process the infix expression from left to right
        for (char c: infix.toCharArray()) {
            // Case 1. If the current token is an opening bracket '(', push it into the stack
            if (c == '(') {
                s.push(c);
            }
            // Case 2. If the current token is a closing bracket ')'
            else if (c == ')') {
                // pop tokens from the stack until the corresponding opening bracket '(' is removed.
                // Append each operator at the end of the postfix expression
                while (s.peek() != '(') {
                    postfix.append(s.pop());
                }
                s.pop();
            }

            // Case 3. If the current token is an operand, append it at the end of the postfix expression
            else if (isOperand(c)) {
                postfix.append(c);
            }

            // Case 4. If the current token is an operator
            else {
                // remove operators from the stack with higher or equal precedence
                // and append them at the end of the postfix expression
                while (!s.isEmpty() && prec(c) >= prec(s.peek())) {
                    postfix.append(s.pop());
                }
                // push the current operator on top of the stack
                s.push(c);
            }
        }

        // append any remaining operators to the postfix expression
        while (!s.isEmpty()) {
            postfix.append(s.pop());
        }

        // return the postfix expression
        return postfix.toString();
    }
    public int calculate(String s) {
        if (s.length() == 0) {
            return 0;
        }
        int[] index = {0};

        return calculate(s, index);
    }

    //https://leetcode.com/problems/basic-calculator/
    private int calculate(String s, int[] currentIndex) {
        int currentNum = 0;
        int sum = 0;
        int sign = 1;
        while(currentIndex[0] < s.length()) {
            char c = s.charAt(currentIndex[0]);
            switch (c) {
                case ' ' -> currentIndex[0]++;
                case '+' -> {
                    sum += currentNum * sign;
                    currentNum = 0;
                    sign = 1;
                    currentIndex[0]++;
                }
                case '-' -> {
                    sum += currentNum * sign;
                    currentNum = 0;
                    sign = -1;
                    currentIndex[0]++;
                }
                case '(' -> {
                    currentIndex[0]++;
                    sum += calculate(s, currentIndex) * sign;
                }
                case ')' -> {
                    sum += currentNum * sign;
                    currentIndex[0]++;
                    return sum;
                }
                default -> {
                    currentNum = currentNum * 10 + c - '0';
                    currentIndex[0]++;
                }
            }
        }
        sum += currentNum * sign;
        return sum;
    }
}



