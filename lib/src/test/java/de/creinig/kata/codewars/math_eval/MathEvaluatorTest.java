package de.creinig.kata.codewars.math_eval;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MathEvaluatorTest {
  private void eval(String expression, double expected) {
    assertEquals(expected, new MathEvaluator().calculate(expression), 0.01);
  }

  @Test
  public void literals() {
    eval("2", 2d);
    eval("2.000", 2d);
    eval("000", 0d);
    eval("1234.56", 1234.56);
  }

  @Test
  public void literalsWithSpace() {
    eval("2 ", 2d);
    eval("   2.000", 2d);
    eval("  000  ", 0d);
    eval("  1234.56  ", 1234.56);
  }

  @Test
  public void literalsInGroups() {
    eval("(2)", 2d);
    eval("( ( (3)))", 3d);
  }

  @Test
  public void realMultiplication() {
    eval("3*5", 15d);
    eval("3.5*4", 14d);
    eval("-3*5", -15d);
    eval("-3*-5", 15d);
  }

  @Test
  public void realDivision() {
    eval("12/4", 3d);
    eval("15/2", 7.5d);
  }

  @Test public void testAddition() {
    assertEquals(2d, new MathEvaluator().calculate("1+1"), 0.01);
  }
  
  @Test public void testSubtraction() {
    assertEquals(0d, new MathEvaluator().calculate("1 - 1"), 0.01);
  }

  @Test public void testMultiplication() {
    assertEquals(1d, new MathEvaluator().calculate("1* 1"), 0.01);
  }

  @Test public void testDivision() {
    assertEquals(1d, new MathEvaluator().calculate("1 /1"), 0.01);
  }

  @Test public void testNegative() {
    assertEquals(-123d, new MathEvaluator().calculate("-123"), 0.01);
  }

  @Test public void testLiteral() {
    assertEquals(123d, new MathEvaluator().calculate("123"), 0.01);
  }

  @Test public void testExpression() {
    assertEquals(21.25, new MathEvaluator().calculate("2 /2+3 * 4.75- -6"), 0.01);
  }

  @Test public void testSimple() {
    assertEquals(1476d, new MathEvaluator().calculate("12* 123"), 0.01);
  }

  @Test public void testComplex() {
    assertEquals(7.732, new MathEvaluator().calculate("2 / (2 + 3) * 4.33 - -6"), 0.01);
  }
}
