package com.jplay.gdx.sprites.action;

/**
 * Action which sets the position of the sprite executing the action.
 *
 * @author Marcel Schoen
 */
public class SetPositionAction extends Action {

    /** The x-coordinate to change the position to. */
    private float x = 0;

    /** The y-coordinate to change the position to. */
    private float y = 0;

    /**
     * Will the position of the sprite to the given coordinates
     * once the action is executed.
     *
     * @param x The x-coordinate on screen (0 is left).
     * @param y The y-coordinate on screen (0 is lower border).
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void doStart() {
        super.sprite.setPosition(x, y);
    }

    @Override
    protected boolean isDone() {
        return true;
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
