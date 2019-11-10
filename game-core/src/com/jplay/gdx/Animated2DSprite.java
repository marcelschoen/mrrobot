/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/JPlaySprite.java,v 1.1 2013/02/11 23:53:37 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

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

	/** 
	 * Flag which marks sprites read from spritesheets
	 * created with the DarkFunctions Editor.
	 */
	private boolean isDarkFunctions = false;
	
	/** Holds the LIBGDX sprite. */
	private Sprite sprite = null;

	/** Stores the animation. */
	private Animation<TextureRegion> animation = null;

	/** Stores the animation info. */
	private AnimationInfo animationInfo = null;
	
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
	 * @param animationInfo The animation info for this sprite.
	 */
	public Animated2DSprite(AnimationInfo animationInfo, boolean isDarkFunctions) {
		if(animationInfo == null) {
			throw new IllegalArgumentException("animation info must not be null!");
		}
		this.animationInfo = animationInfo;
		this.animation = new Animation(this.animationInfo.frameDuration, this.animationInfo.keyFrames);
		this.animationRunning = true;
		this.isDarkFunctions = isDarkFunctions;
	}
	
	/**
	 * Creates a new sprite instance with the same attributes as
	 * this one.
	 * 
	 * @return The new sprite instance.
	 */
	public Animated2DSprite copy() {
		Animated2DSprite copy = new Animated2DSprite(this.sprite);
		copy.animationInfo = this.animationInfo;
		copy.animation = new Animation(this.animationInfo.frameDuration, this.animationInfo.keyFrames);
		copy.animationRunning = this.animationRunning;
		copy.isDarkFunctions = this.isDarkFunctions;
		return copy;
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
	 * Defines if this is a DarkFunctions sprite.
	 * 
	 * @param value True if it is.
	 */
	public void setDarkFunctions(boolean value) {
		this.isDarkFunctions = value;
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

	public AnimationInfo getAnimationInfo() {
		return animationInfo;
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
		System.out.println(">> draw at: " + x + "," + y);
		if(this.sprite != null) {
			batch.draw(this.sprite, x, y);
		} else {
			if(this.animationRunning) {
	            this.stateTime += delta;
	            /*
	            if(this.stateTime > 1.0) {
	            	this.stateTime = 0f;
	            }
	            */
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
