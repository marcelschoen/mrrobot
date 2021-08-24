package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class ToBlackOrWhiteTransition extends AbstractBaseTransition {

    public ShapeRenderer shapeRenderer;
    // Once this reaches 1.0f the next scene is shown
    private float alpha = 0;
    // true if fade in, false if fade out
    private boolean fadeDirection = true;

    public ToBlackOrWhiteTransition() {
        super();
        shapeRenderer = new ShapeRenderer();
    }

    public ToBlackOrWhiteTransition(boolean fadeDirection) {
        this();
        this.fadeDirection = fadeDirection;
    }

    @Override
    public void render(Batch batch, Texture currentScreenTexture, Texture nextScreenTexture, float percent) {


        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, currentScreenTexture.getWidth(),
                currentScreenTexture.getHeight());
        batch.setProjectionMatrix(matrix);


        batch.begin();
        batch.setColor(1, 1, 1, 1);
        if (fadeDirection == true) {
            batch.draw(currentScreenTexture, 0, 0, 0, 0,
                    currentScreenTexture.getWidth(), currentScreenTexture.getHeight(),
                    1, 1, 0, 0,0,
                    currentScreenTexture.getWidth(), currentScreenTexture.getHeight(),
                    false, true);
        } else {
//            batch.setColor(1, 1, 1, alpha);
            batch.draw(nextScreenTexture, 0, 0, 0, 0,
                    nextScreenTexture.getWidth(), nextScreenTexture.getHeight(),
                    1, 1, 0, 0, 0,
                    nextScreenTexture.getWidth(), nextScreenTexture.getHeight(),
                    false, true);
        }
        batch.end();

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(1, 1, 1, alpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        if (alpha >= 1) {
            fadeDirection = false;
        } else if (alpha <= 0 && fadeDirection == false) {
            getGame().setScreen(getNextScreen());
        }
        alpha += fadeDirection == true ? 0.01 : -0.01;
    }
}
