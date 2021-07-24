package games.play4ever.mrrobot.actions;

import com.badlogic.gdx.math.Vector2;

import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.mrrobot.MrRobot;
import games.play4ever.mrrobot.Teleporter;
import games.play4ever.mrrobot.TiledMapTileCellWrapper;

import static games.play4ever.mrrobot.MrRobotState.TELEPORTING;

/**
 * Teleports Mr. Robot to another teleporter.
 *
 * @author Marcel Schoen
 */
public class TeleportAction extends Action {

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /** Flag which indicates if teleportation has been done. */
    private boolean teleported = false;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public TeleportAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
        setDuration(0.8f);
    }

    @Override
    protected void execute(float delta) {
        if(executionTimer <= 0.4f && !teleported) {
            teleported = true;
            TiledMapTileCellWrapper cellBelowMrRobot = mrRobot.getCellBelow();
            Vector2 targetPosition = Teleporter.getTeleportTarget(cellBelowMrRobot);
            mrRobot.setPosition(targetPosition.x, targetPosition.y);
        }
    }

    @Override
    public void doStart() {
        teleported = false;
        mrRobot.setState(TELEPORTING);
    }
}
