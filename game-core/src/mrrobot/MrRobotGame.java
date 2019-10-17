package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ch.marcelschoen.mrrobot.screens.PlayScreen;

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

    public SpriteBatch spriteBatch;

    @Override
    public void create() {
        this.spriteBatch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }
}
