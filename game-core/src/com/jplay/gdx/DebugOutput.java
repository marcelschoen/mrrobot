/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import java.util.HashMap;
import java.util.Map;

/**
 * Prints debug output into the game screen.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DebugOutput {

	private static Map<GridPoint2, String> debugLines = new HashMap();

	private static BitmapFont debugFont = null;

	private static boolean initialized = false;
	
	public static void initialize(BitmapFont font) {
		if(!initialized) {
			initialized = true;
			debugFont = font;
			System.out.println("Debug line height: " + debugFont.getLineHeight() + ", cap height: " + debugFont.getCapHeight() + ", Xheight: " + debugFont.getXHeight());
		}
	}
	
	public static void log(String text, int x, int y) {
		if(initialized) {
			debugLines.put(new GridPoint2(x, y), text);
		}
	}

	public static void clear() {
		debugLines.clear();
	}
	
	public static void draw(SpriteBatch batch) {
		if(debugFont == null) {
			throw new IllegalStateException("Debug font not ready.");
		}
		for(GridPoint2 position : debugLines.keySet()) {
			debugFont.draw(batch, debugLines.get(position), position.x, position.y);
		}
	}
}
