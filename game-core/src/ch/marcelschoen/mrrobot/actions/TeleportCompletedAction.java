package ch.marcelschoen.mrrobot.actions;

import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.action.SpriteAction;

import ch.marcelschoen.mrrobot.MrRobot;

/**
 * Last action in teleportation action chain - completes the teleportation process.
 *
 * @author Marcel Schoen
 */
public class TeleportCompletedAction implements SpriteAction {

    private MrRobot mrRobot = null;

    /**
     * @param mrRobot The Mr. Robot instance.
     */
    public TeleportCompletedAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void start(AnimatedSprite sprite) {
        mrRobot.stopMrRobot();
    }

    @Override
    public void execute(AnimatedSprite sprite, float delta) {
        // nothing to do
    }

    @Override
    public boolean isFinished(AnimatedSprite sprite) {
        return true;
    }
}
