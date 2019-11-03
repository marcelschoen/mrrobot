/*
 * $Header:  $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */
package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.jplay.gdx.Assets;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.screens.ScreenUtil;

import ch.marcelschoen.mrrobot.MrRobotAssets;
import ch.marcelschoen.mrrobot.MrRobotGame;

/**
 * The first screen, displayed while the assets
 * are being loaded.
 *
 * @author Marcel Schoen
 */
public class LoadingScreen extends AbstractBaseScreen {

	/** The title screen. */
	private Screen titleScreen = null;

	/**
	 * Creates a loading screen.
	 * 
	 * @param titleScreen The title screen to show after loading.
	 */
	public LoadingScreen(Game game, Screen titleScreen) {
		super(game, null);
		this.titleScreen = titleScreen;
		((MrRobotAssets) Assets.instance()).continueLoadingAssets();
	}

	/*
	 * (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
	 */
	@Override
	public void doRender(float arg0) {
		batch.begin();
		if(!Assets.instance().loadCompleted()) {
			Assets.instance().load();
		} else {
			System.out.println("All assets loaded.");
			MrRobotGame.instance().finishInitialization();
			MrRobotGame.instance().setScreen(titleScreen);
		}

		Texture loading = Assets.instance().getTexture(MrRobotAssets.TEXTURE_ID.LOADING);
		if(loading != null) {
			int x = (int) (( ScreenUtil.getScreenResolution().getWidth() - loading.getWidth() ) / 2);
			int y = (int) (( ScreenUtil.getScreenResolution().getHeight() - loading.getHeight() ) / 2);
			batch.draw(loading, x, y);
		}
		batch.end();
	}
}
