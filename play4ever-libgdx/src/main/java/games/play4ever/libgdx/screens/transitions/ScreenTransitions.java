package games.play4ever.libgdx.screens.transitions;

import com.badlogic.gdx.math.Interpolation;

import games.play4ever.libgdx.screens.ScreenTransition;

/**
 * Set of pre-defined screen transitions to be used with the
 * "TransitionScreen" class.
 *
 * @author Marcel Schoen
 */
public enum ScreenTransitions {

    TO_BLACK_OR_WHITE(new ToBlackOrWhiteTransition()),

    ALPHA_FADE(new AlphaFadingTransition()),

    SLIDING_RIGHT(new SlidingTransition(SlidingTransition.Direction.RIGHT, Interpolation.exp10Out, false)),

    SLIDING_LEFT(new SlidingTransition(SlidingTransition.Direction.LEFT, Interpolation.exp10Out, false))

    ;

    private ScreenTransition transition;

    ScreenTransitions(ScreenTransition transition) {
        this.transition = transition;
    }

    public ScreenTransition getTransition() {
        return this.transition;
    }

}
