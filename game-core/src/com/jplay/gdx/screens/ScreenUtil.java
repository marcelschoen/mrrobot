/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/ScreenUtil.java,v 1.1 2013/02/09 16:05:05 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.marcelschoen.mrrobot.screens.Resolution;

/**
 * Screen-related utility stuff.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class ScreenUtil {

	private static OrthographicCamera camera;
	private static SpriteBatch batch;
	private static ShapeRenderer shapeRenderer = new ShapeRenderer();
	private static Viewport viewPort;

	/** Offset and size of GL viewport. */
	public static int viewPortWidth = -1;
	public static int viewPortHeight = -1;
	public static int viewPortX = -1;
	public static int viewPortY = -1;
	
	/** Stores the current screen resolution. */
	private static Resolution screenResolution = null;

	/** Stores the current in-game resolution. */
	private static Resolution virtualResolution = null;

	/** Stores the current MIMINUM screenResolution supported by the game. */
	private static final Resolution minimumResolution = new Resolution(1280, 720);


	/**
	 * 
	 * @return
	 */
	public static int getWidth() {
		return screenResolution.getWidth();
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getHeight() {
		return screenResolution.getHeight();
	}

	/**
	 * Returns the current virtual game resolution.
	 *
	 * @return The screen game resolution.
	 */
	public static Resolution getVirtualResolution() {
		return virtualResolution;
	}

	/**
	 * Returns the current screen resolution.
	 * 
	 * @return The screen screen resolution.
	 */
	public static Resolution getScreenResolution() {
		return screenResolution;
	}

	/**
	 * 
	 */
	public static void dispose() {
		batch.dispose();
		batch = null;
	}

	public static void initialize(int width, int height) {
		virtualResolution = new Resolution(width, height);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		resize((int)w, (int)h);
	}

	/**
	 * 
	 * @param newDisplayWidth
	 * @param newDisplayHeight
	 */
	public static void resize(int newDisplayWidth, int newDisplayHeight) {

		if(virtualResolution == null) {
			throw new IllegalStateException("Set virtual game size first with 'initialize'!");
		}
		
		// Perform this stuff only once. This game does not support
		// on-the-fly resizing anyway.
		if(screenResolution == null) {
			// TODO: select screenResolution based on input
			if(newDisplayWidth < minimumResolution.getWidth() || newDisplayHeight < minimumResolution.getHeight()) {
				throw new IllegalStateException("** Screen screenResolution too small / not supported! **");
			}
			screenResolution = minimumResolution;
			
			camera = new OrthographicCamera(screenResolution.getWidth(), screenResolution.getHeight());
			camera.setToOrtho(false, screenResolution.getWidth(), screenResolution.getHeight());

			viewPort = new FitViewport(virtualResolution.getWidth(), virtualResolution.getHeight(), camera);

			batch = new SpriteBatch();
			batch.setProjectionMatrix(camera.combined);

			shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			float xRatio = (float)newDisplayWidth / (float) screenResolution.getWidth();
			float yRatio = (float)newDisplayHeight / (float) screenResolution.getHeight();
			
			if(xRatio < yRatio) {
				// Stretch horizontally
				viewPortWidth = newDisplayWidth;
				viewPortHeight = (int)((float) screenResolution.getHeight() * xRatio);
				viewPortX = 0;
				viewPortY = ( newDisplayHeight - viewPortHeight) / 2;
			} else {
				// Stretch vertically
				viewPortWidth = (int)((float) screenResolution.getWidth() * yRatio);
				viewPortHeight = newDisplayHeight;
				viewPortX = ( newDisplayWidth - viewPortWidth ) / 2;
				viewPortY = 0;
			}
		} else {
			camera.update();
			viewPort.update(newDisplayWidth, newDisplayHeight, true);
			batch.setProjectionMatrix(camera.combined);
		}
	}

	/**
	 * 
	 * @return
	 */
	public static Camera getCamera() {
		return camera;
	}

	/**
	 * 
	 * @return
	 */
	public static SpriteBatch getBatch() {
		return batch;
	}
}
