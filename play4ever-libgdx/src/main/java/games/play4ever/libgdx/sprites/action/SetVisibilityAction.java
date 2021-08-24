package games.play4ever.libgdx.sprites.action;

/**
 * Sets the visibility of the sprite.
 *
 * @author Marcel Schoen
 */
public class SetVisibilityAction extends Action {

    /** Flag which indicates the visibility. */
    private boolean visible = false;

    /**
     * Sets the visibility of the sprite. If there should be a delay after changing the visibility,
     * set the duration to the required time - otherwise, just set it to 0 to complete the
     * action instantly.
     *
     * @param visible True if the sprite should be visible, false if not.
     * @param duration The duration for the action to take (wait after setting the visibility).
     */
    public void setVisibility(boolean visible, float duration) {
        this.visible = visible;
        setDuration(duration);
    }

    @Override
    public boolean isDone() {
        boolean done = executionTimer <= 0f;
        return done;
    }

    @Override
    public void doStart() {
        super.sprite.setVisible(visible);
    }

    @Override
    protected void execute(float delta) {
        // do nothing
    }
}
