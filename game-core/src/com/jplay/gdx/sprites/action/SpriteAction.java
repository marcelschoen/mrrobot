package com.jplay.gdx.sprites.action;

import com.jplay.gdx.sprites.AnimatedSprite;

/**
 * Interface for implementations of custom actions.
 */
public interface SpriteAction {

    public void start(AnimatedSprite sprite);

    public void execute(AnimatedSprite sprite, float delta);

    public boolean isFinished(AnimatedSprite sprite);
}
