package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.action.Action;

import ch.marcelschoen.mrrobot.MrRobot;

/**
 * Last action in teleportation action chain - completes the teleportation process.
 *
 * @author Marcel Schoen
 */
public class TeleportCompletedAction extends Action {

    private MrRobot mrRobot = null;

    /**
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
