/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/AbstractBaseScreen.java,v 1.5 2013/02/12 23:16:47 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.music.MusicPlayer;
import games.play4ever.mrrobot.MrRobotGame;

/**
 * Base class for all game screens.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.5 $
 */
public abstract class AbstractBaseScreen implements Screen {

	/** Reference to the game. */
	protected Game game = null;

	/** Caches the sprite batch reference. */
	protected SpriteBatch batch = null;

	/** Caches the camera reference. */
	protected Camera camera = null;

	/** Stores the previous screen for going back. */
	private Screen previousScreen = null;

	private Color backgroundColor = Color.BLACK;
	private Color flickerBackgroundColor = null;

	/**
	 * Creates a screen.
	 * 
	 * @param game
	 * @param previousScreen
	 */
	public AbstractBaseScreen(Game game, Screen previousScreen, Color backgroundColor) {
		initialize(game, previousScreen, backgroundColor);
	}

	/**
	 * Creates a screen.
	 *
	 * @param game
	 * @param previousScreen
	 */
	public AbstractBaseScreen(Game game, Screen previousScreen) {
		initialize(game, previousScreen, Color.BLACK);
	}

	private void initialize(Game game, Screen previousScreen, Color backgroundColor) {
		this.game = game;
		this.camera = ScreenUtil.getCamera();
		this.batch = ScreenUtil.getBatch();
		this.batch.setProjectionMatrix(this.camera.combined);
		this.previousScreen = previousScreen;
	}

	/**
	 * @return the game
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Sets the previous screen.
	 * 
	 * @param screen The previous screen (for going back).
	 */
	public void setPreviousScreen(Screen screen) {
		this.previousScreen = screen;
	}
	
	/**
	 * Switches back to the previous screen iff
	 * this screen has one.
	 */
	public void goBackToPreviousScreen() {
		if(this.previousScreen != null) {
			MrRobotGame.instance().setScreen(this.previousScreen);
		}
	}
	
	/**
	 * @return the batch
	 */
	public SpriteBatch getBatch() {
		return batch;
	}

	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		baseRendering(delta);
		doRender(delta);
	}

	public void flickerBackground(Color color) {
		flickerBackgroundColor = color;
	}

	protected void setBackground(Color color) {
		backgroundColor = color;
	}

	protected void resetBackground() {
		backgroundColor = Color.BLACK;
	}

	/**
	 * @param delta
	 */
	protected void baseRendering(float delta) {
		camera.update();
		MusicPlayer.instance().update(delta);

		if(flickerBackgroundColor != null) {
			Gdx.gl.glClearColor(flickerBackgroundColor.r, flickerBackgroundColor.g, flickerBackgroundColor.b, 1);
			flickerBackgroundColor = null;
		} else {
			Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Executes the "render" method of the "Screen" interface, but performs
	 * some other stuff like updating the joypad status etc.
	 * 
	 * @param delta
	 */
	public abstract void doRender(float delta);

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resizeViewport(int, int)
	 */
	@Override
	public void resize(int newDisplayWidth, int newDisplayHeight) {
		ScreenUtil.resizeViewport(newDisplayWidth, newDisplayHeight);
		this.camera = ScreenUtil.getCamera();
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
//		Gdx.input.setInputProcessor(this);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
//		Gdx.input.setInputProcessor(null);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
		Gdx.app.log("AbstractBaseScreen", "-- pause screen --");
		// nothing
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		Gdx.app.log("AbstractBaseScreen", "-- resume screen --");
		// nothing
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
//		Gdx.input.setInputProcessor(null);
	}
}
