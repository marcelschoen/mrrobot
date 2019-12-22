/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.music;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Tween accessor which changes the volume of the given music.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class MusicTweenAccessor implements TweenAccessor<MusicWrapper> {

	/** The type of attribute to change. */
    public static final int VOLUME = 0;
    
    @Override
    public int getValues(MusicWrapper target, int tweenType, float[] returnValues) {
        switch(tweenType) {
        case VOLUME:
            returnValues[0] = target.getVolume();
            return 1;
        }
        return 0;
    }
 
    @Override
    public void setValues(MusicWrapper target, int tweenType, float[] newValues) {
        switch(tweenType) {
        case VOLUME:
            target.setVolume(newValues[0]);
            break;
        }
    }
}
