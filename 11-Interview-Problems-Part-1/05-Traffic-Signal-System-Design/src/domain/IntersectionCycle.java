package domain;

public class IntersectionCycle {
    private static final Direction[] PHASES = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private int currentPhaseIndex = 0;
    private boolean isPaused = false;
    private int pausedAtPhaseIndex = 0;

    public Direction getCurrentDirection() {
        return PHASES[currentPhaseIndex];
    }

    public Direction getNextDirection() {
        currentPhaseIndex = (currentPhaseIndex + 1) % PHASES.length;
        return PHASES[currentPhaseIndex];
    }

    public void pause() {
        this.isPaused = true;
        this.pausedAtPhaseIndex = currentPhaseIndex;
    }

    public void resume() {
        this.isPaused = false;
        this.currentPhaseIndex = pausedAtPhaseIndex;
    }

    public boolean isPaused() { return isPaused; }
    public int getCurrentPhaseIndex() { return currentPhaseIndex; }
    public int getPausedAtPhaseIndex() { return pausedAtPhaseIndex; }
}
