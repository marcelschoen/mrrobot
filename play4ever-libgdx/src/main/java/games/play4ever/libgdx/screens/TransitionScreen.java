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

    private ScreenTransition transition = ScreenTransitions.ALPHA_FADE.getTransition();

    /**
     * Creates a transition screen with a certain type of transition.
     *
     * @param transition The transition to use with this screen. Use different transition
     *                   screen instances to use different transitions.
     */
    public TransitionScreen(ScreenTransition transition) {
        this.transition = transition;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
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