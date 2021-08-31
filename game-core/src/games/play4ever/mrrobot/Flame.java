package games.play4ever.mrrobot;

import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import games.play4ever.libgdx.collision.Collision;
import games.play4ever.libgdx.sprites.AnimatedSprite;
import games.play4ever.libgdx.sprites.Sprites;
import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.libgdx.sprites.action.ActionBuilder;

import static games.play4ever.mrrobot.Tiles.NO_TILE;

public class Flame {

    private static final Random random = new Random();

    public static final float FLAME_MOVEMENT_SPEED = 10f;
    public static final FlameMovement MOVE_RIGHT = new FlameMovement(FLAME_STATE.WALKING_RIGHT, FLAME_MOVEMENT_SPEED, 0);
    public static final FlameMovement MOVE_LEFT = new FlameMovement(FLAME_STATE.WALKING_LEFT, FLAME_MOVEMENT_SPEED * -1, 0);
    public static final FlameMovement MOVE_UP = new FlameMovement(FLAME_STATE.CLIMBING_UP, 0, FLAME_MOVEMENT_SPEED);
    public static final FlameMovement MOVE_DOWN = new FlameMovement(FLAME_STATE.CLIMBING_DOWN, 0, FLAME_MOVEMENT_SPEED * -1);

    private Action killFlameAction;
    private FlameMovement currentMovement = MOVE_LEFT;
    private float movementTimer = 0f;

    public enum ANIM {
        flame_right,
        flame_left,
        flame_blue
    };

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 40;
    private static final float ROLLING_SPEED = 26;

    private AnimatedSprite sprite = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private TileMap tileMap;

    public static List<Flame> flames = new ArrayList<>();

    public enum FLAME_STATE {
        DYING(true, ANIM.flame_blue.name()),
        CLIMBING_UP(true, ANIM.flame_right.name()),
        CLIMBING_DOWN(true, ANIM.flame_left.name()),
        WALKING_RIGHT(true, ANIM.flame_right.name()),
        WALKING_LEFT(false, ANIM.flame_left.name());

        private boolean isFacingRight = true;
        private String animationName = null;

        FLAME_STATE(boolean isFacingRight, String animationName) {
            this.isFacingRight = isFacingRight;
            this.animationName = animationName;
        }

        public String getAnimationName() { return this.animationName; }

        public boolean isFacingRight() {
            return this.isFacingRight;
        }
    }
    private FLAME_STATE flameState = FLAME_STATE.WALKING_RIGHT;

    private boolean dying = false;

    public boolean isDying() {
        return dying;
    }

    public Flame() {
        List<String> animationNames = new ArrayList<>();
        for(ANIM animation : ANIM.values()) {
            animationNames.add(animation.name());
        }
        this.sprite = Sprites.createSprite(animationNames, SpriteTypes.FLAMES);
        this.sprite.setVisible(true);
        this.sprite.setDefaultCollisionBounds(6, 3, 10, 10);
        Collision.addRectangles(this.sprite);

        killFlameAction = new ActionBuilder()
                .setAnimation(Flame.ANIM.flame_blue.name())
                .moveTo(4, 5, 0.2f, Interpolation.bounce, null)
                .moveTo(2, -200, 1.5f, Interpolation.slowFast, null)
                .setVisibility(false, 0f)
                .build();
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public static Flame getFlameOfSprite(AnimatedSprite sprite) {
        for(Flame flame : flames) {
            if(flame.getSprite() == sprite) {
                return flame;
            }
        }
        return null;
    }

    /**
     * Makes the flame move around.
     *
     * @param delta
     */
    public void move(float delta) {
        if(movementTimer == 0f) {
            // determine new direction
            if(currentMovement == MOVE_LEFT || currentMovement == MOVE_RIGHT) {

            } else {

            }
            movementTimer = random.nextInt(8) + 3;
        }

        float oldX = getX();
        float oldY = getY();
        float newX = oldX + currentMovement.xSpeed * delta;
        float newY = oldY + currentMovement.ySpeed * delta;
        setPosition(newX, newY);

        boolean resetAndChangeDirection = false;
        if(tileBelowId == NO_TILE) {
            resetAndChangeDirection = true;
        } else if(SpriteUtil.isCrossingLeftScreenBoundary(newX)) {
            resetAndChangeDirection = true;
        } else if(SpriteUtil.isCrossingRightScreenBoundary(newX)) {
            resetAndChangeDirection = true;
        }


        if(resetAndChangeDirection) {
            if(currentMovement == MOVE_LEFT) {
                currentMovement = MOVE_RIGHT;
            } else if(currentMovement == MOVE_RIGHT) {
                currentMovement = MOVE_LEFT;
            }
        }

        if(this.flameState != currentMovement.flame_state) {
            setState(currentMovement.flame_state);
        }
    }

    public void die() {
        if(!dying) {
            dying = true;
            this.sprite.startAction(killFlameAction, null);
        }
    }

    public AnimatedSprite getSprite() {
        return this.sprite;
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
        tileBehindId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    public void setState(FLAME_STATE state) {
        this.dying = false;
        this.flameState = state;
        this.sprite.showAnimation(state.getAnimationName());
    }

    public float getX() {
        return this.sprite.getX();
    }

    public float getY() {
        return this.sprite.getY();
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his feet (lower sprite boundary).
     */
    public boolean isNearlyAlignedVertically() {
        float y = getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }
}
