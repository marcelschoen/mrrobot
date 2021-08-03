package games.play4ever.mrrobot;

import games.play4ever.libgdx.sprites.AnimatedSprite;

/**
 * Holds information related to one trampoline.
 *
 * @author Marcel Schoen
 */
public class Trampoline {

    private AnimatedSprite sprite = null;

    public Trampoline(AnimatedSprite sprite) {
        this.sprite = sprite;
    }

    public AnimatedSprite getSprite() {
        return sprite;
    }
}
