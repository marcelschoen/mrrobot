package games.play4ever.mrrobot;

public class FlameMovement {

    public Flame.FLAME_STATE flame_state;
    public float xSpeed = 0f;
    public float ySpeed = 0f;

    private FlameMovement[] alternateMovements;

    public FlameMovement(Flame.FLAME_STATE state, float xSpeed, float ySpeed) {
        this.flame_state = state;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public FlameMovement[] getAlternateMovements() {
        return alternateMovements;
    }

    public void setAlternateMovements(FlameMovement[] alternateMovements) {
        this.alternateMovements = alternateMovements;
    }
}
