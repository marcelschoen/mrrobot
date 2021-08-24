package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ToBlackOrWhiteTransition extends AbstractBaseTransition {

    public ShapeRenderer shapeRenderer;
    // Once this reaches 1.0f the next scene is shown
    //private float alpha = 0;
    // true if fade in, false if fade out
    private boolean fadeDirection = true;
    private boolean fadingChanged = false;

    private float alpha = 100;

    public ToBlackOrWhiteTransition() {
        super();
        shapeRenderer = new ShapeRenderer();
    }

    public ToBlackOrWhiteTransition(boolean fadeDirection) {
        this();
        this.fadeDirection = fadeDirection;
    }

    @Override
    public void doRender(Batch batch, float percent) {
        batch.setProjectionMatrix(getFreshMatrix4());
        batch.begin();
        batch.setColor(1, 1, 1, 1);

        if (fadeDirection) {
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

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        if(fadeDirection) {
            alpha = percent * 2;
        } else {
            alpha = 1 - (percent / 2);
        }
        System.out.println("> ALPHA: " + alpha);
        shapeRenderer.setColor(1, 1, 1, alpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        if (percent >= 0.5f && !fadingChanged) {
            System.out.println("---- change fading direction ----");
            fadeDirection = !fadeDirection;
            fadingChanged = true;
        }
    }
}
