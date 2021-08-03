package games.play4ever.mrrobot.actions;

import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.mrrobot.MrRobot;
import games.play4ever.mrrobot.Trampoline;
import games.play4ever.mrrobot.Trampolins;

import static games.play4ever.mrrobot.MrRobotState.DROP_RIGHT;
import static games.play4ever.mrrobot.Tiles.NO_TILE;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_SLIDER;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_MIDDLE;

/**
 * Lets Mr. Robot drop down vertically.
 *
 * @author Marcel Schoen
 */
public class DropDownAction extends Action {

    private static final float DOWN_SPEED = 32;

    private int startingHeight = -1;

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
        this.startingHeight = mrRobot.getTileMapRow();
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
                int fallHeight = startingHeight- mrRobot.getTileMapRow();
                if(tileBelowId == TILE_TRAMPOLINE_MIDDLE) {
                    Trampoline trampoline = Trampolins.getTrampolineForTile(mrRobot.getTileMapColumn(), mrRobot.getTileMapRow() - 1);



                } else {
                    if(fallHeight > 4) {
                        mrRobot.die();
                    }
                }
                return true;
            }
        }
        return false;
    }
}
