package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;

import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.libgdx.screens.ScreenTransition;

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

    public void setupTransition(Game game, AbstractBaseScreen currentScreen, AbstractBaseScreen nextScreen) {
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

    public Texture getCurrentTexture() {
        return currentTexture;
    }

    public Texture getNextTexture() {
        return nextTexture;
    }

    public FrameBuffer getCurrentBuffer() {
        return currentBuffer;
    }

    public FrameBuffer getNextBuffer() {
        return nextBuffer;
    }

    public AbstractBaseScreen getCurrentScreen() {
        return currentScreen;
    }

    public AbstractBaseScreen getNextScreen() {
        return nextScreen;
    }

    public Game getGame() {
        return this.game;
    }
}
