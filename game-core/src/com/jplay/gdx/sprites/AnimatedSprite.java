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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jplay.gdx.collision.CollisionRectangle;
import com.jplay.gdx.sprites.action.Action;
import com.jplay.gdx.sprites.action.ActionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for LIBGDX sprite. Holds animation state
 * information as well, handles execution of actions
 * and collision detection.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class AnimatedSprite extends Sprite implements Pool.Poolable {

	private Array<CollisionRectangle> collisionBounds = null;

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

	/** Optional arbitrary type to assign to the sprite. */
	private int type = 0;

	private AnimatedSprite attachedToSprite = null;
	private float xOffsetAttachement = 0;
	private float yOffsetAttachement = 0;

	/**
	 * Creates a sprite with a certain animation.
	 */
	public AnimatedSprite() {
		collisionBounds = new Array<>(2);
		collisionBounds.add(new CollisionRectangle(this, CollisionRectangle.TYPE_DEFAULT));
	}

	public AnimatedSprite(int type) {
		this();
		this.type = type;
	}

	public void attachToSprite(AnimatedSprite otherSprite, float xOffset, float yOffset) {
		attachedToSprite = otherSprite;
		xOffsetAttachement = xOffset;
		yOffsetAttachement = yOffset;
	}

	/**
	 * Returns the type of this sprite (defaults to 0).
	 *
	 * @return the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Allows to set a type. This is an arbitrary value you can use to easily distinguish different
	 * types of sprites. For example, create an int constant "BULLET=1" which you then assign to
	 * all bullet sprites, so you can easily determine which type of sprite it is in collision
	 * handling logic.
	 *
	 * @param type The type value to set.
	 */
	public void setType(int type) {
		this.type = type;
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
		float x = getX();
		float y = getY();
		if(attachedToSprite != null) {
			x = attachedToSprite.getX();
			y = attachedToSprite.getY();
		}
		draw(batch, x, y, delta);
	}

	public void addCollisionRectangle(CollisionRectangle collisionRectangle) {
		this.collisionBounds.add(collisionRectangle);
	}

	public Array<CollisionRectangle> getCollisionBounds() {
		return this.collisionBounds;
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
		// Update default sprite bounds with values matching current animation frame
		CollisionRectangle spriteBounds = collisionBounds.get(0);
		spriteBounds.x = xPosition;
		spriteBounds.y = yPosition;
		spriteBounds.width = keyFrame.getRegionWidth();
		spriteBounds.height = keyFrame.getRegionHeight();
		batch.draw(keyFrame, xPosition, yPosition);
	}
}
