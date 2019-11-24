/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/JPlaySprite.java,v 1.1 2013/02/11 23:53:37 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.aseprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Wrapper for LIBGDX sprite. Holds animation state
 * information as well.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Animated2DSprite {

	/** Holds the LIBGDX sprite. */
	private Sprite sprite = null;

	/** Stores the animation. */
	private Animation<TextureRegion> animation = null;

	/** The animation state time holder. */
    private float stateTime = 0.0f;

    /** Flag which determines if animation is active. */
    private boolean animationRunning = false;

    /** X-coordinate on screen. */
    private float x = 0;

    /** Y-coordinate on screen. */
    private float y = 0;

    /**
     * Creates a sprite which wraps a certain LIBGDX sprite.
     * 
     * @param sprite The wrapped LIBGDX sprite.
     */
	public Animated2DSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	/**
	 * Creates a sprite with a certain animation.
	 * 
	 */
	public Animated2DSprite(Animation<TextureRegion> animation) {
		assert(animation != null);
		this.animation = animation;
		this.animationRunning = true;
	}

	/**
	 * @return The LIBGDX sprite.
	 */
	public Sprite getSprite() {
		return this.sprite;
	}

	/**
	 * Sets the position of the sprite.
	 *
	 * @param x The x-coordinate on screen.
	 * @param y The y-coordinate on screen.
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return The x-coordinate on screen.
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return The Y-coordinate on screen.
	 */
	public float getY() {
		return y;
	}

	/**
	 * Returns the height in pixels of the current sprite.
	 * 
	 * @param delta The number of seconds since last screen refresh.
	 * @return The current height.
	 */
	public int getCurrentHeight(float delta) {
		if(this.sprite != null) {
			return (int)this.sprite.getHeight();
		} 
		TextureRegion keyFrame = this.animation.getKeyFrame(this.stateTime, true);
		return keyFrame.getRegionHeight();
	}

	/**
	 * Returns the width in pixels of the current sprite.
	 *
	 * @param delta The number of seconds since last screen refresh.
	 * @return The current width.
	 */
	public int getCurrentWidth(float delta) {
		if(this.sprite != null) {
			return (int)this.sprite.getHeight();
		}
		TextureRegion keyFrame = this.animation.getKeyFrame(this.stateTime, true);
		return keyFrame.getRegionWidth();
	}

	public Animation<TextureRegion> getAnimation() {
		return animation;
	}

	/**
	 * Draws the sprite into a given spriteBatch at the current position.
	 *
	 * @param batch The target spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 */
	public void draw(SpriteBatch batch, float delta) {
		draw(batch, x, y, delta);
	}

	/**
	 * Draws the sprite into a given spriteBatch.
	 * 
	 * @param batch The target spriteBatch.
	 * @param xPosition The x-coordinate (in pixels) within the spriteBatch.
	 * @param yPosition The y-coordinate (in pixels) within the spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 */
	public void draw(SpriteBatch batch, float xPosition, float yPosition, float delta) {
		float x = xPosition, y = yPosition;
		if(this.sprite != null) {
			batch.draw(this.sprite, x, y);
		} else {
			if(this.animationRunning) {
	            this.stateTime += delta;
	            if(this.stateTime > 1.0) {
	            	this.stateTime -= 1f;
	            }
			}
			if(batch == null) {
				throw new IllegalArgumentException("batch must not be null");
			}
			if(this.animation == null) {
				throw new IllegalStateException("Animation not ready.");
			}
			TextureRegion keyFrame = this.animation.getKeyFrame(this.stateTime, true);
			batch.draw(keyFrame, x, y);
		}
	}
}
