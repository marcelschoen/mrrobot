package com.jplay.gdx.collision;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.Sprites;

/**
 * Manages collision detections between sprites etc. This class
 *
 * @author Marcel Schoen
 */
public class Collision {

    private static Array<CollisionListener> listeners = new Array<>(10);

    private static Array<CollisionRectangle> rectangles = new Array<>(50);

    private static Rectangle overlapRectangle = new Rectangle();

    /**
     *
     */
    public static void initialize() {
        Array<AnimatedSprite> sprites = Sprites.getSprites();
        rectangles = new Array<>(sprites.size);
        for(AnimatedSprite sprite : sprites) {
            addRectangles(sprite);
        }
    }

    public static void addListener(CollisionListener listener) {
        listeners.add(listener);
    }

    public static void addRectangle(CollisionRectangle rectangle) {
        rectangles.add(rectangle);
    }
    public static void addRectangles(AnimatedSprite sprite) {
        for(CollisionRectangle rectangle : sprite.getCollisionBounds()) {
            addRectangle(rectangle);
        }
    }

    public static void notifyListeners(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle) {
        for(CollisionListener listener : listeners) {
            listener.spritesCollided(spriteOne, spriteTwo, overlapRectangle);
        }
    }

    public static void checkForCollisions() {
        for(int i = 0; i < rectangles.size - 1; i++) {
            for(int u = i + 1; u < rectangles.size; u++) {
                // TODO: FILTER OUT DISABLED / INACTIVE SPRITES
                checkRectangles(rectangles.get(i), rectangles.get(u));
            }
        }
    }

    private static void checkRectangles(CollisionRectangle one, CollisionRectangle two) {
        if(Intersector.intersectRectangles(one, two, overlapRectangle)) {
            notifyListeners(one.getSprite(), two.getSprite(), overlapRectangle);
        }
    }


    /**
     * Processes the given tiled map to provide collision detection for all of its tiles.
     *
     * @param tiledMap
     */
    public static void processTileMap(TiledMap tiledMap) {

    }

    public static void registerCollisionsForMapTiles(Vector2 position) {

    }
    public static void registerCollisionsForMapTiles(Array<Vector2> position) {

    }
    public static void registerCollisionsForAllMapTiles() {

    }
}
