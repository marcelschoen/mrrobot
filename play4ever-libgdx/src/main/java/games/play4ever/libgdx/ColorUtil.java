/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx;

import com.badlogic.gdx.graphics.Color;

/**
 * Utility class for LIBGDX / RGB color handling.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class ColorUtil {

	/**
	 * Create a color based on integer RGB values with ranges from 0 - 255 
	 * (LIBGDX uses float with ranges from 0 - 1).
	 * 
	 * @param red The red value (0 - 255).
	 * @param green The green value (0 - 255).
	 * @param blue The blue value (0 - 255).
	 * @param alpha The transparence value (0 - 255, where 0 is fully transparent and 255 is opaque).
	 * @return The Color holder.
	 */
	public static Color createColor(int red, int green, int blue, int alpha) {
		float realRed = (1f / 255f) * red;
		float realGreen = (1f / 255f) * green;
		float realBlue = (1f / 255f) * blue;
		float realAlpha = (1f / 255f) * alpha;
		return new Color(realRed, realGreen, realBlue, realAlpha);
	}
}
