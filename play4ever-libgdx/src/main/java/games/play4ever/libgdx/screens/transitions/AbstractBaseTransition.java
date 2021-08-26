package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;

import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.libgdx.screens.ScreenTransition;

/**
 * Base class for all transition implementations. It stores references to the framebuffers and
 * textures of both screens, and controls the duration timing.
 *
 * @author Marcel Schoen
 */
public abstract class AbstractBaseTransition implements ScreenTransition {

    private Game game;
    private AbstractBaseScreen currentScreen;
    private AbstractBaseScreen nextScreen;
    protected Matrix4 matrix = new Matrix4();
    protected Matrix4 startMatrix = new Matrix4();
    private FrameBuffer currentBuffer;
    private FrameBuffer nextBuffer;
    private Texture currentTexture;
    private Texture nextTexture;
    private float currentTransitionTime;
    private float transitionDuration;

    @Override
    public void setupTransition(Game game, float transitionDuration, AbstractBaseScreen currentScreen, AbstractBaseScreen nextScreen) {
        this.transitionDuration = transitionDuration;
        this.game = game;
        this.currentScreen = currentScreen;
        this.nextScreen = nextScreen;

        currentBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        nextBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        currentBuffer.begin();
        currentScreen.doRender(1f);
        currentBuffer.end();
        nextBuffer.begin();
        nextScreen.doRender(1f);
        nextBuffer.end();

        currentTexture = currentBuffer.getColorBufferTexture();
        nextTexture = nextBuffer.getColorBufferTexture();
    }

    /**
     * @return A re-set, freshly initialized Matrix4 instance. Re-uses and re-sets the same
     *         instance every time. Also sets it up for orthographic 2D projection.
     */
    protected Matrix4 getFreshMatrix4() {
        this.matrix.set(startMatrix);
        this.matrix.setToOrtho2D(0, 0, getCurrentTexture().getWidth(),
                getCurrentTexture().getHeight());
        return this.matrix;
    }

    @Override
    public void render(Batch batch, float delta) {
        float percent = currentTransitionTime / transitionDuration;
        doRender(batch, percent);
        currentTransitionTime += delta;
        if(currentTransitionTime >= transitionDuration) {
            getGame().setScreen(getNextScreen());
        }
    }

    /**
     * Actual transition implementations must implement this method to render the transitioning
     * images to the screen. They can access they framebuffers, textures etc. of the two
     * screens using the method of this parent class.
     *
     * @param batch The sprite batch used for rendering.
     * @param percent The percentage of completion, as a float value ranging from 0 (start) to 1 (done).
     */
    public abstract void doRender(Batch batch, float percent);

    /**
     * @return The texture of the current (starting) screen.
     */
    public Texture getCurrentTexture() {
        return currentTexture;
    }

    /**
     * @return The texture of the next (ending) screen.
     */
    public Texture getNextTexture() {
        return nextTexture;
    }

    /**
     * @return The framebuffer of the current (starting) screen.
     */
    public FrameBuffer getCurrentBuffer() {
        return currentBuffer;
    }

    /**
     * @return The framebuffer of the next (ending) screen.
     */
    public FrameBuffer getNextBuffer() {
        return nextBuffer;
    }

    /**
     * @return The current (starting) screen.
     */
    public AbstractBaseScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * @return The next (ending) screen.
     */
    public AbstractBaseScreen getNextScreen() {
        return nextScreen;
    }

    /**
     * @return The game instance on which to invoke "setScreen()"
     */
    public Game getGame() {
        return this.game;
    }
}
