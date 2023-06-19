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
    trace("  Tokens: %s", tokens);

    Parser parser = new Parser(tokens);
    Node rootNode = parser.parse();

    return rootNode.eval();
  }

  private List<Token> tokenize(String expression) {
    List<Token> tokens = new ArrayList<>();

    int index = 0;
    while (!eof(expression, index)) {
      index = skipWhitespace(expression, index);

      if (!eof(expression, index)) {
        Token token = nextToken(expression, index);
        index += token.text.length();
        tokens.add(token.withIndex(tokens.size()));
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
    System.out.printf((format) + "%n", args);
  }

  private enum TokenType {
    LITERAL, GROUP_START, GROUP_END, OPERATION
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
      return type.name() + ":<" + text + ">@" + tokenIndex;
    }
  }

  private static class Parser {
    private final List<Token> tokens;
    private final Deque<Node> values = new ArrayDeque<>();
    private final Deque<Token> operations = new ArrayDeque<>();

    public Parser(List<Token> tokens) {
      this.tokens = tokens;
    }

    public Node parse() {
      for (Token token : tokens) {
        switch (token.type) {
        case GROUP_START -> operations.push(token);
        case LITERAL -> values.push(new Literal(Double.parseDouble(token.text)));
        case OPERATION -> handleOperation(token);
        case GROUP_END -> values.push(handleGroup());
        }
      }

      return handleGroup();
    }

    private void handleOperation(Token op) {
      trace("  handleOp: %s", op);
      while (!operations.isEmpty() && !isUnary(op) && (priority(operations.peek()) >= priority(op))) {
        values.push(handleExpression());
      }
      operations.push(op);
    }

    private Node handleGroup() {
      Node value = handleExpression();
      if (!operations.isEmpty()) {
        operations.pop();
      }
      return value;
    }

    private Node handleExpression() {
      if (operations.isEmpty()) {
        // Fragment "123"
        trace("  handleExpr(ops == empty)");
        assert (values.size() == 1);
        return values.pop();
      }
      else if (operations.peek().type == TokenType.GROUP_START) {
        // Fragment "(123)"
        trace("  handleExpr(ops == simpleGroup)");
        return values.pop();
      }

      while (!operations.isEmpty() && (operations.peek().type != TokenType.GROUP_START)) {
        Token op = operations.pop();
        trace("  handleExpr: %s", op);
        if (isUnary(op)) {
          trace("    unary");
          values.push(new Negation(values.pop()));
        }
        else {
          Node rvalue = values.pop();
          Node lvalue = values.pop();
          trace("    binary: %s, %s", lvalue, rvalue);
          values.push(buildBinaryOp(op, lvalue, rvalue));
        }
      }
      return values.pop();
    }

    private static Node buildBinaryOp(Token op, Node lvalue, Node rvalue) {
      return switch (op.text) {
        case "-" -> new Subtraction(lvalue, rvalue);
        case "+" -> new Addition(lvalue, rvalue);
        case "*" -> new Multiplication(lvalue, rvalue);
        case "/" -> new Division(lvalue, rvalue);
        default -> null;
      };
    }

    private boolean isUnary(Token op) {
      return mayBeUnary(op) &&
          // may be unary, but only if there's no literal / ")" before it
          (op.tokenIndex == 0) || (tokens.get(op.tokenIndex - 1).type == TokenType.GROUP_START) || (tokens.get(
          op.tokenIndex - 1).type == TokenType.OPERATION);
    }

    private boolean mayBeUnary(Token op) {
      return switch (op.text) {
        case "+", "-" -> true;
        default -> false;
      };
    }

    private int priority(Token op) {
      return switch (op.text) {
        case "*", "/" -> 2;
        case "+", "-" -> 1;
        default -> 0;
      };
    }
  }

  private interface Node {
    double eval();
  }

  private record Literal(double value) implements Node {
    @Override
    public double eval() {
      return this.value;
    }
  }

  private record Negation(Node target) implements Node {
    @Override
    public double eval() {
      return -target.eval();
    }
  }

  private record Addition(Node left, Node right) implements Node {
    @Override
    public double eval() {
      return left.eval() + right.eval();
    }
  }

  private record Subtraction(Node left, Node right) implements Node {
    @Override
    public double eval() {
      return left.eval() - right.eval();
    }
  }

  private record Multiplication(Node left, Node right) implements Node {
    @Override
    public double eval() {
      return left.eval() * right.eval();
    }
  }

  private record Division(Node left, Node right) implements Node {
    @Override
    public double eval() {
      return left.eval() / right.eval();
    }
  }
}
