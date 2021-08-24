package games.play4ever.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;

import games.play4ever.libgdx.screens.transitions.ScreenTransitions;

public class TransitionScreen implements Screen {
    private Screen currentScreen;
    private Screen nextScreen;

    private FrameBuffer currentBuffer;
    private FrameBuffer nextBuffer;

    private ScreenTransition transition = ScreenTransitions.ALPHA_FADE.getTransition();

    private Matrix4 matrix = new Matrix4();

/*
    public ShapeRenderer shapeRenderer;
    // Once this reaches 1.0f the next scene is shown
    private float alpha = 0;
    // true if fade in, false if fade out
    private boolean fadeDirection = true;
  */
    private boolean painted = false;

    public TransitionScreen(ScreenTransition transition) {
        this.currentScreen = transition.getCurrentScreen();
        this.nextScreen = transition.getNextScreen();
        this.transition = transition;
//        shapeRenderer = new ShapeRenderer();

        // I temporarily change the screen to next because if I call render() on it without calling the create() function
        // there will be crashed caused by using null variables
        transition.getGame().setScreen(transition.getNextScreen());
        transition.getGame().setScreen(transition.getCurrentScreen());
    }

    @Override
    public void show() {
        painted = false;
    }

    @Override
    public void render(float delta) {

        if(!painted) {

            currentBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
            nextBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

            currentBuffer.begin();
            currentScreen.render(1f);
            currentBuffer.end();
            nextBuffer.begin();
            nextScreen.render(1f);
            nextBuffer.end();
            painted = true;
        }
        Batch batch = ScreenUtil.getBatch();
/*
        this.matrix = new Matrix4();
        this.matrix.setToOrtho2D(0, 0, currentBuffer.getColorBufferTexture().getWidth(),
                currentBuffer.getColorBufferTexture().getHeight());
        batch.setProjectionMatrix(this.matrix);
*/
        this.transition.render(batch, currentBuffer.getColorBufferTexture(),
                nextBuffer.getColorBufferTexture(), delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}