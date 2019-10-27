package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.Assets;
import com.jplay.gdx.darkfunction.AnimationSheet;
import com.jplay.gdx.screens.OnScreenLabelTweenAccessor;
import com.jplay.gdx.screens.ScreenUtil;
import com.jplay.gdx.tween.SpriteTweenAccessor;

import ch.marcelschoen.mrrobot.screens.LoadingScreen;
import ch.marcelschoen.mrrobot.screens.PlayScreen;
import ch.marcelschoen.mrrobot.screens.TitleScreen;

/**
 * Mr. Robot platform game based on the Atari XL original by Ron Rosen (which was also
 * ported to the C64 and other systems of that time).
 *
 * This Android version is based on the great Windows port by Vidar Bang (aka "dmx") who generously
 * gave permission to use the assets of that version (maps, graphics, sounds).
 *
 * NOTE: This Android game is free in every sense of the word, and the code is open source and
 * may be used for whatever purposes you want.
 *
 * NOTE 2: While the code is open source and under Apache 2 license (so it can also be used for
 * commercial purposes) the graphics and sounds are NOT; they are the property of Vidar Bang. So
 * any commercial version of this game needs to get new graphics and sound assets!
 *
 * @author Marcel Schoen
 */
public class MrRobotGame extends Game {

    public static boolean testing = false;

    /** Singleton instance of game. */
    private static MrRobotGame instance = null;

    /** Stores the current display mode. */
    public static Graphics.DisplayMode displayMode = null;

    public SpriteBatch spriteBatch;

    @Override
    public void create() {
        if(instance != null) {
            throw new IllegalStateException("** already a game instance there **");
        }
        instance = this;

        AnimationSheet.initialize("sprites");
///////        com.jplay.gdx.joypad.Controllers.initialize();

        if(System.getProperty("game.testing","").equalsIgnoreCase("on")) {
            // enable interactive testing
            testing = true;
        }

        SpriteTweenAccessor.register();
        OnScreenLabelTweenAccessor.register();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        ScreenUtil.resize((int)w, (int)h);

        Assets assets = new MrRobotAssets();
        assets.initialize();

        this.spriteBatch = new SpriteBatch();
        //setScreen(new PlayScreen(this));

        TitleScreen titleScreen = new TitleScreen(this);
        setScreen(new LoadingScreen(this, titleScreen));
    }

    public void finishInitialization() {
        PlayScreen playScreen = new PlayScreen(this);
    }

    /**
     * Creates a MrRobotGame game instance.
     * @param currentMode
     */
    public MrRobotGame(Graphics.DisplayMode currentMode) {
        displayMode = currentMode;
    }

    /**
     * Returns the singleton game instance.
     *
     * @return The game instance (never null).
     */
    public static MrRobotGame instance() {
        return instance;
    }
}
