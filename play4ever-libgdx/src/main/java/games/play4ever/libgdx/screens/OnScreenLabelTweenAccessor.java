/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * Tween accessor which changes the position of the given on-screen label.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class OnScreenLabelTweenAccessor implements TweenAccessor<OnScreenLabel> {

	/** The type of attribute to change. */
    public static final int POSITION = 1;
	
	/**
	 * Registers this accessor in the Tween engine.
	 */
	public static void register() {
        Tween.registerAccessor(OnScreenLabel.class, new OnScreenLabelTweenAccessor());
	}

    @Override
    public int getValues(OnScreenLabel target, int tweenType, float[] returnValues) {
        switch(tweenType) {
        case POSITION:
            returnValues[0] = target.getX();
            returnValues[1] = target.getY();
            return 2;
        }
        return 0;
    }
 
    @Override
    public void setValues(OnScreenLabel target, int tweenType, float[] newValues) {
        switch(tweenType) {
        case POSITION:
            target.setX(newValues[0]);
            target.setY(newValues[1]);
            break;
        }
    }
}
