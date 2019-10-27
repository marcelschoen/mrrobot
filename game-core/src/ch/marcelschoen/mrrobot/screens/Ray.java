/*
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.mrrobot.screens;


/**
 * Holds information about a yellow background ray.
 *
 * @author Marcel Schoen
 * @version $Revision: $
 */
public class Ray {
	
	public int length = 0;
	public Ray nextRay = null;
	
	public Ray(int length, Ray previousRay) {
		this.length = length;
		if(previousRay != null) {
			previousRay.nextRay = this;
		}
	}

}
