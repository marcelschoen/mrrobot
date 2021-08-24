package games.play4ever.libgdx.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;

import games.play4ever.libgdx.screens.transitions.ScreenTransitions;

/**
 * Screen which shows a transition from one screen to the next. Once the transition
 * is complete, the game instance will be set to the "next" screen.
 *
 * @author Marcel Schoen
 */
public class TransitionScreen implements Screen {

    private Screen currentScreen;
    private Screen nextScreen;


    private ScreenTransition transition = ScreenTransitions.ALPHA_FADE.getTransition();

    private boolean painted = false;

    /**
     * Creates a transition screen with a certain type of transition.
     *
     * @param transition The transition to use with this screen. Use different transition
     *                   screen instances to use different transitions.
     */
    public TransitionScreen(ScreenTransition transition) {
        this.currentScreen = transition.getCurrentScreen();
        this.nextScreen = transition.getNextScreen();
        this.transition = transition;
    }

    @Override
    public void show() {
        painted = false;
    }

    @Override
    public void render(float delta) {

        if(!painted) {
/*
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

 */
            painted = true;
        }
        Batch batch = ScreenUtil.getBatch();
        this.transition.render(batch, delta);
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