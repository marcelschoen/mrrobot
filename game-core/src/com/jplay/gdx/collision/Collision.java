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
    private static CollisionRectangle checkOne = new CollisionRectangle();
    private static CollisionRectangle checkTwo = new CollisionRectangle();

    /**
     *
     */
    public static void initialize() {
        Array<AnimatedSprite> sprites = Sprites.getSprites();
        rectangles = new Array<>(sprites.size);
        for(AnimatedSprite sprite : sprites) {
            if(sprite != null) {
                addRectangles(sprite);
            }
        }
    }

    public static void addListener(CollisionListener listener) {
        listeners.add(listener);
    }

    public static void addRectangle(CollisionRectangle rectangle) {
        rectangles.add(rectangle);
    }
    public static void addRectangles(AnimatedSprite sprite) {
        Array<CollisionRectangle> rectangles = sprite.getCollisionBounds();
        for(int i = 0; i < rectangles.size; i++) {
            CollisionRectangle rectangle = rectangles.get(i);
            if(rectangle != null) {
                addRectangle(rectangle);
            }
        }
    }

    public static void notifyListeners(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle) {
        for(CollisionListener listener : listeners) {
            listener.spritesCollided(spriteOne, spriteTwo, overlapRectangle);
        }
    }

    /**
     * Runs collision checks on all registered rectangles (e.g. all animated sprites created
     * with the "Sprites" class). Invokes all registered collision listeners if a collision is detected.
     */
    public static void checkForCollisions() {
        for(int i = 0; i < rectangles.size - 1; i++) {
            for(int u = i + 1; u < rectangles.size; u++) {

                CollisionRectangle spriteOneRectangle = rectangles.get(i);
                AnimatedSprite spriteOne = spriteOneRectangle.getSprite();

                CollisionRectangle spriteTwoRectangle = rectangles.get(u);
                AnimatedSprite spriteTwo = spriteTwoRectangle.getSprite();

                if(spriteOne.isVisible() && spriteTwo.isVisible()) {
                    checkOne.setSprite(spriteOne);
                    checkOne.x = spriteOneRectangle.getSprite().getX() + spriteOneRectangle.x;
                    checkOne.y = spriteOneRectangle.getSprite().getY() + spriteOneRectangle.y;
                    checkOne.width = spriteOneRectangle.width;
                    checkOne.height = spriteOneRectangle.height;

                    checkTwo.setSprite(spriteTwo);
                    checkTwo.x = spriteTwoRectangle.getSprite().getX() + spriteTwoRectangle.x;
                    checkTwo.y = spriteTwoRectangle.getSprite().getY() + spriteTwoRectangle.y;
                    checkTwo.width = spriteTwoRectangle.width;
                    checkTwo.height = spriteTwoRectangle.height;

                    // TODO: FILTER OUT DISABLED / INACTIVE SPRITES
                    checkRectangles(checkOne, checkTwo);
                }
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
