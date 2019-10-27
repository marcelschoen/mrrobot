/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/ScreenUtil.java,v 1.1 2013/02/09 16:05:05 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.awt.Dimension;

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

	/** Offset and size of GL viewport. */
	public static int viewPortWidth = -1;
	public static int viewPortHeight = -1;
	public static int viewPortX = -1;
	public static int viewPortY = -1;
	
	/** Supported screen resolutions */
	public static enum RESOLUTION {

		/** For mobile gaming 
		XPERIA_PLAY(854, 480),
		*/
		
		/** For testing / development */
		HD(1280, 720, "hd"),
		
		/** For OUYA TV gaming */
		FULL_HD(1920, 1080, "fullhd");
		
		private String type = null;
		private int width = -1;
		private int height = -1;
		private static Dimension size = null;

		/**
		 * Creates a gameResolution holder.
		 * 
		 * @param width The width in number of pixels.
		 * @param height The height in number of pixels.
		 */
		RESOLUTION(int width, int height, String type) {
			this.width = width;
			this.height = height;
			this.type = type;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}
		
		public Dimension getSize() {
			return size;
		}
		
		public String getType() {
			return type;
		}
	}

	/** Stores the current gameResolution. */
	private static RESOLUTION gameResolution = null;
	
	/** Stores the current MIMINUM gameResolution supported by the game. */
	private static final RESOLUTION minimumResolution = RESOLUTION.HD;

	/**
	 * 
	 */
	public static void init() {
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getWidth() {
		return gameResolution.getWidth();
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getHeight() {
		return gameResolution.getHeight();
	}
	
	/**
	 * Returns the current gameResolution.
	 * 
	 * @return The screen gameResolution.
	 */
	public static RESOLUTION getResolution() {
		return gameResolution;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Dimension getSize() {
		return gameResolution.getSize();
	}

	/**
	 * 
	 */
	public static void dispose() {
		batch.dispose();
		batch = null;
	}

	/**
	 * 
	 * @param newDisplayWidth
	 * @param newDisplayHeight
	 */
	public static void resize(int newDisplayWidth, int newDisplayHeight) {
		
		// Perform this stuff only once. This game does not support
		// on-the-fly resizing anyway.
		if(gameResolution == null) {
			// TODO: select gameResolution based on input
			if(newDisplayWidth < minimumResolution.width || newDisplayHeight < minimumResolution.height) {
				throw new IllegalStateException("** Screen gameResolution too small / not supported! **");
			}
			gameResolution = RESOLUTION.HD;
			
			System.out.println("Resolution: " + gameResolution.width + " / " + gameResolution.height);
			camera = new OrthographicCamera(gameResolution.width, gameResolution.height);
			camera.setToOrtho(true, gameResolution.width, gameResolution.height);
			
			batch = new SpriteBatch();
			batch.setProjectionMatrix(camera.combined);
			
			shapeRenderer = new ShapeRenderer();
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			float xRatio = (float)newDisplayWidth / (float)gameResolution.getWidth();
			float yRatio = (float)newDisplayHeight / (float)gameResolution.getHeight();
			
			if(xRatio < yRatio) {
				// Stretch horizontally
				viewPortWidth = newDisplayWidth;
				viewPortHeight = (int)((float)gameResolution.height * xRatio);
				viewPortX = 0;
				viewPortY = ( newDisplayHeight - viewPortHeight) / 2;
			} else {
				// Stretch vertically
				viewPortWidth = (int)((float)gameResolution.width * yRatio);
				viewPortHeight = newDisplayHeight;
				viewPortX = ( newDisplayWidth - viewPortWidth ) / 2;
				viewPortY = 0;
			}
			/*
			viewPortWidth = gameResolution.width;
			viewPortHeight = gameResolution.height;
			viewPortX = 0;
			viewPortY = 0;
			*/
			System.out.println("---- VIEWPORT ---");
			System.out.println("Pos: " + viewPortX + "/" + viewPortY + ", size: " + viewPortWidth + "/" + viewPortHeight);
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

	/**
	 * 
	 * @return
	 */
	public static ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
}
