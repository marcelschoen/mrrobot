package games.play4ever.libgdx.sprites.action;

/**
 * Allows to set the animation of the sprite and / or
 * just wait for a given duration.
 *
 * @author Marcel Schoen
 */
public class StayPutAction extends Action {

    /** The name of the animation to use. */
    private String animationName = null;

    /**
     * Sets the animation name. The animation for the sprite will
     * be set once the action is executed.
     * Also sets the duration for the time to wait.
     *
     * @param animationName The name of the animation (can be null).
     * @param duration The time for the action; if set to 0 or smaller, it will be done immediately.
     */
    public void stayPut(String animationName, float duration) {
        this.animationName = animationName;
        setDuration(duration);
    }

    /**
     * Sets the duration for the time to wait.
     *
     * @param duration The time for the action; if set to 0 or smaller, it will be done immediately.
     */
    public void stayPut(float duration) {
        setDuration(duration);
    }

    @Override
    public void doStart() {
        if(animationName != null) {
            super.sprite.showAnimation(animationName);
        }
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
