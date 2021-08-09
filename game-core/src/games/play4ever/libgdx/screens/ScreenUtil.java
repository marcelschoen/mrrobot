/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/ScreenUtil.java,v 1.1 2013/02/09 16:05:05 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import games.play4ever.mrrobot.screens.Resolution;

/**
 * Screen-related utility stuff.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class ScreenUtil {

	private static OrthographicCamera camera;
	private static SpriteBatch batch = new SpriteBatch();
	private static Viewport viewPort;

	/** Offset and size of GL viewport. */
/*
	public static int viewPortWidth = -1;
	public static int viewPortHeight = -1;
	public static int viewPortX = -1;
	public static int viewPortY = -1;

 */
	
	/** Stores the current screen resolution. */
	private static Resolution screenResolution = null;

	/** Stores the current in-game resolution. */
	private static Resolution virtualResolution = null;

	/** Stores the current MIMINUM screenResolution supported by the game. */
	//private static final Resolution minimumResolution = new Resolution(1280, 720);


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
		Gdx.app.log("ScreenUtil", "**** INITIALIZE SCREEN UTIL: " + width + " x " + height);
		virtualResolution = new Resolution(width, height);
		screenResolution = new Resolution((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
		Gdx.app.log("ScreenUtil", ">> virtual resolution: " + virtualResolution.getWidth() + " x "
				+ virtualResolution.getHeight());
		Gdx.app.log("ScreenUtil", ">> screen resolution: " + screenResolution.getWidth() + " x "
				+ screenResolution.getHeight());

		camera = new OrthographicCamera(virtualResolution.getWidth(), virtualResolution.getHeight());
		camera.setToOrtho(false, virtualResolution.getWidth(), virtualResolution.getHeight());
		viewPort = new ExtendViewport(virtualResolution.getWidth(), virtualResolution.getHeight(), camera);


//		viewPort = new StretchViewport(virtualResolution.getWidth(), virtualResolution.getHeight(), camera);
//		viewPort = new ScreenViewport(camera);
		resizeViewport((int)width, (int)height);
	}

	/**
	 * 
	 * @param newViewportWidth
	 * @param newViewportHeight
	 */
	public static void resizeViewport(int newViewportWidth, int newViewportHeight) {

		Gdx.app.log("ScreenUtil", "****************** RESIZE VIEWPORT: " + newViewportWidth + " x " + newViewportHeight);
		if(virtualResolution == null) {
			throw new IllegalStateException("Set virtual game size first with 'initialize'!");
		}

		camera = new OrthographicCamera(virtualResolution.getWidth(), virtualResolution.getHeight());
		camera.setToOrtho(false, virtualResolution.getWidth(), virtualResolution.getHeight());

		float xRatio = (float)newViewportWidth / (float) screenResolution.getWidth();
		float yRatio = (float)newViewportHeight / (float) screenResolution.getHeight();
		Gdx.app.log("ScreenUtil", ">> ratio: " + xRatio + " vs " + yRatio);
		int viewPortWidth = newViewportWidth;
		int viewPortHeight = newViewportHeight;

		if(xRatio < yRatio) {
			Gdx.app.log("ScreenUtil", "--> STRETCH: Horizontally");
			// Stretch horizontally
			viewPortHeight = (int)((float) screenResolution.getHeight() * xRatio);
		} else if(yRatio < xRatio){
			Gdx.app.log("ScreenUtil", "--> STRETCH: Vertically");
			// Stretch vertically
			viewPortWidth = (int)((float) screenResolution.getWidth() * yRatio);
		}
		Gdx.app.log("ScreenUtil", "--> RESIZE viewport to " + viewPortWidth + " x " + viewPortHeight);

		Gdx.app.log("ScreenUtil", "CAMERA POS: " + camera.position.x + " / " + camera.position.y);
/*
		camera.position.x = 0;
		camera.position.y = 0;
		camera.update();
		viewPort.setScreenPosition(newDisplayX, newDisplayY);
		viewPort.setScreenBounds(newDisplayX, newDisplayY, viewPortWidth, viewPortHeight);

		viewPort.setScreenX(newDisplayX);
		viewPort.setScreenY(newDisplayY);
 */
//		viewPort = new StretchViewport(viewPortWidth, viewPortHeight, camera);
		viewPort.update(viewPortWidth, viewPortHeight, true);

		Gdx.app.log("ScreenUtil", "Screen bounds: " + viewPort.getScreenWidth() + "x" + viewPort.getScreenHeight());
		Gdx.app.log("ScreenUtil", "World bounds: " + viewPort.getWorldWidth() + "x" + viewPort.getWorldHeight());

//		Gdx.gl.glViewport(newDisplayX, newDisplayY, viewPortWidth, viewPortHeight);

		batch.setProjectionMatrix(camera.combined);
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
