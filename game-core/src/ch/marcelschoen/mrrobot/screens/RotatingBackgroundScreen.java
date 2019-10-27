/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.mrrobot.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jplay.gdx.screens.AbstractBaseScreen;
import com.jplay.gdx.screens.ScreenUtil;

import java.util.Random;

/**
 * Screen which displays a background made of
 * rotating yellow rays.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public abstract class RotatingBackgroundScreen extends AbstractBaseScreen {
	
	private Ray firstRay = null;
	private Coordinate startCoordinate = new Coordinate(0, 0);
	private Coordinate drawCoordinate = new Coordinate(0,0);

	public RotatingBackgroundScreen(Game game, Screen previousScreen) {
		super(game, previousScreen);
		
		Random rnd = new Random(System.currentTimeMillis());
		int totalPixels = ScreenUtil.getWidth() * 2 + ScreenUtil.getHeight() * 2;
		int width = 0, pixelCt = 0;
		Ray ray = null;
		while(pixelCt < totalPixels) {
			width = rnd.nextInt(60) + 10;
			ray = new Ray(width, ray);
			if(firstRay == null) {
				firstRay = ray;
			}
			pixelCt += width;
		}
	}

	
	/* (non-Javadoc)
	 * @see com.jplay.gdx.screens.AbstractBaseScreen#render(float)
	 */
	@Override
	public void render(float delta) {
		baseRendering(delta);
		
		drawRotatingBackground(this.shapeRenderer, this.startCoordinate, this.drawCoordinate, firstRay);
		this.startCoordinate.moveForward();
		
		doRender(delta);
	}

	/**
	 * @param currentRay
	 */
	private void drawRotatingBackground(ShapeRenderer shapeRenderer, Coordinate startCoordinate, Coordinate drawCoordinate, Ray currentRay) {
		Gdx.gl.glClearColor(1, 1, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.setColor(new Color(1, 1, 0, 0.0f));
		shapeRenderer.begin(ShapeType.Line);
		
		drawCoordinate.x = startCoordinate.x;
		drawCoordinate.y = startCoordinate.y;
		drawCoordinate.xStep = startCoordinate.xStep;
		drawCoordinate.yStep = startCoordinate.yStep;
		int midx = ScreenUtil.getWidth() / 2, midy = ScreenUtil.getHeight() / 2;
		int pixelCt = 0;
		boolean drawOn = true;
		while(currentRay != null) {
			if(drawOn) {
				shapeRenderer.line(drawCoordinate.x, drawCoordinate.y, midx, midy);
			}
			drawCoordinate.moveForward();
			pixelCt ++;
			if(pixelCt >= currentRay.length) {
				currentRay = currentRay.nextRay;
				pixelCt = 0;
				drawOn = !drawOn;
			}
		}
		shapeRenderer.end();
	}
}
