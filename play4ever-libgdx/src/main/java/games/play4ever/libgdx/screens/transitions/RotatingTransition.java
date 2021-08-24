/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *	 http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;

/** A Rotating transition
 * @author iXeption */
public class RotatingTransition extends AbstractBaseTransition {

	private Interpolation interpolation;
	private float angle;
	private TransitionScaling scaling;

	public enum TransitionScaling {
		NONE, IN, OUT
	}

	/** @param interpolation the {@link Interpolation} method
	 * @param angle the amount of rotation
	 * @param scaling apply {@link TransitionScaling} */
	public RotatingTransition (Interpolation interpolation, float angle, TransitionScaling scaling) {
		this.interpolation = interpolation;
		this.angle = angle;
		this.scaling = scaling;
	}

	@Override
	public void render (Batch batch, float percent) {
		float width = getCurrentTexture().getWidth();
		float height = getCurrentTexture().getHeight();
		float x = 0;
		float y = 0;

		float scalefactor;

		switch (scaling) {
		case IN:
			scalefactor = percent;
			break;
		case OUT:
			scalefactor = 1.0f - percent;
			break;
		case NONE:
		default:
			scalefactor = 1.0f;
			break;
		}

		float rotation = 1;
		if (interpolation != null) rotation = interpolation.apply(percent);

		batch.begin();
		batch.draw(getCurrentTexture(), 0, 0, width / 2, height / 2, width, height, 1, 1, 0, 0, 0, (int)width, (int)height, false,
			true);
		batch.draw(getNextTexture(), 0, 0, width / 2, height / 2, getNextTexture().getWidth(), getNextTexture().getHeight(),
			scalefactor, scalefactor, rotation * angle, 0, 0, getNextTexture().getWidth(), getNextTexture().getHeight(), false,
			true);
		batch.end();

	}

}
