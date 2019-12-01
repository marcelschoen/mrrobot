/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/JPlaySprite.java,v 1.1 2013/02/11 23:53:37 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import com.jplay.gdx.sprites.action.Action;
import com.jplay.gdx.sprites.action.ActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for LIBGDX sprite. Holds animation state
 * information as well.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class AnimatedSprite extends Sprite implements Pool.Poolable {

	/** Visibility flag. */
	private boolean visible = false;

	/** Stores the animation. */
	private Animation<TextureRegion> animation = null;

	/** Used to determine which key frame to render during render() invocation. */
	private float animationStateTime = 0f;

	/** Animations set in initialization phase. Key = tag name in Aseprite */
    private Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();

	/** Points to action currently being executed. */
	private Action currentAction = null;

	/**
	 * Creates a sprite with a certain animation.
	 */
	public AnimatedSprite() {
	}

	/**
	 * @return True if the sprite is currently visible.
	 */
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void reset() {
		currentAction = null;
	}

	/**
	 * Lets the sprite execute an action (which can contain follow-up actions, in which case
	 * the entire chain of actions will be executed one by one).
	 *
	 * @param action The action to execute.
	 */
	public void startAction(Action action, ActionListener listener) {
		action.start(this, listener);
		currentAction = action;
	}

	/**
	 * Shows or hides the sprite (if set to "false", it will no longer
	 * be rendered on screen).
	 *
	 * @param visible Visibility value.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Adds an animation to this sprite instance.
	 *
	 * NOTE 1: This method should be invoked only during the initialization phase of the game.
	 *
	 * @param name The animation name.
	 * @param animation The animation texture regions.
	 */
	public void addAnimation(String name, Animation<TextureRegion> animation) {
        animationMap.put(name, animation);
    }

	/**
	 * Starts the given animation for this sprite.
	 *
	 * @param name The name of the animation.
	 */
	public void showAnimation(String name) {
		this.animation = animationMap.get(name);
		if(this.animation == null) {
			throw new IllegalArgumentException("No animation found for name '" + name + "'");
		}
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
		if(currentAction != null) {
			if(currentAction.isRunning()) {
				currentAction.executeAction(delta);
				if (currentAction.isCompleted()) {
					currentAction = currentAction.runFollowUpAction();
				}
			} else {
				currentAction = null;
			}
		}

		this.animationStateTime += delta;
		if(this.animationStateTime > 1.0) {
			this.animationStateTime -= 1f;
		}
		if(batch == null) {
			throw new IllegalArgumentException("batch must not be null");
		}
		if(this.animation == null) {
			throw new IllegalStateException("Animation not ready.");
		}
		TextureRegion keyFrame = this.animation.getKeyFrame(this.animationStateTime, true);
		batch.draw(keyFrame, xPosition, yPosition);
	}
}
