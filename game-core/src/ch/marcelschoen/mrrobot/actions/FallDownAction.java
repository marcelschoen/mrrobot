package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.action.Action;

import ch.marcelschoen.mrrobot.MrRobot;
import ch.marcelschoen.mrrobot.MrRobotGame;

import static ch.marcelschoen.mrrobot.MrRobotState.FALL_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

/**
 * Lets Mr. Robot fall down sideways.
 *
 * @author Marcel Schoen
 */
public class FallDownAction extends Action {

    private static final float HORIZONTAL_SPEED = 40;
    private static final float DOWN_SPEED = 32;

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public FallDownAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        mrRobot.changeState(FALL_RIGHT);
    }

    @Override
    protected void execute(float delta) {
        float x = mrRobot.getX();
        if(mrRobot.getState() == FALL_RIGHT) {
            x += HORIZONTAL_SPEED * delta;
        } else {
            x -= HORIZONTAL_SPEED * delta;
        }
        mrRobot.setPosition(x, mrRobot.getY() - DOWN_SPEED * delta);
        if(x < -5) {
            x = -5;
            if(mrRobot.isFalling()) {
                mrRobot.setState(mrRobot.getState().getReverse());
            }
        } else if(x > MrRobotGame.VIRTUAL_WIDTH - 19) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
            mrRobot.setState(mrRobot.getState().getReverse());
        }
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
