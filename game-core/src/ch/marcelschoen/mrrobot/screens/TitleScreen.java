/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jplay.gdx.Assets;
import com.jplay.gdx.screens.ScreenUtil;
import com.jplay.gdx.tween.JPlayTweenManager;
import com.jplay.gdx.tween.SpriteTweenAccessor;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import ch.marcelschoen.mrrobot.SPRITE_ID;

/**
 * The title screen.
 *
 * @author Marcel Schoen
 */
public class TitleScreen extends RotatingBackgroundScreen {
	
	/** The "BOMB" label sprite. */
	private Sprite label_bomb = null;
	
	/** The "MANIACS" label sprite. */
	private Sprite label_maniacs = null;

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
		if (this.label_bomb != null && this.label_maniacs != null) {
			batch.draw(this.label_bomb, this.label_bomb.getX(),
					this.label_bomb.getY());
			batch.draw(this.label_maniacs, this.label_maniacs.getX(),
					this.label_maniacs.getY());
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
		this.label_bomb = Assets.instance().getSprite(SPRITE_ID.TITLE_BOMB);
		this.label_maniacs = Assets.instance().getSprite(SPRITE_ID.TITLE_MANIACS);

		float x = ( ScreenUtil.getWidth() - this.label_bomb.getWidth() ) / 2;
		float x2 = ( ScreenUtil.getWidth() - this.label_maniacs.getWidth() ) / 2;
		Timeline.createSequence()
		.push( Tween.set(this.label_bomb, SpriteTweenAccessor.POSITION)
			   .target(x, -this.label_bomb.getHeight()) )
		.push( Tween.set(this.label_maniacs, SpriteTweenAccessor.POSITION)
			   .target(x2, ScreenUtil.getHeight()) )
		.beginParallel()
			.push( Tween.to(this.label_bomb, SpriteTweenAccessor.POSITION, 2.0f)
			    .target(x, 180) )
			.push( Tween.to(this.label_maniacs, SpriteTweenAccessor.POSITION, 2.0f)
				.target(x2, 360) )
		.end()
		.start(JPlayTweenManager.instance());
	}

	/* (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#enterKey()
	 */
	@Override
	public void enterKey() {
		/*
		MrRobotAssets.playMenuMusic();
		MrRobotGame.instance().setScreen(MrRobotGame.startMenu);

		 */
	}
}
