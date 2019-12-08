package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.action.Action;

import ch.marcelschoen.mrrobot.MrRobot;

import static ch.marcelschoen.mrrobot.MrRobotState.SLIDING_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

/**
 * Lets Mr. Robot slide down a tube.
 *
 * @author Marcel Schoen
 */
public class SlideDownAction extends Action {

    private static final float SLIDE_SPEED = 32;

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public SlideDownAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        mrRobot.changeState(SLIDING_RIGHT);
    }

    @Override
    protected void execute(float delta) {
        mrRobot.setPosition(mrRobot.getX(), mrRobot.getY() - SLIDE_SPEED * delta);
    }

    @Override
    protected boolean isDone() {
        int tileBelowId = mrRobot.getCellBelow().getTile().getId();
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
