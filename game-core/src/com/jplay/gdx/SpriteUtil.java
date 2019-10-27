/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.screens.ScreenUtil;

/**
 * Sprite utility stuff.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class SpriteUtil {

	public static void drawCentered(SpriteBatch batch, Sprite sprite, float y) {
		float x = ( ScreenUtil.getWidth() - sprite.getWidth() ) / 2;
		batch.draw(sprite, x, y);
	}
}
