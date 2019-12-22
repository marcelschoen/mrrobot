package games.play4ever.libgdx.collision;

import com.badlogic.gdx.math.Rectangle;

import games.play4ever.libgdx.sprites.AnimatedSprite;

public interface CollisionListener {

    void spritesCollided(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle);
}
