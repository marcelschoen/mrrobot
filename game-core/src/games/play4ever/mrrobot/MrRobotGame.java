package games.play4ever.mrrobot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Graphics;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.screens.ScreenUtil;
import games.play4ever.mrrobot.screens.LoadingScreen;
import games.play4ever.mrrobot.screens.PlayScreen;
import games.play4ever.mrrobot.screens.TitleScreen;

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

    // Map is 40x22 with 8x8 pixel tiles
    public static int VIRTUAL_WIDTH = 320;
    public static int VIRTUAL_HEIGHT = 176;

    public static boolean testing = false;

    /** Singleton instance of game. */
    private static MrRobotGame instance = null;

    /** Stores the current display mode. */
    public static Graphics.DisplayMode displayMode = null;

    public PlayScreen playScreen;

    @Override
    public void create() {
        if(instance != null) {
            throw new IllegalStateException("** already a game instance there **");
        }
        instance = this;

        if(System.getProperty("game.testing","").equalsIgnoreCase("on")) {
            // enable interactive testing
            testing = true;
        }

        ScreenUtil.initialize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        Assets assets = new MrRobotAssets();
        assets.initialize();

        TitleScreen titleScreen = new TitleScreen(this);
        setScreen(new LoadingScreen(this, titleScreen));
    }

    @Override
    public void resize (int width, int height) {
        if (screen != null) screen.resize(width, height);
        GamepadOverlay.resize(width, height);
    }

    public void finishInitialization() {
        playScreen = new PlayScreen(this);
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
