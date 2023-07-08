require 'bundler/setup'
require 'minitest/autorun'

require 'codewars/math_expr'

describe :calc do
    [
      ['1+1', 2],
      ['1 - 1', 0],
      ['1* 1', 1],
      ['1 /1', 1],
      ['-123', -123],
      ['123', 123],
      ['2 /2+3 * 4.75- -6', 21.25],
      ['12* 123', 1476],
      ['2 / (2 + 3) * 4.33 - -6', 7.732]
    ].each do |expr, expected|
      describe "when called with #{expr}" do
        it "must return #{expected}" do
          _(calc(expr)).must_equal expected
        end
      end
    end
end
