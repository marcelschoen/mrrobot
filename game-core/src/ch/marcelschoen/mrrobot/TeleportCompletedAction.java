package ch.marcelschoen.mrrobot;

import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.action.SpriteAction;

public class TeleportCompletedAction implements SpriteAction {

    private MrRobot mrRobot = null;

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
