/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.tween;

import com.badlogic.gdx.graphics.g2d.Sprite;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * Tween accessor for changing sprite attributes.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class SpriteTweenAccessor implements TweenAccessor<Sprite> {

	/** Type for position changes. */
	public static final int POSITION = 1;
	
	/**
	 * Registers this accessor in the Tween engine.
	 */
	public static void register() {
        Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());
	}
	
	/* (non-Javadoc)
	 * @see aurelienribon.tweenengine.TweenAccessor#getValues(java.lang.Object, int, float[])
	 */
	@Override
	public int getValues(Sprite target, int tweenType, float[] returnValues) {
		switch(tweenType) {
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see aurelienribon.tweenengine.TweenAccessor#setValues(java.lang.Object, int, float[])
	 */
	@Override
	public void setValues(Sprite target, int tweenType, float[] newValues) {
	    switch (tweenType) {
        case POSITION:
            target.setPosition(newValues[0], newValues[1]);
            break;
	    }
	}
}
