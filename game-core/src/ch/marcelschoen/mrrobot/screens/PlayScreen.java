package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.screens.ScreenUtil;
import com.jplay.gdx.sprites.Sprites;
import com.jplay.gdx.tween.JPlayTweenManager;

import ch.marcelschoen.mrrobot.Flame;
import ch.marcelschoen.mrrobot.Hud;
import ch.marcelschoen.mrrobot.MrRobot;
import ch.marcelschoen.mrrobot.MrRobotAssets;
import ch.marcelschoen.mrrobot.MrRobotGame;
import ch.marcelschoen.mrrobot.TileMap;

public class PlayScreen extends AbstractBaseScreen /*implements TweenCallback*/ {

    private TileMap tileMap = null;
    private MrRobot mrRobot = null;
    private Hud hud = null;

    /**
     *
     * @param game
     */
    public PlayScreen(MrRobotGame game) {
        super(game, null);

        DebugOutput.setPlayScreen(this);
        this.mrRobot = new MrRobot();
        this.tileMap = new TileMap("map/level16.tmx", this.mrRobot, this.camera);
//        this.mrRobot.setTileMap(this.tileMap);

        Hud.setScore(0);
    }

    public void handleInput(float delta) {
        /*
        if(Gdx.input.isTouched()) {
            camera.position.x += 100 * delta;
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x += 100 * delta;
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x -= 100 * delta;
            }
        }
         */
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            ScreenUtil.dispose();
            System.exit(-1);
        }
        mrRobot.handleInput(delta);
    }

    @Override
    public void show() {
        MrRobotAssets.stopMenuMusic();
    }

    /* (non-Javadoc)
     * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
     */
    @Override
    public void doRender(float delta) {
        // Update tween engine status
        JPlayTweenManager.instance().update(delta);

        // Handle controls
        handleInput(delta);
        this.camera.update();

        // Update tilemap
        this.tileMap.doRender(delta, this.camera);

        batch.begin();

        // Draw Mr. Robot
        Sprites.drawSprites(batch, delta);
////////////        this.mrRobot.draw(batch, delta);

        // Draw all flames
        for(Flame flame : Flame.flames) {
            flame.draw(batch, delta);
        }
        // print debug stuff on screen
        // TODO: Enable only in testing / debug mode
        DebugOutput.draw(batch);

        // Draw score etc.
        Hud.doRender(batch, delta);

        batch.end();
    }
}
