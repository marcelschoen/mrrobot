/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.mrrobot.screens;

import games.play4ever.libgdx.screens.ScreenUtil;

/**
 * Represents a coordinate on the screen, along the edge. The
 * rotating yellow background rays will be drawn from the center
 * of the screen to this coordinate.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class Coordinate {

	public int x = 0;
	public int y = 0;
	public int xStep = 1;
	public int yStep = 0;
	
	/**
	 * Creates a coordinate.
	 * 
	 * @param x The on-screen column.
	 * @param y The on-screen row.
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Moves the coordinate forward by 1 pixel, in
	 * clockwise direction.
	 */
	public void moveForward() {
		x += xStep;
		y += yStep;
		if(x >= ScreenUtil.getWidth()) {
			x = ScreenUtil.getWidth() - 1;
			xStep = 0;
			yStep = 1;
		}
		if(y >= ScreenUtil.getHeight()) {
			y = ScreenUtil.getHeight() - 1;
			xStep = -1;
			yStep = 0;
		}
		if(x < 0) {
			x = 0;
			xStep = 0;
			yStep = -1;
		}
		if(y < 0) {
			y = 0;
			xStep = 1;
			yStep = 0;
		}
	}
}