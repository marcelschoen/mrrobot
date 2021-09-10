package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.collision.Collision;
import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.libgdx.screens.ScreenTransition;
import games.play4ever.libgdx.screens.TransitionScreen;
import games.play4ever.libgdx.screens.transitions.ScreenTransitions;
import games.play4ever.libgdx.sprites.Sprites;
import games.play4ever.mrrobot.Bombs;
import games.play4ever.mrrobot.DebugOutput;
import games.play4ever.mrrobot.Flame;
import games.play4ever.mrrobot.GameDataStore;
import games.play4ever.mrrobot.GameInput;
import games.play4ever.mrrobot.GamepadOverlay;
import games.play4ever.mrrobot.Hud;
import games.play4ever.mrrobot.MrRobot;
import games.play4ever.mrrobot.MrRobotAssets;
import games.play4ever.mrrobot.MrRobotGame;
import games.play4ever.mrrobot.TileMap;

public class PlayScreen extends AbstractBaseScreen {

    private TileMap tileMap = null;
    private MrRobot mrRobot = null;
    private Hud hud = null;

    private static boolean slidingDirection = true;

    /**
     *
     * @param game
     */
    public PlayScreen(MrRobotGame game) {
        super(game, Color.BLUE);
    }

    public void startGame(int level) {

        TileMap.setLastUnlockedMapIndex(GameDataStore.getLastUnlockedLevel());
        Hud.setHighScore(GameDataStore.getHighScore());

        TileMap.jumpToLevel(level);

        if(this.mrRobot == null) {
            this.mrRobot = new MrRobot(this);
        }
        startLevel();
        Hud.resetScoreAndLives();

        GamepadOverlay.initialize();
    }

    public void startLevel() {
        MrRobotGame.isGameOver = false;
        Sprites.clearAll();
        Collision.clearRectangles();
        this.mrRobot.createSprites();
        this.tileMap = new TileMap(TileMap.getCurrentMap(), this.mrRobot);
    }

    public void handleInput(float delta) {
        if(MrRobotGame.isGameOver){
            if(GameInput.isButtonOkJustPressed()) {
                TransitionScreen.setupAndShowTransition(game, 1.5f, ScreenTransitions.TO_BLACK_OR_WHITE.getTransition(), this, TitleScreen.getInstance());
            }
            return;
        }
        if(GameInput.isButtonBackJustPressed()) {
            TransitionScreen.setupAndShowTransition(game, 1.5f, ScreenTransitions.TO_BLACK_OR_WHITE.getTransition(), this, TitleScreen.getInstance());
            return;
        }
        mrRobot.handleInput(delta);
        mrRobot.moveMrRobot(delta);
        mrRobot.checkMrRobot(delta);
    }

    @Override
    public void show() {
        MrRobotAssets.stopMenuMusic();
        super.show();
    }

    @Override
    public void performLogic(float delta) {

        if(tileMap.getNumberOfDots() == 0) {

            slidingDirection = !slidingDirection;
            ScreenTransition transition = slidingDirection ?
                    ScreenTransitions.SLIDING_RIGHT.getTransition() :
                    ScreenTransitions.SLIDING_LEFT.getTransition();
            transition.setupTransition(game, 2f, this);

            TileMap.switchToNextMap();
            startLevel();
            transition.setupNextScreen(this);

            TransitionScreen transitionScreen = TransitionScreen.getInstance();
            transitionScreen.setTransition(transition);
            MrRobotGame.instance().setScreen(transitionScreen);
        }

        // Handle controls
        handleInput(delta);

        for(Flame flame : Flame.flames) {
            flame.move(delta);
        }

        // TODO: MOVE INTO "Sprites" CONTROLLER
        Collision.checkForCollisions();

        Bombs.getInstance().processBombs(delta);
    }

    /* (non-Javadoc)
     * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
     */
    @Override
    public void doRender(float delta) {

        this.camera.update();

        // Update tilemap
        this.tileMap.doRender(delta, camera);

        batch.begin();

        if(MrRobotGame.isGameOver) {
            BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.SETTINGS);
            font.draw(batch, "GAME", 90f, 140f);
            font.draw(batch, "OVER", 95f, 100f);
        } else {
            // Draw Mr. Robot
            Sprites.drawSprites(batch, delta);
        }

        // print debug stuff on screen
        // TODO: Enable only in testing / debug mode
        DebugOutput.draw(batch);

        // Draw score etc.
        Hud.doRender(batch, delta);

        batch.end();

        GamepadOverlay.draw();
    }
}
