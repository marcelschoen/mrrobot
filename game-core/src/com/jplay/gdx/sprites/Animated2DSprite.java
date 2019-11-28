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
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

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

    private Interpolation interpolation;
	private Interpolation interpolation2;
    private float movementDuration = -1f;
	private float movementTimer = -1f;
    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private SpriteListener listener;


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
	    this.animation = animationMap.get(name);
	    if(this.animation == null) {
	    	throw new IllegalArgumentException("No animation found for name '" + name + "'");
		}
    }

	public void moveTo(float targetX, float targetY, float secDuration,
					   SpriteListener completionCallback) {
		moveTo(targetX, targetY, secDuration, Interpolation.linear, null, completionCallback);
	}

	public void moveTo(float targetX, float targetY, float secDuration,
					   Interpolation interpolation,
					   SpriteListener completionCallback) {
		moveTo(targetX, targetY, secDuration, interpolation, null, completionCallback);
	}

	public void moveTo(float targetX, float targetY, float secDuration,
					   Interpolation interpolationX,
					   Interpolation interpolationY,
					   SpriteListener completionCallback) {
		this.startX = getX();
		this.startY = getY();
		this.targetX = targetX;
		this.targetY = targetY;
		this.interpolation = interpolationX;
		this.interpolation2 = interpolationY;
		this.movementDuration = secDuration;
		this.movementTimer = secDuration;
		this.listener = completionCallback;
	}


	private boolean moveSpline = false;
	private int splineFidelity = 100; //increase splineFidelity for more splineFidelity to the spline
	private Vector2[] points = new Vector2[splineFidelity];
	float splineMoveSpeed = 0.15f;
	float current = 0;

	public void moveAlongSpline(Vector2[] dataSet) {
		if(moveSpline) {
			return;
		}
		CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(dataSet, true);
		for(int i = 0; i < splineFidelity; ++i) {
			points[i] = new Vector2();
			myCatmull.valueAt(points[i], ((float)i)/((float) splineFidelity -1));
		}
		moveSpline = true;
	}


	/**
	 * Draws the sprite into a given spriteBatch at the current position.
	 *
	 * @param batch The target spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 */
	public void draw(SpriteBatch batch, float delta) {

		if(moveSpline) {
			current += delta * splineMoveSpeed;
			if(current >= 1)
				current -= 1;
			float place = current * splineFidelity;
			Vector2 first = points[(int)place];
			Vector2 second;
			if(((int)place+1) >= splineFidelity) {
				moveSpline = false;
			} else {
				second = points[(int)place+1];
				float t = place - ((int)place); //the decimal part of place
				setX(first.x + (second.x - first.x) * t);
				setY(first.y + (second.y - first.y) * t);
			}
		} else if(movementTimer > 0) {
			float state = 1f - (movementTimer / movementDuration);
			setX(interpolation.apply(startX, targetX, state));
			if(interpolation2 != null) {
				setY(interpolation2.apply(startY, targetY, state));
			} else {
				setY(interpolation.apply(startY, targetY, state));
			}
			movementTimer -= delta;
			if(movementTimer <= 0f && this.listener != null) {
				this.listener.updated(SpriteEvent.getEvent(SpriteEvent.TYPES.TARGET_REACHED, this));
			}
		}
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
