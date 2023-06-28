require 'pp'

# https://www.codewars.com/kata/52a78825cdfc2cfc87000005
def calc expression
  puts "calc(#{expression})"
  token_index = -1
  tokens = expression.scan(/([()\/*+-]|\d+(\.\d+)?)/).map{|text| Token.new(text[0], token_index += 1)}

  Parser.new.evaluate(tokens)
  # evaluate `expression` and return result
end


class Parser
  def evaluate(tokens)
    @tokens = tokens
    @values = []
    @ops = []

    @tokens.each do |token|
      case token.type
      when :group_start then @ops << token
      when :literal then @values << Float(token.text)
      when :operator then handle_op(token)
      when :group_end then @values << handle_group()
      else fail "Unknown token type <#{token.type}>"
      end
    end

    handle_group()
  end

  def handle_op(op)
    until @ops.empty? or unary? op or (prio(@ops.last) < prio(op))
      @values << handle_expr()
    end
    @ops << op
  end

  def handle_group
    value = handle_expr()
    @ops.pop unless @ops.empty?
    value
  end

  def handle_expr
    if @ops.empty?
      # Fragment "123"
      raise "value stack > 1 for literal" unless @values.length == 1
      return @values.pop
    elsif @ops.last.type == :group_start
      # Fragment "(123)"
      return @values.pop
    end

    until @ops.empty? or (@ops.last.type == :group_start)
      op = @ops.pop

      if unary? op and op.text == "-"
        # Negation
        puts "  -#{@values.last}"
        @values << - @values.pop
      else
        rval = @values.pop
        lval = @values.pop
        puts "  #{lval} #{op.text} #{rval}"
        @values.push binary_op(op, lval, rval)
      end
    end

    @values.pop
  end

  def binary_op(op, lval, rval)
    case op.text
    when "+" then lval + rval
    when "-" then lval - rval
    when "*" then lval * rval
    when "/" then lval / rval
    end
  end

  def unary?(op)
    return false unless %w{+ -}.include? op.text

    # may be unary, but only if there's no literal / ")" before it
    return true if op.position == 0

    prev_type = @tokens[op.position - 1].type
    return prev_type != :group_end && prev_type != :literal
  end

  def prio(op)
    case op.text
    when "*", "/" then 2
    when "+", "-" then 1
    else 0
    end
  end
end


class Token
  attr_reader :type, :text, :position

  def initialize(text, position)
    @text = text.strip
    @position = position
    @type = case @text
            when /^\d+(\.\d+)?$/ then :literal
            when /^[*\/+-]$/ then :operator
            when "(" then :group_start
            when ")" then :group_end
            end
  end
end

