package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.action.Action;

import ch.marcelschoen.mrrobot.MrRobot;

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
