package games.play4ever.mrrobot.actions;

import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.mrrobot.MrRobot;

/**
 * Last action in teleportation action chain - completes the teleportation process
 * (basically just stops the teleporting animation).
 *
 * @author Marcel Schoen
 */
public class TeleportCompletedAction extends Action {

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport completed action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public TeleportCompletedAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        mrRobot.stopMrRobot();
    }

    @Override
    protected void execute(float delta) {
        // do nothing
    }

}
