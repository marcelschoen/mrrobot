/*
 * $Header: $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Prints debug output into the game screen.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class DebugOutput {

	private static StringBuilder[] debugLines = new StringBuilder[20];
	
	private static BitmapFont debugFont = null;
	private static float yStep = 0;
	
	private static int currentLine = 0;
	
	private static boolean initialized = false;
	
	public static void initialize(BitmapFont font) {
		if(!initialized) {
			initialized = true;
			debugFont = font;
			yStep = (debugFont.getXHeight() * -1) + 4;
			for(int i = 0; i < debugLines.length; i++) {
				debugLines[i] = new StringBuilder(300);
			}
		}
	}
	
	public static void log(String text) {
		if(initialized) {
			debugLines[currentLine].setLength(0);
			debugLines[currentLine].append(text);
			currentLine ++;
			if(currentLine >= debugLines.length) {
				currentLine = 0;
			}
		}
	}
	
	public static void draw(SpriteBatch batch) {
		if(debugFont == null) {
			throw new IllegalStateException("Debug font not ready.");
		}
		float y = 100;
		int line = currentLine - 1;
		for(int i = 0; i < debugLines.length; i++) {
			if(line < 0) {
				line = debugLines.length - 1;
			}
			debugFont.draw(batch, debugLines[line--], 10, y);
			y += yStep;
		}
	}
}
