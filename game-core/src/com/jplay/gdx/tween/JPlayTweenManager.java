/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package com.jplay.gdx.tween;

import aurelienribon.tweenengine.TweenManager;

/**
 * Singleton instance handler for the Tween manager.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class JPlayTweenManager {

	/** The singleton Tween manager. */
	private static TweenManager manager = new TweenManager();
	
	/**
	 * Returns the singleton TweenManager instance.
	 * 
	 * @return The manager instance (never null).
	 */
	public static TweenManager instance() {
		return manager;
	}
}
