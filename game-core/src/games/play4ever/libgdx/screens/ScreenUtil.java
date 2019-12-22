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
import com.badlogic.gdx.utils.viewport.FitViewport;
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
		System.out.println("**** INITIALIZE SCREEN UTIL: " + width + " x " + height);
		virtualResolution = new Resolution(width, height);
		screenResolution = new Resolution((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());

		camera = new OrthographicCamera(virtualResolution.getWidth(), virtualResolution.getHeight());
		camera.setToOrtho(false, virtualResolution.getWidth(), virtualResolution.getHeight());
//		viewPort = new ExtendViewport(virtualResolution.getWidth(), virtualResolution.getHeight(), camera);
		viewPort = new FitViewport(virtualResolution.getWidth(), virtualResolution.getHeight(), camera);
//		viewPort = new ScreenViewport(camera);
		resize((int)width, (int)height);
	}

	/**
	 * 
	 * @param newDisplayWidth
	 * @param newDisplayHeight
	 */
	public static void resize(int newDisplayWidth, int newDisplayHeight) {

//		newDisplayWidth = (newDisplayWidth * 70) / 100;
//		newDisplayHeight = (newDisplayHeight * 70) / 100;


		System.out.println("****************** RESIZE: " + newDisplayWidth + " x " + newDisplayHeight);
		//new RuntimeException().printStackTrace();

		screenResolution = new Resolution((int)Gdx.graphics.getWidth(), (int)Gdx.graphics.getHeight());
		System.out.println(">> screen resolution: " + screenResolution.getWidth() + " x "
				+ screenResolution.getHeight());

		if(virtualResolution == null) {
			throw new IllegalStateException("Set virtual game size first with 'initialize'!");
		}

		int newDisplayX = (screenResolution.getWidth() - newDisplayWidth) / 2;
		int newDisplayY = (screenResolution.getHeight() - newDisplayHeight) / 2;
		System.out.println(">> position: " + newDisplayX + " x " + newDisplayY);

		camera = new OrthographicCamera(virtualResolution.getWidth(), virtualResolution.getHeight());
		camera.setToOrtho(false, virtualResolution.getWidth(), virtualResolution.getHeight());

		//camera.update();

		float xRatio = (float)newDisplayWidth / (float) screenResolution.getWidth();
		float yRatio = (float)newDisplayHeight / (float) screenResolution.getHeight();
		System.out.println(">> ratio: " + xRatio + " vs " + yRatio);
		int viewPortWidth = newDisplayWidth;
		int viewPortHeight = newDisplayHeight;

		if(xRatio < yRatio) {
			System.out.println("--> STRETCH: Horizontally");
			// Stretch horizontally
			viewPortHeight = (int)((float) screenResolution.getHeight() * xRatio);
		} else if(yRatio < xRatio){
			System.out.println("--> STRETCH: Vertically");
			// Stretch vertically
			viewPortWidth = (int)((float) screenResolution.getWidth() * yRatio);
		}
		System.out.println("--> RESIZE viewport to " + newDisplayX + "," + newDisplayY + " / "
				+ viewPortWidth + " x " + viewPortHeight);

		System.out.println("CAMERA POS: " + camera.position.x + " / " + camera.position.y);
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
		viewPort.update(newDisplayWidth, newDisplayHeight, true);

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
