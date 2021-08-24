/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/JPlaySprite.java,v 1.1 2013/02/11 23:53:37 msc Exp $
 * 
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package games.play4ever.libgdx.sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.HashMap;
import java.util.Map;

import games.play4ever.libgdx.collision.CollisionRectangle;
import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.libgdx.sprites.action.ActionListener;

import static games.play4ever.libgdx.collision.CollisionRectangle.TYPE_DEFAULT;

/**
 * Wrapper for LIBGDX sprite. Holds animation state
 * information as well, handles execution of actions
 * and collision detection.
 *
 * @author Marcel Schoen
 * @version $Revision: 1.1 $
 */
public class AnimatedSprite extends Sprite implements Pool.Poolable {

	private Array<CollisionRectangle> collisionBounds = new Array<>(0);

	/** Visibility flag. */
	private boolean visible = false;

	/** Stores the animation. */
	private Animation<TextureRegion> animation = null;

	/** Used to determine which key frame to render during render() invocation. */
	private float animationStateTime = 0f;

	/** Animations set in initialization phase. Key = tag name in Aseprite */
    private Map<String, Animation<TextureRegion>> animationMap = new HashMap<>();

	/** Points to action currently being executed. */
	private Action currentAction = null;

	/** Optional arbitrary type to assign to the sprite. */
	private int type = -1;

	private AnimatedSprite attachedToSprite = null;

	private int zPlane = 0;
	public static final int FOREGROUND = 255;
	public static final int BACKGROUND = 0;

	private float xOffsetAttachement = 0;
	private float yOffsetAttachement = 0;

	/**
	 * Creates a sprite with a certain animation.
	 */
	public AnimatedSprite() {
	}

	public AnimatedSprite(int type) {
		this();
		this.type = type;
	}

	public AnimatedSprite(int type, int zPlane) {
		this();
		this.type = type;
		setZ(zPlane);
	}

	/**
	 * Create an animated sprite from an animated Tiled-map tile.
	 *
	 * @param tiledMapTile The Tiled map tile from which to create a sprite.
	 * @param duration The time in ms for how long to show one frame.
	 * @param type The arbitrary sprite type.
	 */
	public AnimatedSprite(TiledMapTile tiledMapTile, float duration, int type) {
		setType(type);
		TextureRegion texture = tiledMapTile.getTextureRegion();
		initializeAnimationAndType(new TextureRegion[] { texture }, "tile-" + tiledMapTile.getId(), duration, type, BACKGROUND);
	}

	/**
	 * Create an animated sprite from an animated Tiled-map tile.
	 *
	 * @param tiledMapTile The Tiled map tile from which to create a sprite.
	 * @param duration The time in ms for how long to show one frame.
	 * @param type The arbitrary sprite type.
	 * @param zPlane The z-order which defines if the sprite is drawn behind other sprites or before them
	 *               (see constants FOREGROUND and BACKGROUND). Must be a value ranging from 0 (BACKGROUND)
	 *               to 255 (FOREGROUND).
	 */
	public AnimatedSprite(TiledMapTile tiledMapTile, float duration, int type, int zPlane) {
		setType(type);
		TextureRegion texture = tiledMapTile.getTextureRegion();
		initializeAnimationAndType(new TextureRegion[] { texture }, "tile-" + tiledMapTile.getId(), duration, type, zPlane);
	}

	/**
	 * Create an animated sprite from an animated Tiled-map tile.
	 *
	 * @param animatedTiledMapTile The animated Tiled map tile from which to create a sprite.
	 * @param type The arbitrary sprite type.
	 */
	public AnimatedSprite(AnimatedTiledMapTile animatedTiledMapTile, int type) {
		setType(type);

		float duration = animatedTiledMapTile.getAnimationIntervals()[0] / 1000f;

		StaticTiledMapTile[] tiles = animatedTiledMapTile.getFrameTiles();
		TextureRegion[] textures = new TextureRegion[tiles.length];

		int ct = 0;
		for(StaticTiledMapTile tile : tiles) {
			textures[ct++] = tile.getTextureRegion();
		}

		initializeAnimationAndType(textures, "tile-" + animatedTiledMapTile.getId(), duration, type, BACKGROUND);
	}

	/**
	 * Initialize an animated sprite from a set of texture regions.
	 *
	 * @param textures The texture regions with the graphics for the animation frames.
	 * @param animationName The name of the animation.
	 * @param duration The time in ms for how long to show one frame.
	 * @param type The sprite type.
	 * @param zPlane The z-order which defines if the sprite is drawn behind other sprites or before them
	 *               (see constants FOREGROUND and BACKGROUND). Must be a value ranging from 0 (BACKGROUND)
	 *               to 255 (FOREGROUND).
	 */
	private void initializeAnimationAndType(TextureRegion[] textures, String animationName, float duration, int type, int zPlane) {
		setType(type);
		setZ(zPlane);

		Animation<TextureRegion> animation = new Animation<>(duration, textures);
		animation.setPlayMode(Animation.PlayMode.LOOP);

		addAnimation(animationName, animation);
		showAnimation(animationName);
	}

	public int getZ() {
		return zPlane;
	}

	public void setZ(int zPlane) {
		if(zPlane < BACKGROUND || zPlane > FOREGROUND) {
			throw new IllegalArgumentException("invalid z-plane: " + zPlane + ", value must range from " + BACKGROUND + " to " + FOREGROUND + "!");
		}
		this.zPlane = zPlane;
	}

	/**
	 * Sets the default collision bounds for this sprite. Collision detection is only possible for
	 * the sprite until this method has been invoked.
	 *
	 * Note that the screen coordinate system starts in the lower left corner, and positive x-axis
	 * values are going to the right, and positive y-axis values upwards.
	 *
	 * @param xOffset The x-offset of the collision bounds rectangle, relative to the lower left corner of the sprite.
	 * @param yOffset The y-offset of the collision bounds rectangle, relative to the lower left corner of the sprite.
	 * @param width The width of the collision bounds rectangle.
	 * @param height The height of the collision bounds rectangle.
	 */
	public void setDefaultCollisionBounds(float xOffset, float yOffset, float width, float height) {
		collisionBounds = new Array<>(2);
		collisionBounds.add(new CollisionRectangle(this, xOffset, yOffset, width, height, TYPE_DEFAULT));
	}

	/**
	 * Sets the default collision bounds for this sprite. Collision detection is only possible for
	 * the sprite until this method has been invoked.
	 *
	 * This method requires that at least one animation had been set before, as it will automatically
	 * use the dimensions of the first frame of that animation for the collision bounds rectangle.
	 */
	public void setDefaultCollisionBounds() {
		if(this.animationMap.size() > 0) {
			Animation<TextureRegion> animation = this.animationMap.values().iterator().next();
			TextureRegion frame = animation.getKeyFrame(0);
			setDefaultCollisionBounds(1, 1, frame.getRegionWidth() -2 , frame.getRegionHeight() - 2);
		}
	}

	/**
	 * Add an additional collision bounds rectangle to the sprite. For example, in a typical bullet hell
	 * shooter, the regular outer bounds of the jet fighter sprite might be used only for collision detection
	 * with power-up items, but for collisions with bullets, a smaller rectangle which covers only the
	 * cockpit would be used.
	 *
	 * So, in a sprite that has dimensions of 16x16 pixels, in order to create a collision bounds
	 * rectangle which covers only the core 4x4 pixel area at the center of the sprite, the rectangle
	 * would need the relative values 6 (x-offset), 6 (y-offset, 4 (width) and 4(height).
	 *
	 * @param xOffset The x-offset of the collision bounds rectangle, relative to the lower left corner of the sprite.
	 * @param yOffset The y-offset of the collision bounds rectangle, relative to the lower left corner of the sprite.
	 * @param width The width of the collision bounds rectangle.
	 * @param height The height of the collision bounds rectangle.
	 * @param type An arbitrary numeric type to assign this rectangle, so that in the logic which deals
	 *             with the collision, it is possible to distinguish between the multiple collision rectangles
	 *             of this sprite.
	 */
	public void addCollisionBounds(float xOffset, float yOffset, float width, float height, int type) {
		for(int i = 0; i < collisionBounds.size; i++) {
			CollisionRectangle rectangle = collisionBounds.get(i);
			if(rectangle != null && rectangle.getType() == type) {
				// If a bounds rectangle of this type already exists, just update its dimensions
				rectangle.x = xOffset;
				rectangle.y = yOffset;
				rectangle.width = width;
				rectangle.height = height;
				return;
			}
		}
		// Otherwise add a new collision bounds rectangle for the given type
		collisionBounds.add(new CollisionRectangle(this, xOffset, yOffset, width, height, type));
	}

    public Array<CollisionRectangle> getCollisionBounds() {
        return this.collisionBounds;
    }

	/**
	 * Attaches this sprite to another sprite, with a given x- and y-offset. The result is that
	 * whenever the other sprite changes its position, this sprite will automatically have its
	 * position updated accordingly.
	 *
	 * A typical use-case would be a sprite that shows a status effect on a game character after picking
	 * up some power-up item. Or in a shooter like R-Type, when an extension to the player's ship is
	 * picked up, it is visibly attached to the ship sprite.
	 *
	 * @param otherSprite The sprite to which to attach this sprite.
	 * @param xOffset The x-offset relative to the position of the other sprite.
	 * @param yOffset The y-offset relative to the position of the other sprite.
	 */
	public void attachToSprite(AnimatedSprite otherSprite, float xOffset, float yOffset) {
		attachedToSprite = otherSprite;
		xOffsetAttachement = xOffset;
		yOffsetAttachement = yOffset;
	}

	/**
	 * Detaches this sprite from the given other sprite, if it is attached to it.
	 *
	 * @param otherSprite The sprite from which to detach this one.
	 */
	public void detachFromSprite(AnimatedSprite otherSprite) {
		attachedToSprite = null;
	}

	/**
	 * Returns the type of this sprite (defaults to 0).
	 *
	 * @return the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Allows to set a type. This is an arbitrary value you can use to easily distinguish different
	 * types of sprites. For example, create an int constant "BULLET=1" which you then assign to
	 * all bullet sprites, so you can easily determine which type of sprite it is in collision
	 * handling logic.
	 *
	 * @param type The type value to set.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return True if the sprite is currently visible.
	 */
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void reset() {
		currentAction = null;
	}

	/**
	 * Lets the sprite execute an action (which can contain follow-up actions, in which case
	 * the entire chain of actions will be executed one by one).
	 *
	 * @param action The action to execute.
	 */
	public void startAction(Action action, ActionListener listener) {
		action.start(this, listener);
		currentAction = action;
	}

	/**
	 * Shows or hides the sprite (if set to "false", it will no longer
	 * be rendered on screen).
	 *
	 * @param visible Visibility value.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Adds an animation to this sprite instance.
	 *
	 * NOTE 1: This method should be invoked only during the initialization phase of the game.
	 *
	 * @param name The animation name.
	 * @param animation The animation texture regions.
	 */
	public void addAnimation(String name, Animation<TextureRegion> animation) {
        animationMap.put(name, animation);
    }

	/**
	 * Starts the given animation for this sprite.
	 *
	 * @param name The name of the animation.
	 */
	public void showAnimation(String name) {
		showAnimation(name, Animation.PlayMode.LOOP);
	}

	/**
	 * Starts the given animation for this sprite.
	 *
	 * @param name The name of the animation.
	 * @param mode The play mode for the animation.
	 */
	public void showAnimation(String name, Animation.PlayMode mode) {
		this.animation = animationMap.get(name);
		this.animationStateTime = 0f;
		this.animation.setPlayMode(mode);
		if(this.animation == null) {
			throw new IllegalArgumentException("No animation found for name '" + name + "'");
		}
	}

	/**
	 * Draws the sprite into a given spriteBatch at the current position.
	 *
	 * @param batch The target spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 */
	public void draw(SpriteBatch batch, float delta) {
		float x = getX();
		float y = getY();
		if(attachedToSprite != null) {
			x = attachedToSprite.getX();
			y = attachedToSprite.getY();
		}
		draw(batch, x, y, delta);
	}

	/**
	 * Executes the current actions, no matter if the sprite is visible or not.
	 * Invoke this method just right before invoking "draw()", if actions should
	 * be executed. The "Sprites" class does this automatically.
	 *
	 * @param delta The time in seconds since last refresh.
	 * @see {@link games.play4ever.libgdx.sprites.Sprites}
	 */
	public void executeCurrentAction(float delta) {
		if(currentAction != null) {
			synchronized (currentAction) {
				if(currentAction.isRunning()) {
					currentAction.executeAction(delta);
					if (currentAction.isCompleted()) {
						currentAction = currentAction.runFollowUpAction();
					}
				} else {
					currentAction = null;
				}
			}
		}
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	/**
	 * Draws the sprite into a given spriteBatch. T
	 * he "Sprites" class invokes this method
	 * automatically for all registered sprites.
	 * 
	 * @param batch The target spriteBatch.
	 * @param xPosition The x-coordinate (in pixels) within the spriteBatch.
	 * @param yPosition The y-coordinate (in pixels) within the spriteBatch.
	 * @param delta The time in seconds since last refresh.
	 * @see {@link games.play4ever.libgdx.sprites.Sprites}
	 */
	public void draw(SpriteBatch batch, float xPosition, float yPosition, float delta) {

		if(batch == null) {
			throw new IllegalArgumentException("batch must not be null");
		}
		if(this.animation == null) {
			throw new IllegalStateException("Animation not ready.");
		}
		this.animationStateTime += delta;
		if(this.animation.getPlayMode() != Animation.PlayMode.NORMAL && this.animationStateTime > 1.0) {
			this.animationStateTime -= 1f;
		}
		TextureRegion keyFrame = this.animation.getKeyFrames()[0];
		if(this.animation.getPlayMode() != Animation.PlayMode.NORMAL || !this.animation.isAnimationFinished(this.animationStateTime)) {
			keyFrame = this.animation.getKeyFrame(this.animationStateTime);
		} else {
			// If animation is finished, stay on last frame.
			TextureRegion[] textures = this.animation.getKeyFrames();
			keyFrame = textures[textures.length - 1];
		}
		// Update default sprite bounds with values matching current animation frame
		batch.draw(keyFrame, xPosition, yPosition);
	}
}
