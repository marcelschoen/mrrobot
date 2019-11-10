/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.jplay.gdx.Assets;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.tween.JPlayTweenManager;

import ch.marcelschoen.mrrobot.MrRobotAssets;
import ch.marcelschoen.mrrobot.MrRobotGame;

/**
 * The title screen.
 *
 * @author Marcel Schoen
 */
public class TitleScreen extends AbstractBaseScreen {
	
	/** The "BOMB" label sprite. */
	private Texture titlePicture = null;

	/** Start timer. */
	private long startTime = -1;
	
	/**
	 * Creates a title screen.
	 */
	public TitleScreen(Game game) {
		super(game, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
	 */
	@Override
	public void doRender(float arg0) {
		batch.begin();
		JPlayTweenManager.instance().update(arg0);
		if (this.titlePicture != null) {
			batch.draw(this.titlePicture, 0, 0);
		}
		batch.end();
		if(System.currentTimeMillis() - this.startTime > 15000) {
			enterKey();
		}
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#show()
	 */
	@Override
	public void show() {
		super.show();
		this.startTime = System.currentTimeMillis();
		this.titlePicture = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.TITLE);
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#enterKey()
	 */
	@Override
	public void enterKey() {
		MrRobotAssets.playMenuMusic();
		MrRobotGame.instance().setScreen(MrRobotGame.instance().playScreen);
	}
}
