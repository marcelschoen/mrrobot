package com.jplay.gdx.collision;

import com.badlogic.gdx.math.Rectangle;
import com.jplay.gdx.sprites.AnimatedSprite;

public interface CollisionListener {

    void spritesCollided(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle);
}
