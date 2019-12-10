package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.collision.Collision;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.screens.ScreenUtil;
import com.jplay.gdx.sprites.Sprites;

import ch.marcelschoen.mrrobot.Bombs;
import ch.marcelschoen.mrrobot.Flame;
import ch.marcelschoen.mrrobot.GamepadOverlay;
import ch.marcelschoen.mrrobot.Hud;
import ch.marcelschoen.mrrobot.MrRobot;
import ch.marcelschoen.mrrobot.MrRobotAssets;
import ch.marcelschoen.mrrobot.MrRobotGame;
import ch.marcelschoen.mrrobot.TileMap;

public class PlayScreen extends AbstractBaseScreen {

    private TileMap tileMap = null;
    private MrRobot mrRobot = null;
    private Hud hud = null;

    /**
     *
     * @param game
     */
    public PlayScreen(MrRobotGame game) {
        super(game, null, Color.BLUE);

        DebugOutput.setPlayScreen(this);
        this.mrRobot = new MrRobot();
        this.tileMap = new TileMap("map/level16.tmx", this.mrRobot, this.camera);
//        this.mrRobot.setTileMap(this.tileMap);
        Hud.setScore(0);

        GamepadOverlay.initialize();
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
        if(Gdx.input.isTouched()) {
            System.out.println("*TOUCH*" + Gdx.input.getX() + " / " + Gdx.input.getY());
        }
        mrRobot.handleInput(delta);
        mrRobot.moveMrRobot(delta);
        mrRobot.checkMrRobot(delta);
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

        // Handle controls
        handleInput(delta);
        this.camera.update();

        // Update tilemap
        this.tileMap.doRender(delta, this.camera);

        batch.begin();

        // Draw Mr. Robot
        Sprites.drawSprites(batch, delta);

        // Draw all flames
        for(Flame flame : Flame.flames) {
            flame.draw(batch, delta);
        }

        // TODO: MOVE INTO "Sprites" CONTROLLER
        Collision.checkForCollisions();

        Bombs.getInstance().processBombs(delta);

        // print debug stuff on screen
        // TODO: Enable only in testing / debug mode
        DebugOutput.draw(batch);

        // Draw score etc.
        Hud.doRender(batch, delta);

        batch.end();

        GamepadOverlay.draw();
    }
}
