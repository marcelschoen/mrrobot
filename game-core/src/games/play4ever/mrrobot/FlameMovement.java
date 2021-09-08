package games.play4ever.mrrobot;

import static games.play4ever.mrrobot.Flame.FLAME_MOVEMENT_SPEED;

public enum FlameMovement {

    RIGHT(Flame.FLAME_STATE.WALKING_RIGHT, true, FLAME_MOVEMENT_SPEED, 0),
    LEFT(Flame.FLAME_STATE.WALKING_LEFT, true, -FLAME_MOVEMENT_SPEED, 0),
    UP(Flame.FLAME_STATE.CLIMBING_DOWN, true, 0, FLAME_MOVEMENT_SPEED),
    DOWN(Flame.FLAME_STATE.CLIMBING_UP, true, 0, -FLAME_MOVEMENT_SPEED)
    ;

    private Flame.FLAME_STATE flame_state;
    private float xSpeed = 0f;
    private float ySpeed = 0f;
    private boolean horizontally = false;

    private FlameMovement[] alternateMovements;
    private FlameMovement[] forcedAlternateMovements;

    FlameMovement(Flame.FLAME_STATE state, boolean horizontally, float xSpeed, float ySpeed) {
        this.flame_state = state;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.horizontally = horizontally;
    }

    public Flame.FLAME_STATE getState() {
        return flame_state;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public String getName() {
        return name();
    }

    public boolean isHorizontally() {
        return horizontally;
    }

    public FlameMovement[] getAlternateMovements() {
        return alternateMovements;
    }

    public FlameMovement[] getForcedAlternateMovements() {
        return forcedAlternateMovements;
    }

    public void setForcedAlternateMovements(FlameMovement[] forcedAlternateMovements) {
        if(forcedAlternateMovements.length > 2) {
            throw new IllegalArgumentException("There can be only 2 enforcable alternative directions!");
        }
        this.forcedAlternateMovements = forcedAlternateMovements;
    }

    public void setAlternateMovements(FlameMovement[] alternateMovements) {
        this.alternateMovements = alternateMovements;
    }
}
