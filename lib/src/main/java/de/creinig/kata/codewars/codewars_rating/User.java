package de.creinig.kata.codewars.codewars_rating;

/**
 * https://www.codewars.com/kata/51fda2d95d6efda45e00004e
 */
public class User {
    public int rank = -8;
    public int progress = 0;

    public User() {
        System.out.println("-- New User() --");
    }

    public void incProgress(int activityRank) {
        System.out.println("incProgress(" + activityRank + ") on " + this.rank + ":" + this.progress);
        validateRank(activityRank);

        int activityProgress = getProgressFor(activityRank);
        System.out.println("  Progress(" + activityRank + ") = " + activityProgress);
        int newProgress = this.progress + activityProgress;

        while (newProgress >= 100) {
            newProgress -= 100;
            incRank();
        }

        if (this.rank == 8) {
            newProgress = 0;
        }

        this.progress = newProgress;
        System.out.println("  New state: " + this.rank + ":" + this.progress);
    }


    private void validateRank(int rank) {
        if ((rank < -8) || (rank > 8) || (rank == 0)) {
            throw new IllegalArgumentException("Unsupported rank " + rank);
        }
    }

    private int getProgressFor(int activityRank) {
        int rankDiff = diffRank(activityRank, this.rank);

        if (rankDiff == 0) {
            return 3;
        } else if (rankDiff == -1) {
            return 1;
        } else if (rankDiff <= -2) {
            return 0;
        } else {
            return (10 * rankDiff * rankDiff);
        }
    }

    private int diffRank(int activityRank, int currentRank) {
        int diff = activityRank - currentRank;
        if ((activityRank > 0) && (currentRank < 0)) {
            return diff - 1; // skip nonexisting rank 0
        } else if ((activityRank < 0) && (currentRank > 0)) {
            return diff + 1; // skip nonexisting rank 0
        } else {
            return diff;
        }
    }

    private void incRank() {
        if (this.rank >= 8) {
            return;
        } else if (this.rank == -1) {
            this.rank = 1;
        } else {
            this.rank += 1;
        }
    }
}
