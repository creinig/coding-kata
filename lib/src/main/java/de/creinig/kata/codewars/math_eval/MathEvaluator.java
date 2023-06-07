package de.creinig.kata.codewars.math_eval;

import java.util.ArrayList;
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

    return rootNode.eval();
  }


  private List<Token> tokenize(String expression) {
      List<Token> tokens = new ArrayList<>();

      int index = 0;
      while(!eof(expression, index)) {
          index = skipWhitespace(expression, index);

          if(!eof(expression, index)) {
            trace("  tokenize@%d", index);
            Token token = nextToken(expression, index);
            index += token.text.length();
            tokens.add(token);
            trace("   found %s", token);
          }
      }

      return tokens;
  }

  private Token nextToken(String expression, int startIndex) {
      char current = expression.charAt(startIndex);
      if(current == '('){
          return Token.of(TokenType.GROUP_START, "(");
      }else if(current == ')') {
          return Token.of(TokenType.GROUP_END, ")");
      } else if (isLiteralChar(current)) {
          return Token.of(TokenType.LITERAL, readLiteral(expression, startIndex));
      } else {
          return Token.of(TokenType.OPERATION, ""+ current);
      }
  }

  private String readLiteral(String expression, int startIndex) {
      int index = startIndex;
      while(!eof(expression, index) && isLiteralChar(expression.charAt(index))) {
          index++;
      }

      return expression.substring(startIndex, index);
  }

  private boolean isLiteralChar(char character) {
      return ((character >= '0') && (character <= '9')) || (character == '.');
  }

  private int skipWhitespace(String expression, int startIndex) {
      while(!eof(expression, startIndex) && (expression.charAt(startIndex) == ' ')) {
          startIndex++;
      }

      return startIndex;
  }

  private boolean eof(String expression, int index) {
      return index >= expression.length();
  }

  private static void trace(String format, Object ... args) {
      System.out.println(String.format(format, args));
  }

  private static enum TokenType {
      LITERAL,
      GROUP_START,
      GROUP_END,
      OPERATION;
  }

  /** A single lexer token */
  private static class Token {
      public final TokenType type;
      public final String text;

      public static Token of(TokenType type, String text) {
         return new Token(type, text);
      }

      private Token(TokenType type, String text) {
          this.type = type;
          this.text = text;
      }

      public String toString() {
          return type.name() + ":<" + text + ">";
      }
  }

  private static class Parser {
      private final List<Token> tokens;
      private int nextPosition = 0;
      private Node lval = null;

      public Parser(List<Token> tokens) {
          this.tokens = tokens;
      }

      public Node parse() {
          Token current = tokens.get(nextPosition);
          switch(current.type) {
              case GROUP_START:
                  trace("  parsed GROUP_START@%d", nextPosition);
                  nextPosition++;
                  return parse();
              case OPERATION:
                  trace("  parsed Negation@%d", nextPosition);
                  if("-".equals(current.text)) {
                      nextPosition++;
                      return new Negation(parse());
                  } else {
                      throw new IllegalArgumentException("Grammar: Unsupported token #" + nextPosition + ": " + current + " at this position");
                  }
              case LITERAL:
                  trace("  parsed Literal@%d", nextPosition);
                  nextPosition++;
                  return new Literal(Double.parseDouble(current.text));
          }
        return null;
      }
  }

  private static abstract class Node {
    public abstract double eval();
  }

  private static class Literal extends Node {
      private double value;

      public Literal(double value) {
          this.value = value;
      }

      public double eval() {
          return this.value;
      }
  }

  private static class Negation extends Node {
      private final Node target;

      public Negation(Node target) {
          this.target = target;
      }

      public double eval() {
          return -target.eval();
      }
  }

  private static abstract class BinaryOperation extends Node {
      protected final Node left;
      protected final Node right;

      public BinaryOperation(Node left, Node right) {
          this.left = left;
          this.right = right;
      }
  }

  private static class Addition extends BinaryOperation {
      public Addition(Node left, Node right) {
          super(left, right);
      }

      public double eval() {
          return left.eval() + right.eval();
      }
  }

  private static class Subtraction extends BinaryOperation {
      public Subtraction(Node left, Node right) {
          super(left, right);
      }

      public double eval() {
          return left.eval() - right.eval();
      }
  }

  private static class Multiplication extends BinaryOperation {
      public Multiplication(Node left, Node right) {
          super(left, right);
      }

      public double eval() {
          return left.eval() * right.eval();
      }
  }

  private static class Division extends BinaryOperation {
      public Division(Node left, Node right) {
          super(left, right);
      }

      public double eval() {
          return left.eval() / right.eval();
      }
  }

}
