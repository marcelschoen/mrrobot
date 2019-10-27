/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Stores the animation values for later re-use (to create
 * copies of "Animation" class instances).
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class AnimationInfo {

	/** Stores the duration of a single frame in seconds. */
	public float frameDuration;
	
	/** Stores the animation key frames. */
	public Array<TextureRegion> keyFrames;

	/**
	 * Creates an animation info holder.
	 * 
	 * @param frameDuration The frame duration in seconds.
	 * @param keyFrames The key frames (images).
	 */
	public AnimationInfo(float frameDuration, Array<TextureRegion> keyFrames) {
		this.frameDuration = frameDuration;
		this.keyFrames = keyFrames;
	}

}
