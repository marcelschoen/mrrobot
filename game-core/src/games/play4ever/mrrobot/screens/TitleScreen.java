/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.mrrobot.MrRobotAssets;

/**
 * The title screen.
 *
 * @author Marcel Schoen
 */
public class TitleScreen extends AbstractBaseScreen {

	private Game game = null;

	private static TitleScreen instance = null;

	/** The "BOMB" label sprite. */
	private Texture titlePicture = null;

	private TitleMenu menu = new TitleMenu();

	/** Start timer. */
	private long startTime = -1;

	public static TitleScreen create(Game game) {
		instance = new TitleScreen(game);
		return instance;
	}

	public static TitleScreen getInstance() {
		return instance;
	}

	/**
	 * Creates a title screen.
	 */
	public TitleScreen(Game game) {
		super(game, null);
		this.game = game;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#doRender(float)
	 */
	@Override
	public void doRender(float arg0) {
		batch.begin();
		if (this.titlePicture != null) {
			batch.draw(this.titlePicture, 0, 0);
		}
		menu.render(batch, arg0);
		batch.end();
	}

	@Override
	public void performLogic(float delta) {
		menu.performLogic(this, delta);
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
}
