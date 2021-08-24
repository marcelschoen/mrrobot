package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Matrix4;

import games.play4ever.libgdx.screens.ScreenTransition;

public abstract class AbstractBaseTransition implements ScreenTransition {

    private Game game;
    private Screen currentScreen;
    private Screen nextScreen;
    protected Matrix4 matrix = new Matrix4();

    public void setupTransition(Game game, Screen currentScreen, Screen nextScreen) {
        this.game = game;
        this.currentScreen = currentScreen;
        this.nextScreen = nextScreen;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public Screen getNextScreen() {
        return nextScreen;
    }

    public Game getGame() {
        return this.game;
    }
}
