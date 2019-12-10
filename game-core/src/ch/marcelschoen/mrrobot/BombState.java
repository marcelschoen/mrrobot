package ch.marcelschoen.mrrobot;

/**
 * All possible states of a bomb.
 *
 * @author Marcel Schoen
 */
public enum BombState {
    GONE(-1, null),
    EXPLODING(0.3f, GONE),
    BURNING(4f, EXPLODING),
    IGNITED(0.5f, BURNING),
    DEFAULT(-1, IGNITED);

    public  float threshold = -1;

    public BombState nextState = null;

    BombState(float threshold, BombState nextState) {
        this.threshold = threshold;
        this.nextState = nextState;
    }
}
