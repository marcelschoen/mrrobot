package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.action.Action;

import ch.marcelschoen.mrrobot.MrRobot;

import static ch.marcelschoen.mrrobot.MrRobotState.DROP_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

/**
 * Lets Mr. Robot drop down vertically.
 *
 * @author Marcel Schoen
 */
public class DropDownAction extends Action {

    private static final float DOWN_SPEED = 32;

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public DropDownAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        mrRobot.changeState(DROP_RIGHT);
    }

    @Override
    protected void execute(float delta) {
        mrRobot.setPosition(mrRobot.getX(), mrRobot.getY() - DOWN_SPEED * delta);
    }

    @Override
    protected boolean isDone() {
        int tileBelowId = mrRobot.getTileBelowId();
        if (tileBelowId != NO_TILE && tileBelowId != TILE_SLIDER
                && tileBelowId != TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_RIGHT) {
            if (mrRobot.mrRobotIsNearlyAlignedVertically()) {
                mrRobot.mrRobotLands();
                return true;
            }
        }
        return false;
    }
}
