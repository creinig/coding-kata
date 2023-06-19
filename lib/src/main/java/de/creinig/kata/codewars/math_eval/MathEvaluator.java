package de.creinig.kata.codewars.math_eval;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * https://www.codewars.com/kata/52a78825cdfc2cfc87000005/train/java
 */
public class MathEvaluator {

  public double calculate(String expression) {
    trace("Evaluating <%s>", expression);
    List<Token> tokens = tokenize(expression);

    Parser parser = new Parser(tokens);
    Node rootNode = parser.parse();

    try {
      return rootNode.eval();
    }
    catch (RuntimeException e) {
      e.printStackTrace(System.out);
      throw e;
    }
  }

  private List<Token> tokenize(String expression) {
    List<Token> tokens = new ArrayList<>();

    int index = 0;
    while (!eof(expression, index)) {
      index = skipWhitespace(expression, index);

      if (!eof(expression, index)) {
        trace("  tokenize@%d", index);
        Token token = nextToken(expression, index);
        index += token.text.length();
        tokens.add(token.withIndex(tokens.size()));
        trace("   found %s", token);
      }
    }

    return tokens;
  }

  private Token nextToken(String expression, int startIndex) {
    char current = expression.charAt(startIndex);
    if (current == '(') {
      return Token.of(TokenType.GROUP_START, "(");
    }
    else if (current == ')') {
      return Token.of(TokenType.GROUP_END, ")");
    }
    else if (isLiteralChar(current)) {
      return Token.of(TokenType.LITERAL, readLiteral(expression, startIndex));
    }
    else {
      return Token.of(TokenType.OPERATION, "" + current);
    }
  }

  private String readLiteral(String expression, int startIndex) {
    int index = startIndex;
    while (!eof(expression, index) && isLiteralChar(expression.charAt(index))) {
      index++;
    }

    return expression.substring(startIndex, index);
  }

  private boolean isLiteralChar(char character) {
    return ((character >= '0') && (character <= '9')) || (character == '.');
  }

  private int skipWhitespace(String expression, int startIndex) {
    while (!eof(expression, startIndex) && (expression.charAt(startIndex) == ' ')) {
      startIndex++;
    }

    return startIndex;
  }

  private boolean eof(String expression, int index) {
    return index >= expression.length();
  }

  private static void trace(String format, Object... args) {
    System.out.println(String.format(format, args));
  }

  private static enum TokenType {
    LITERAL, GROUP_START, GROUP_END, OPERATION;
  }

  /**
   * A single lexer token
   */
  private static class Token {
    public final TokenType type;
    public final String text;
    public int tokenIndex;

    public static Token of(TokenType type, String text) {
      return new Token(type, text);
    }

    private Token(TokenType type, String text) {
      this.type = type;
      this.text = text;
    }

    public Token withIndex(int index) {
      this.tokenIndex = index;
      return this;
    }

    public String toString() {
      return type.name() + ":<" + text + ">";
    }
  }

  private static class Parser {
    private final List<Token> tokens;
    private int nextPosition = 0;

    private Deque<Token> previousTokens = new ArrayDeque<>();
    private Deque<Node> values = new ArrayDeque<>();
    private Deque<Token> operations = new ArrayDeque<>();

    public Parser(List<Token> tokens) {
      this.tokens = tokens;
    }

    public Node parse() {
      for (Token token : tokens) {
        switch (token.type) {
        case GROUP_START -> operations.push(token);
        case LITERAL -> values.push(new Literal(Double.parseDouble(token.text)));
        case OPERATION -> operations.push(token);
        case GROUP_END -> values.push(processGroup());
        }
      }

      return processGroup();
    }

    private Node processGroup() {
      if (operations.isEmpty()) {
        // Fragment "123"
        assert (values.size() == 1);
        return values.pop();
      }
      else if (operations.peek().type == TokenType.GROUP_START) {
        // Fragment "(123)"
        operations.pop();
        return values.pop();
      }

      while (!operations.isEmpty() && (operations.peek().type != TokenType.GROUP_START)) {
        Token op = operations.pop();
        if (isUnary(op)) {
          values.push(new Negation(values.pop()));
        }
        else {
          Node rvalue = values.pop();
          Node lvalue = values.pop();
          values.push(switch (op.text) {
            case "-" -> new Subtraction(lvalue, rvalue);
            case "+" -> new Addition(lvalue, rvalue);
            case "*" -> new Multiplication(lvalue, rvalue);
            case "/" -> new Division(lvalue, rvalue);
            default -> null;
          });
        }
      }

      if (!operations.isEmpty()) {
        operations.pop();
      }
      return values.pop();
    }

    private boolean isUnary(Token op) {
      return switch (op.text) {
        case "-", "+" ->
            (op.tokenIndex == 0) || (tokens.get(op.tokenIndex - 1).type == TokenType.GROUP_START) || (tokens.get(
                op.tokenIndex - 1).type == TokenType.OPERATION);
        default -> false;
      };
    }
  }

  private interface Node {
    double eval();
  }

  private record Literal(double value) implements Node {
    public double eval() {
      return this.value;
    }
  }

  private record Negation(Node target) implements Node {
    public double eval() {
      return -target.eval();
    }
  }

  private record Addition(Node left, Node right) implements Node {
    public double eval() {
      return left.eval() + right.eval();
    }
  }

  private record Subtraction(Node left, Node right) implements Node {
    public double eval() {
      return left.eval() - right.eval();
    }
  }

  private record Multiplication(Node left, Node right) implements Node {
    public double eval() {
      return left.eval() * right.eval();
    }
  }

  private record Division(Node left, Node right) implements Node {
    public double eval() {
      return left.eval() / right.eval();
    }
  }
}
