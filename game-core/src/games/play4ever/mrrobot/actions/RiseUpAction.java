package games.play4ever.mrrobot.actions;

import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.mrrobot.MrRobot;

import static games.play4ever.mrrobot.MrRobotState.RISING_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_ELEVATOR;

/**
 * Lets Mr. Robot rise up vertically.
 *
 * @author Marcel Schoen
 */
public class RiseUpAction extends Action {

    private static final float RISING_SPEED = 10;

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public RiseUpAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        mrRobot.changeState(RISING_RIGHT);
    }

    @Override
    protected void execute(float delta) {
        mrRobot.setPosition(mrRobot.getX(), mrRobot.getY() + RISING_SPEED * delta);
    }

    @Override
    protected boolean isDone() {
        int tileBelowId = mrRobot.getTileBelowId();
        if (tileBelowId != TILE_ELEVATOR) {
            if (mrRobot.mrRobotIsNearlyAlignedVertically()) {
                mrRobot.mrRobotLands();
                return true;
            }
        }
        return false;
    }
}
