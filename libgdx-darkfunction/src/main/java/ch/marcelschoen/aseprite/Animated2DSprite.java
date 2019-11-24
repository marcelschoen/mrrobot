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

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for LIBGDX sprite. Holds animation state
 * information as well.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class Animated2DSprite extends Sprite {

	/** Stores the animation. */
	private Animation<TextureRegion> animation = null;

	private float stateTime = 0f;

    private Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();

	/**
	 * Creates a sprite with a certain animation.
	 * 
	 */
	public Animated2DSprite() {
	}

	public void addAnimation(String name, Animation<TextureRegion> animation) {
        animationMap.put(name, animation);
    }

    public void setAnimation(String name) {
	    animation = animationMap.get(name);
	    assert(animation != null);
    }

	/**
	 * Draws the sprite into a given spriteBatch at the current position.
	 *
	 * @param batch The target spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 */
	public void draw(SpriteBatch batch, float delta) {
		draw(batch, getX(), getY(), delta);
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
		this.stateTime += delta;
		if(this.stateTime > 1.0) {
			this.stateTime -= 1f;
		}
		if(batch == null) {
			throw new IllegalArgumentException("batch must not be null");
		}
		if(this.animation == null) {
			throw new IllegalStateException("Animation not ready.");
		}
		TextureRegion keyFrame = this.animation.getKeyFrame(this.stateTime, true);
		batch.draw(keyFrame, xPosition, yPosition);
	}
}
