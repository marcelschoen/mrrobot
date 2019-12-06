package com.jplay.gdx.collision;

import com.badlogic.gdx.math.Rectangle;
import com.jplay.gdx.sprites.AnimatedSprite;

/**
 * A rectangle used for collision detection. It carries an additional
 * type which allows to easily distinguish multiple collision rectangles
 * from one object. E.g. a sprite could have the default bound rectangle (which
 * is as large as the sprites texture image) and a smaller inner one that covers
 * only a small critical part like a cockpit; that inner rectangle would then
 * have a custom type value like 2. A listener for collision events can then use
 * that type value to determine if the outer or the inner bounds of the sprite
 * were causing the collision.
 *
 * @author Marcel Schoen
 */
public class CollisionRectangle extends Rectangle {

    public static int TYPE_DEFAULT = 0;

    /** Optional arbitrary type for collision rectangle. */
    private int type = -1;

    /** Optional sprite to which the rectangle belongs. */
    private AnimatedSprite sprite = null;

    public CollisionRectangle(int type) {
        this.type = type;
    }

    public CollisionRectangle(AnimatedSprite sprite, float x, float y, float width, float height, int type) {
        super(x, y, width, height);
        this.sprite = sprite;
        this.type = type;
    }

    public CollisionRectangle(float x, float y, float width, float height, int type) {
        super(x, y, width, height);
        this.type = type;
    }

    public CollisionRectangle(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public AnimatedSprite getSprite() {
        return sprite;
    }

    public int getType() {
        return type;
    }
}
