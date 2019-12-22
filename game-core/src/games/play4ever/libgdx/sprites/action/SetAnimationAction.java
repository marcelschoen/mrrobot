package games.play4ever.libgdx.sprites.action;

/**
 * Sets a specific animation for the sprite which executes the action.
 * NOTE: The given animation must have been added to the sprite before.
 *
 * @author Marcel Schoen
 */
public class SetAnimationAction extends Action {

    /** The name of the animation to set (e.g. tag from Aseprite) */
    private String animationName = null;

    /**
     * Sets the animation name. The animation for the sprite will
     * be set once the action is executed.
     *
     * @param animationName Name of animation to show.
     */
    public void setAnimation(String animationName) {
        this.animationName = animationName;
    }

    @Override
    public void doStart() {
        super.sprite.showAnimation(animationName);
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
