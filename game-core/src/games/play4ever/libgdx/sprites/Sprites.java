package games.play4ever.libgdx.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import games.play4ever.libgdx.Assets;

/**
 * Handles creation and management of sprite instances. In order to build an instance of an
 * animated sprite, use the corresponding static "createSprite" method. Then, as long as the
 * sprite is set to "visible", it will be rendered if the method "drawSprites()" of this class
 * is invoked. This will also execute any pending action of every sprite.
 *
 * @author Marcel Schoen
 */
public class Sprites {

    private static Array<AnimatedSprite> sprites = new Array<>(true, 256);

    /**
     * Calls the "reset" method on every sprite, to reset its state and the state of all its actions.
     */
    public static void resetAllSprites() {
        for(AnimatedSprite sprite : sprites) {
            sprite.reset();
        }
    }

    public static void pushSpriteIntoBackground(AnimatedSprite sprite) {
        int swapWith = sprites.indexOf(sprite, true);
        if(swapWith != 0) {
            Gdx.app.log(Sprites.class.getName(), "push sprite into background: " + sprite);
            sprites.swap(0, swapWith);
        }
    }

    public static void pushSpriteIntoForeground(AnimatedSprite sprite) {
        int swapWith = sprites.lastIndexOf(sprite, true);
        if(swapWith != sprites.size - 1) {
            Gdx.app.log(Sprites.class.getName(), ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> push sprite into foreground: " + sprite);
            sprites.swap(sprites.size - 1, swapWith);
        }
    }

    public static Array<AnimatedSprite> getSprites() {
        return sprites;
    }

    /**
     * Clears the list of sprites (freeing up all instances for garbage collection). After this
     * method is invoked, all sprite instances are gone and need to be created anew.
     */
    public static void clearAll() {
        sprites.clear();
    }

    /**
     * Create single sprite with given single animation (name is tag from Aseprite file).
     *
     * NOTE: This method should be invoked during an initialization phase only, not repeatedly
     * throughout the game loop, as it creates a new sprite object instance.
     *
     * @param animationName Animation name (equals tag in Aseprite file).
     */
    public static AnimatedSprite createSprite(String animationName) {
        AnimatedSprite sprite = new AnimatedSprite();
        addAnimationToSprite(sprite, animationName);
        sprite.showAnimation(animationName);
        sprites.add(sprite);
        return sprite;
    }

    public static AnimatedSprite createSprite(String animationName, int type) {
        AnimatedSprite sprite = createSprite(animationName);
        sprite.setType(type);
        return sprite;
    }

    /**
     * Create single sprite with given animations (names are tags from Aseprite file).
     *
     * NOTE: This method should be invoked during an initialization phase only, not repeatedly
     * throughout the game loop, as it creates a new sprite object instance.
     *
     * @param animationNames Array of animation names (equals tags in Aseprite file).
     */
    public static AnimatedSprite createSprite(List<String> animationNames) {
        AnimatedSprite sprite = new AnimatedSprite();
        for(String animationName : animationNames) {
            addAnimationToSprite(sprite, animationName);
        }
        sprites.add(sprite);
        return sprite;
    }

    /**
     * Create single sprite with given animations (names are tags from Aseprite file).
     *
     * NOTE: This method should be invoked during an initialization phase only, not repeatedly
     * throughout the game loop, as it creates a new sprite object instance.
     *
     * @param animationNames Array of animation names (equals tags in Aseprite file).
     * @param type The arbitrary custom type for the sprite.
     */
    public static AnimatedSprite createSprite(List<String> animationNames, int type) {
        AnimatedSprite sprite = createSprite(animationNames);
        sprite.setType(type);
        return sprite;
    }

    public static AnimatedSprite createSprite(TiledMapTile tiledMapTile, float duration, int type) {
        AnimatedSprite sprite = new AnimatedSprite(tiledMapTile, duration, type);
        sprite.setType(type);
        sprites.add(sprite);
        return sprite;
    }

    public static AnimatedSprite createSprite(AnimatedTiledMapTile animatedTiledMapTile, int type) {
        AnimatedSprite sprite = new AnimatedSprite(animatedTiledMapTile, type);
        sprite.setType(type);
        sprites.add(sprite);
        return sprite;
    }

    /**
     * Adds the animation with the specified name to the given sprite. An animation of that
     * same name must exist in the configuration.
     *
     * @param sprite The sprite instance to which to add the animation.
     * @param animationName The name of the animation to add.
     */
    private static void addAnimationToSprite(AnimatedSprite sprite, String animationName) {
        Animation<TextureRegion> animation = Assets.instance().getAnimation(animationName);
        sprite.addAnimation(animationName, animation);
    }

    /**
     * Draws all visible sprites (and also continues execution of every currently pending action of
     * each visible sprite). To completely exclude a sprite from being processed by this method,
     * set "setVisible(false)".
     *
     * This method should be invoked from the "render()" method of the game screen.
     *
     * @param batch The current SpriteBatch to draw the sprites on.
     * @param delta The time delta since the last screen refresh.
     */
    public static void drawSprites(SpriteBatch batch, float delta) {
        for(AnimatedSprite sprite : sprites) {
            sprite.executeCurrentAction(delta);
        }
        for(int zPlane=AnimatedSprite.BACKGROUND; zPlane<=AnimatedSprite.FOREGROUND; zPlane++) {
            for(AnimatedSprite sprite : sprites) {
                if(sprite.isVisible() && sprite.getZ() == zPlane) {
                    sprite.draw(batch, delta);
                }
            }

        }
    }
}
