package games.play4ever.libgdx.sprites.action;

import com.badlogic.gdx.graphics.Color;

public class FlickerAction extends Action {

    private Color color = null;

    public void flickerBackground(Color color) {
        this.color = color;
    }

    @Override
    public void doStart() {
        // do nothing
    }

    @Override
    protected void execute(float delta) {
        // do nothing
    }

    @Override
    protected boolean isDone() {
        return true;
    }
}
