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

/** @author iXeption */

/** A simple alpha fade transition
 * @author iXeption */
public class AlphaFadingTransition extends AbstractBaseTransition {

	@Override
	public void doRender (Batch batch, float percent) {
		batch.setProjectionMatrix(getFreshMatrix4());
		batch.begin();
		batch.setColor(1, 1, 1, 1);
		batch.draw(getCurrentTexture(), 0, 0, 0, 0, getCurrentTexture().getWidth(),
				getCurrentTexture().getHeight(), 1, 1, 0, 0,
			0, getCurrentTexture().getWidth(), getCurrentTexture().getHeight(), false, true);
		batch.setColor(1, 1, 1, percent);
		batch.draw(getNextTexture(), 0, 0, 0, 0, getNextTexture().getWidth(),
				getNextTexture().getHeight(), 1, 1, 0, 0, 0,
				getNextTexture().getWidth(), getNextTexture().getHeight(), false, true);
		batch.end();
	}
}
