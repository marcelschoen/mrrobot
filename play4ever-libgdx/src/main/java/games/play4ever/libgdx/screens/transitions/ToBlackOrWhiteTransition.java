package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Transitions from the first screen by fading out to white, and then
 * transitions from the white screen to the next / second screen by fading back from
 * white to the image.
 *
 * @author Marcel Schoen
 */
public class ToBlackOrWhiteTransition extends AbstractBaseTransition {

    public ShapeRenderer shapeRenderer;

    private boolean fadingChanged = false;

    private float alpha = 100;

    public ToBlackOrWhiteTransition() {
        super();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void doRender(Batch batch, float percent) {
        batch.setProjectionMatrix(getFreshMatrix4());
        batch.begin();
        batch.setColor(1, 1, 1, 1);

        if (!fadingChanged) {
            batch.draw(getCurrentTexture(), 0, 0, 0, 0,
                    getCurrentTexture().getWidth(), getCurrentTexture().getHeight(),
                    1, 1, 0, 0,0,
                    getCurrentTexture().getWidth(), getCurrentTexture().getHeight(),
                    false, true);
        } else {
            batch.draw(getNextTexture(), 0, 0, 0, 0,
                    getNextTexture().getWidth(), getNextTexture().getHeight(),
                    1, 1, 0, 0, 0,
                    getNextTexture().getWidth(), getNextTexture().getHeight(),
                    false, true);
        }
        batch.end();

        // Draw white filled rectangle with varying transparency (alpha)

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(1, 1, 1, alpha);
        if (!fadingChanged) {
            alpha = percent * 2;
        } else {
            alpha = 1 - (percent / 2);
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        if (percent >= 0.5f && !fadingChanged) {
            // After image has gone full white, change fading direction
            fadingChanged = true;
        }
    }
}
