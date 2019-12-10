/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/AbstractBaseScreen.java,v 1.5 2013/02/12 23:16:47 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.joypad.ButtonID;
import com.jplay.gdx.joypad.DpadDirection;
import com.jplay.gdx.joypad.IController;
import com.jplay.gdx.music.MusicPlayer;

import ch.marcelschoen.mrrobot.MrRobotGame;

/**
 * Base class for all game screens.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.5 $
 */
public abstract class AbstractBaseScreen implements Screen, InputProcessor {

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
		DisplayMode displayMode = MrRobotGame.displayMode;
		if(displayMode != null) {
///			System.out.println("---> SET VIEW PORT TO: ");
//	        Gdx.gl.glViewport(ScreenUtil.viewPortX, ScreenUtil.viewPortY, ScreenUtil.viewPortWidth, ScreenUtil.viewPortHeight);
		}
		Gdx.gl.glViewport(400, 0, ScreenUtil.viewPortWidth - 400 ,ScreenUtil.viewPortHeight - 400);

		camera.update();

		MusicPlayer.instance().update(delta);

		handleJoypad();

		if(flickerBackgroundColor != null) {
			Gdx.gl.glClearColor(flickerBackgroundColor.r, flickerBackgroundColor.g, flickerBackgroundColor.b, 1);
			flickerBackgroundColor = null;
		} else {
			Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * Handles joypad input.
	 */
	protected void handleJoypad() {
		IController[] controllers = com.jplay.gdx.joypad.Controllers.getControllers();
		for(IController controller : controllers) {
			DpadDirection direction = controller.getDpadDirectionOnce();
			if(direction == DpadDirection.UP) {
				upKey();
			} else if(direction == DpadDirection.DOWN) {
				downKey();
			} else if(direction == DpadDirection.LEFT) {
				leftKey();
			} else if(direction == DpadDirection.RIGHT) {
				rightKey();
			} else if(direction == DpadDirection.NONE) {
				noKey();
			}
			if(controller.isButtonPressed(ButtonID.FACE_DOWN)) {
				enterKey();
			}
			if(controller.isButtonPressed(ButtonID.BACK)) {
				goBackToPreviousScreen();
			}
		}
	}

	/**
	 * Executes the "render" method of the "Screen" interface, but performs
	 * some other stuff like updating the joypad status etc.
	 * 
	 * @param delta
	 */
	public abstract void doRender(float delta);

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		ScreenUtil.resize(width, height);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#show()
	 */
	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#hide()
	 */
	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#pause()
	 */
	@Override
	public void pause() {
		System.out.println("-- pause screen --");
		// nothing
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#resume()
	 */
	@Override
	public void resume() {
		System.out.println("-- resume screen --");
		// nothing
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.Screen#dispose()
	 */
	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.DPAD_DOWN) {
			downKey();
		} else if(keycode == Input.Keys.DPAD_UP) {
			upKey();
		} else if(keycode == Input.Keys.DPAD_RIGHT) {
			rightKey();
		} else if(keycode == Input.Keys.DPAD_LEFT) {
			leftKey();
		} else if(keycode == Input.Keys.ENTER) {
			enterKey();
		}
		return false;
	}

	/**
	 * Perform menu stuff when cursor up key is pressed
	 * or up on dpad.
	 */
	public void upKey() {
		// default does nothing
	}
	
	/**
	 * Perform menu stuff when cursor down key is pressed 
	 * or down on dpad.
	 */
	public void downKey() {
		// default does nothing
	}
	
	/**
	 * Perform menu stuff when cursor left key is pressed
	 * or left on dpad.
	 */
	public void leftKey() {
		// default does nothing
	}
	
	/**
	 * Perform menu stuff when cursor right key is pressed
	 * or right on dpad.
	 */
	public void rightKey() {
		// default does nothing
	}
	
	/**
	 * Perform menu stuff when no key is pressed
	 * or dpad has been released.
	 */
	public void noKey() {
		// default does nothing
	}
	
	/**
	 * Perform menu stuff when enter down key is pressed
	 * or fire button on gamepad.
	 */
	public void enterKey() {
		// default does nothing
	}

}
