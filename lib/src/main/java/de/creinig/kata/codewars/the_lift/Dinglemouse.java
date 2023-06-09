package de.creinig.kata.codewars.the_lift;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * https://www.codewars.com/kata/58905bfa1decb981da00009e
 */
public class Dinglemouse {

  public static int[] theLift(final int[][] queues, final int capacity) {
    Lift lift = new Lift(queues, capacity);
    List<Integer> visitedFloors = new ArrayList<>();
    // we start out at ground floor
    visitedFloors.add(0);

    int floor = lift.move();
    while (canMoveTo(floor)) {
      visitedFloors.add(floor);
      floor = lift.move();
    }

    if (visitedFloors.get(visitedFloors.size() - 1) != 0) {
      visitedFloors.add(0); // return to ground at the end
    }

    p("Movement: %s", visitedFloors);
    return toArray(visitedFloors);
  }

  /**
   * The Lift including its current state
   */
  private static class Lift {
    private final List<List<Integer>> queues;
    private final int capacity;
    private final int topFloor;
    private boolean movingUp = true;
    private int currentFloor;
    private List<Integer> passengers = List.of();

    public Lift(final int[][] queues, final int capacity) {
      this.queues = new ArrayList<>(queues.length);
      for (int[] queue : queues) {
        this.queues.add(toList(queue));
      }

      this.capacity = capacity;
      this.currentFloor = 0;
      this.topFloor = queues.length - 1;

      p("Running with queues: %s, capacity %d", this.queues, this.capacity);
    }

    /**
     * Move the lift to the next floor and handle movement of people into / out of the lift.
     *
     * @return the floor number the lift moved to. Negative if no movement is required anymore.
     */
    public int move() {
      return move(false);
    }

    private int move(boolean finalTry) {
      movePassengersOut();
      movePassengersIn();

      // look for the next floor in the current direction where we can 
      // (a) unload passengers or (b) load passengers going in the same direction
      int floor = getNextFloor(true);
      if (!canMoveTo(floor)) {
        // next try: look for the best floor in the current direction to pick up passengers going in the opposite direction 
        floor = getNextFloor(false);

        if (canMoveTo(floor)) {
          // move there and change direction
          movingUp = !movingUp;
        }
        else {
          // next try: immediately change direction & retry once
          movingUp = !movingUp;
          return finalTry ? -1 : move(true);
        }
      }

      p("Moving %s to floor %d, passengers = %s", (movingUp ? "up" : "down"), floor, passengers);

      currentFloor = floor;
      return currentFloor;
    }

    private void movePassengersOut() {
      List<Integer> passengersStaying = new ArrayList<>(capacity);
      int passengersLeaving = 0;
      for (Integer passenger : passengers) {
        if (passenger == currentFloor) {
          passengersLeaving++;
        }
        else {
          passengersStaying.add(passenger);
        }
      }

      if (passengersLeaving > 0) {
        p("Floor %2d: %2d passengers leaving", currentFloor, passengersLeaving);
      }

      this.passengers = passengersStaying;
    }

    private void movePassengersIn() {
      List<Integer> remainingQueue = new ArrayList<>();

      for (Integer passenger : queues.get(currentFloor)) {
        if (directionMatches(passenger) && passengers.size() < capacity) {
          passengers.add(passenger);
        }
        else {
          remainingQueue.add(passenger);
        }
      }

      int passengersEntering = queues.get(currentFloor).size() - remainingQueue.size();
      if (passengersEntering > 0) {
        p("Floor %2d: %2d passengers entering, remaining: %s", currentFloor, passengersEntering, remainingQueue);
      }

      queues.set(currentFloor, remainingQueue);
    }

    private boolean directionMatches(Integer passenger) {
      return (movingUp && (passenger > currentFloor)) || (!movingUp && (passenger < currentFloor));
    }

    private int getNextFloor(boolean forSameDirection) {
      if (movingUp) {
        return scanUp(forSameDirection);
      }
      else {
        return scanDown(forSameDirection);
      }
    }

    private boolean hasPassengersFor(int floor) {
      //p("   hasPassengersFor(%2d, %s): %s", floor, passengers, passengers.contains(floor));
      return passengers.contains(floor);
    }

    private boolean hasWaitingAt(int floor, boolean movingUp) {
      if (movingUp) {
        return queues.get(floor).stream().anyMatch(dest -> dest > floor);
      }
      else {
        return queues.get(floor).stream().anyMatch(dest -> dest < floor);
      }
    }

    private int scanUp(boolean forSameDirection) {
      return scan(topFloor, (i, edge) -> i <= edge, 1, forSameDirection);
    }

    private int scanDown(boolean forSameDirection) {
      return scan(0, (i, edge) -> i >= edge, -1, forSameDirection);
    }

    private int scan(int edgeFloor, BiPredicate<Integer, Integer> edgeDetector, int increment,
        boolean forSameDirection) {
      // already at bottom => end of movement in this direction
      if (currentFloor == edgeFloor) {
        return -1;
      }

      if (forSameDirection) {
        // any floor with passengers to drop off at or with someone waiting to go further in this direction?
        for (int i = currentFloor + increment; edgeDetector.test(i, edgeFloor); i += increment) {
          if (hasPassengersFor(i) || hasWaitingAt(i, movingUp)) {
            return i;
          }
        }

        return -1;
      }
      else {
        // by this point we have no passengers and nobody waiting to go further in the original direction
        // => we look for the most distant floor where someone wants to go up
        int mostDistantFloor = -1;
        for (int i = currentFloor + increment; edgeDetector.test(i, edgeFloor); i += increment) {
          if (!queues.get(i).isEmpty()) {
            mostDistantFloor = i;
          }
        }

        return mostDistantFloor;
      }
    }
  }

  private static boolean canMoveTo(int floor) {
    return floor >= 0;
  }

  private static void p(String format, Object... args) {
    System.out.println(String.format(format, args));
  }

  private static List<Integer> toList(int[] array) {
    List<Integer> result = new ArrayList<>(array.length);
    for (int value : array) {
      result.add(value);
    }

    return result;
  }

  private static int[] toArray(List<Integer> list) {
    int[] result = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      result[i] = list.get(i);
    }

    return result;
  }
}

