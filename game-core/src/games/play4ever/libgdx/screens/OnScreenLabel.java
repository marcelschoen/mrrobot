/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/bombermaniacs/gdx/menu/OnScreenLabel.java,v 1.7 2013/02/10 11:19:49 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.screens;



/**
 * A text label displayed at a certain position on screen
 * using a bitmap font.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.7 $
 */
public class OnScreenLabel {
	
	/**
	 * Stores the option text to be displayed.
	 * TODO: localize
	 */
	private String text = "<define>";

	/** X-coordinate on screen. */
	private float x = 0;
	
	/** Y-coordinate on screen. */
	private float y = 0;

	/** target X-coordinate on screen. */
	private float targetX = 0;
	
	/** target Y-coordinate on screen. */
	private float targetY = 0;
	
	/**
	 * Creates a text label.
	 * 
	 * @param text The text label.
	 */
	public OnScreenLabel(String text) {
		this.text = text;
	}
	
	/**
	 * Sets the text of the label.
	 * 
	 * @param text The label text.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the xOffset
	 */
	public float getTargetX() {
		return targetX;
	}

	/**
	 * @param targetX the xOffset to set
	 * @return This object (for method chaining)
	 */
	public OnScreenLabel setTargetX(float targetX) {
		this.targetX = targetX;
		return this;
	}

	/**
	 * @return the yOffset
	 */
	public float getTargetY() {
		return targetY;
	}

	/**
	 * @param targetY the yOffset to set
	 * @return This object (for method chaining)
	 */
	public OnScreenLabel setTargetY(float targetY) {
		this.targetY = targetY;
		return this;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 * @return This object (for method chaining)
	 */
	public OnScreenLabel setX(float x) {
		this.x = x;
		return this;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 * @return This object (for method chaining)
	 */
	public OnScreenLabel setY(float y) {
		this.y = y;
		return this;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
}
