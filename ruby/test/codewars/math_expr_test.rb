require 'bundler/setup'
require 'minitest'

require 'codewars/math_expr'

class MathExpressionTest < Minitest::Test
  def test_standards
    tests = [
      ['1+1', 2],
      ['1 - 1', 0],
      ['1* 1', 1],
      ['1 /1', 1],
      ['-123', -123],
      ['123', 123],
      ['2 /2+3 * 4.75- -6', 21.25],
      ['12* 123', 1476],
      ['2 / (2 + 3) * 4.33 - -6', 7.732]
    ]

    tests.each do |pair|
      assert_equal pair[1], calc(pair[0])
    end
  end
end

