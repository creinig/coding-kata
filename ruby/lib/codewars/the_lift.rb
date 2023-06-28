
# https://www.codewars.com/kata/58905bfa1decb981da00009e
def the_lift(queues, capacity)
  puts ">> Testing: #{queues.inspect}, #{capacity}"
  visited = [0] # We start at ground floor

  lift = Lift.new(queues, capacity)
  while (floor = lift.move(false))
    visited << floor
  end

  visited << 0 unless visited.last == 0 # return to ground at end

  puts "Movement: #{visited.inspect}"
  visited
end


class Lift
  def initialize(queues, capacity)
    @queues = [] + queues # shallow copy
    @capacity = capacity
    @passengers = []
    @current_floor = 0
    @top_floor = queues.length - 1
    @moving_up = true
  end

  # Move the lift to the next floor and handle movement of people into / out of it.
  # Returns the floor number the lift moved to. Negative if no movement is required anymore
  def move(final)
    puts "  @ floor #{@current_floor}, final=#{final}, state = #{self.inspect}"
    move_out
    move_in

    # look for the next floor in the current direction where we can
    # (a) unload passengers or (b) load passengers going in the same direction
    floor = next_floor(true)

    unless floor
      # next try: look for the best floor in the current direction to pick up passengers going in the opposite direction
      floor = next_floor(false)
      if floor
        # move there and change direction
        @moving_up = !@moving_up
      else
        # next try: immediately change direction & retry once
        @moving_up = !@moving_up
        return final ? nil : move(true)
      end
    end

    puts "   Moving #{@moving_up ? 'up' : 'down'} to floor #{floor}, passengers = #{@passengers}"

    @current_floor = floor if floor
    floor
  end

  def move_out
    staying = @passengers.select{|dest| dest != @current_floor }

    puts "   Floor #{@current_floor}: #{@passengers.length - staying.length} passengers leaving" unless staying == @passengers

    @passengers = staying
  end

  def move_in
    queue = @queues[@current_floor]
    remaining = []

    queue.each do |passenger|
      if direction_matches?(passenger) && (@passengers.length < @capacity)
        @passengers << passenger
      else
        remaining << passenger
      end
    end

    unless queue == remaining
      puts "   Floor #{@current_floor}: #{queue.length - remaining.length} passengers entering, remaining: #{remaining.inspect}"
    end

    @queues[@current_floor] = remaining
  end

  def direction_matches?(passenger)
    @moving_up && passenger > @current_floor or !@moving_up && passenger < @current_floor
  end

  def next_floor(for_same_direction)
    @moving_up ? scan_up(for_same_direction) : scan_down(for_same_direction)
  end

  def has_waiting_at(floor, moving_up)
    @queues[floor].find{|f| moving_up ? f > floor : f < floor }
  end

  def scan_up(for_same_direction)
    scan((@current_floor + 1).upto(@top_floor), for_same_direction)
  end

  def scan_down(for_same_direction)
    scan((@current_floor - 1).downto(0), for_same_direction)
  end

  def scan(floor_range, for_same_direction)
    # already at end => end of movement in this direction
    return nil if floor_range.size == 0

    if for_same_direction
      # any floor with passengers to drop off at or with someone waiting to go further in this direction?
      return floor_range.find{|f| @passengers.include?(f) || has_waiting_at(f, @moving_up)}
    else
      # by this point we have no passengers and nobody waiting to go further in the original direction
      # => we look for the most distant floor where someone wants to go up
      return floor_range.find_all{|f| not @queues[f].empty?}.last
    end
  end

end

