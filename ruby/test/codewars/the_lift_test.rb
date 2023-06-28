require 'bundler/setup'
require 'minitest'

require 'codewars/the_lift'

class TheLiftTest < Minitest::Test
  def test_standard
    # Floors:    G     1      2        3     4      5      6         Answers:
    tests = [
            [ [ [],   [],    [5,5,5], [],   [],    [],    [] ],     [0, 2, 5, 0]          ],
	    [ [ [],   [],    [1,1],   [],   [],    [],    [] ],     [0, 2, 1, 0]          ],
	    [ [ [],   [3,],  [4,],    [],   [5,],  [],    [] ],     [0, 1, 2, 3, 4, 5, 0] ],
	    [ [ [],   [0,],  [],      [],   [2,],  [3,],  [] ],     [0, 5, 4, 3, 2, 1, 0] ]]
      
    for queues, answer in tests do
      puts ">> Testing #{queues.inspect}"
      assert_equal answer, the_lift(queues, 5)
    end
  end
end
