require 'bundler/setup'
require 'minitest/autorun'

require 'codewars/the_lift'

describe :the_lift do
    # Floors:    G     1      2        3     4      5      6         Answers:
    tests = [
            [ [ [],   [],    [5,5,5], [],   [],    [],    [] ], 5,  [0, 2, 5, 0]          ],
	    [ [ [],   [],    [1,1],   [],   [],    [],    [] ], 5,  [0, 2, 1, 0]          ],
	    [ [ [],   [3,],  [4,],    [],   [5,],  [],    [] ], 5,  [0, 1, 2, 3, 4, 5, 0] ],
	    [ [ [],   [0,],  [],      [],   [2,],  [3,],  [] ], 5,  [0, 5, 4, 3, 2, 1, 0] ],
            [ [ [1, 1, 1, 1, 1], [0, 0]                      ], 3,  [0, 1, 0, 1, 0]       ] ]
  
    tests.each do |queues, capacity, answer| 
      describe "when called on #{queues.inspect} with capacity #{capacity}" do
	it "must move to these floors: #{answer.inspect}" do
	  _(the_lift(queues, capacity)).must_equal answer
	end
      end
    end
end
