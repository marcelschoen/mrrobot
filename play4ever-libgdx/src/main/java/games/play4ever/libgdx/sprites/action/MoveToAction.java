package games.play4ever.libgdx.sprites.action;

import com.badlogic.gdx.math.Interpolation;

/**
 * Moves a sprite to a position relative to its current one.
 *
 * Package-restricted access because the functionality of this class
 * is to be accessed through "ActionBuilder".
 *
 * @author Marcel Schoen
 */
public class MoveToAction extends Action {

    protected Interpolation interpolationX = Interpolation.linear;
    protected Interpolation interpolationY = Interpolation.linear;
    protected float xOffset;
    protected float yOffset;
    protected float startX;
    protected float startY;
    protected float targetX;
    protected float targetY;

    /**
     * @param xOffset The horizontal offset in pixels.
     * @param yOffset The vertical offset in pixels.
     * @param secDuration The duration of the movement in number of seconds.
     */
    public void moveTo(float xOffset, float yOffset, float secDuration) {
        moveTo(xOffset, yOffset, secDuration, null, null);
    }

    /**
     * @param xOffset The horizontal offset in pixels.
     * @param yOffset The vertical offset in pixels.
     * @param secDuration The duration of the movement in number of seconds.
     * @param interpolation The type of interpolation to use (instead of linear).
     */
    /*
    public void moveTo(float xOffset, float yOffset, float secDuration,
                       Interpolation interpolationX) {
        moveTo(xOffset, yOffset, secDuration, interpolationX, null);
    }

     */

    /**
     *
     * @param xOffset The horizontal offset in pixels.
     * @param yOffset The vertical offset in pixels.
     * @param secDuration The duration of the movement in number of seconds.
     * @param interpolationX The type of interpolation to use for the X-coordinate (instead of linear). Will be linear if parameter is null.
     * @param interpolationY The type of interpolation to use for the Y-coordinate (instead of linear). Will be linear if parameter is null.
     */
    public void moveTo(float xOffset, float yOffset, float secDuration,
                       Interpolation interpolationX,
                       Interpolation interpolationY) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        if(interpolationX != null) {
            this.interpolationX = interpolationX;
        }
        if(interpolationY != null) {
            this.interpolationY = interpolationY;
        }
        this.executionDuration = secDuration;
    }

    @Override
    public void doStart() {
        this.startX = super.sprite.getX();
        this.startY = super.sprite.getY();
        this.targetX = this.startX + xOffset;
        this.targetY = this.startY + yOffset;

    }

    @Override
    protected void execute(float delta) {
        if(executionTimer > 0) {
            float state = 1f - (executionTimer / executionDuration);
            super.sprite.setX(interpolationX.apply(startX, targetX, state));
            super.sprite.setY(interpolationY.apply(startY, targetY, state));
            /*
            if(interpolation2 != null) {
                super.sprite.setX(interpolation2.apply(startX, targetX, state));
            } else {
                super.sprite.setX(interpolation.apply(startX, targetX, state));
            }
            if(interpolation2 != null) {
                super.sprite.setY(interpolation2.apply(startY, targetY, state));
            } else {
                super.sprite.setY(interpolation.apply(startY, targetY, state));
            }

             */
        }
    }
}
