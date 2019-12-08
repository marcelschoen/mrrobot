package com.jplay.gdx.sprites.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

/**
 * Helper class to build actions.
 *
 * NOTE: Actions should be created during the initialization phase of the game and then be re-used.
 * NOTE 2: Actions are stateful, so if there are 10 sprites that must perform the same action, 10
 *         action instances must be created too, one for each sprite instance.
 */
public class ActionBuilder {

    private Action firstAction = null;
    private Action action = null;

    /**
     * Creates an action builder which creates action for a given sprite.
     */
    public ActionBuilder() {
    }

    public ActionBuilder flickerBackground(Color color) {
        FlickerAction newAction = (FlickerAction)addAction(new FlickerAction());
        newAction.flickerBackground(color);
        return this;
    }

    public ActionBuilder setAnimation(String animationName) {
        SetAnimationAction newAction = (SetAnimationAction)addAction(new SetAnimationAction());
        newAction.setAnimation(animationName);
        return this;
    }

    public ActionBuilder setVisibility(boolean visible, float secDuration) {
        SetVisibilityAction newAction = (SetVisibilityAction)addAction(new SetVisibilityAction());
        newAction.setVisibility(visible, secDuration);
        return this;
    }

    public ActionBuilder stayPut(String animationName, float secDuration){
        StayPutAction newAction = (StayPutAction)addAction(new StayPutAction());
        newAction.stayPut(animationName, secDuration);
        return this;
    }

    public ActionBuilder stayPut(float secDuration){
        StayPutAction newAction = (StayPutAction)addAction(new StayPutAction());
        newAction.stayPut(secDuration);
        return this;
    }

    public ActionBuilder setPosition(float x, float y) {
        SetPositionAction newAction = (SetPositionAction)addAction(new SetPositionAction());
        newAction.setPosition(x, y);
        return this;
    }

    public ActionBuilder custom(Action customAction) {
        addAction(customAction);
        return this;
    }

    /**
     * Moves the sprite to a position relative to its current one. The movement happens within the
     * specified duration time, so the speed of the movement depends on the distance and the specified
     * time to cross that distance.
     *
     * This method allows to specify different interpolations for the x- and y-coordinate, so that
     * irregular, non-linear movements can be implemented by using different interpolations for
     * both axis.
     *
     * @param xOffset The x-coordinate offset in pixels.
     * @param yOffset The y-coordinate offset in pixels.
     * @param secDuration The duration of the movement, in seconds.
     * @param xInterpolation Optional: The interpolation algorithm to use for the x-coordinate.
     *                      If set to null, a linear interpolation will be used.
     * @param yInterpolation Optional: The interpolation algorithm to use for the y-coordinate.
     *                      If set to null, a linear interpolation will be used.
     * @return
     */
    public ActionBuilder moveTo(float xOffset, float yOffset, float secDuration,
                                Interpolation xInterpolation, Interpolation yInterpolation){
        MoveToAction newAction = (MoveToAction)addAction(new MoveToAction());
        newAction.moveTo(xOffset, yOffset, secDuration, xInterpolation, yInterpolation);
        return this;
    }

    /**
     * Moves the sprite to a position relative to its current one. The movement happens within the
     * specified duration time, so the speed of the movement depends on the distance and the specified
     * time to cross that distance.
     *
     * @param xOffset The x-coordinate offset in pixels.
     * @param yOffset The y-coordinate offset in pixels.
     * @param secDuration The duration of the movement, in seconds.
     * @param interpolation Optional: The interpolation algorithm to use (for both x- and y-values).
     *                      If set to null, a linear interpolation will be used.
     * @return
     */
    public ActionBuilder moveTo(float xOffset, float yOffset, float secDuration,
                                Interpolation interpolation){
        MoveToAction newAction = (MoveToAction)addAction(new MoveToAction());
        newAction.moveTo(xOffset, yOffset, secDuration, interpolation);
        return this;
    }

    /**
     * Build the action (or chain of actions).
     *
     * @return The action instance (may have follow-up actions).
     */
    public Action build() {
        if(firstAction == null) {
            throw new IllegalStateException("build() must only be invoked AFTER actions have been defined with other methods!");
        }
        return firstAction;
    }

    private Action addAction(Action newAction) {
        if(firstAction == null) {
            firstAction = newAction;
        }
        if(action != null) {
            action.followUpAction = newAction;
            newAction.precedingAction = action;
        }
        action = newAction;
        return newAction;
    }
}
