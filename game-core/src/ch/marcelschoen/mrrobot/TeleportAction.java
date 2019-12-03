package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.action.SpriteAction;

public class TeleportAction implements SpriteAction {

    private MrRobot mrRobot = null;

    private float duration = 0.8f;
    private float timer = 0f;
    private boolean teleported = false;

    public TeleportAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void start(AnimatedSprite sprite) {
        timer = duration;
        teleported = false;
        mrRobot.setState(MrRobot.MRROBOT_STATE.TELEPORTING);
    }

    @Override
    public void execute(AnimatedSprite sprite, float delta) {
        timer -= delta;
        if(timer <= 0.4f && !teleported) {
            teleported = true;
            TiledMapTile tileBelowMrRobot = MrRobot.getTileBelow();
            Vector2 targetPosition = Teleporter.getTeleportTarget(tileBelowMrRobot);
            mrRobot.setPosition(targetPosition.x, targetPosition.y);
        }
    }

    @Override
    public boolean isFinished(AnimatedSprite sprite) {
        return timer <= 0;
    }
}
