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
import static games.play4ever.mrrobot.Tiles.TILE_BOMB_EXPLODING;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_LEFT;

public class Flame {

    public static final float FLAME_MOVEMENT_SPEED = 25f;

    private static final Random random = new Random();

    private Action killFlameAction;
    private FlameMovement currentMovement = FlameMovement.LEFT;
    private float movementTimer = 0f;

    // For debugging purposes, a flame can be disabled and be held in place
    private boolean disabled = false;

    static {
        FlameMovement.RIGHT.setAlternateMovements(new FlameMovement[] { FlameMovement.LEFT, FlameMovement.UP, FlameMovement.DOWN });
        FlameMovement.RIGHT.setForcedAlternateMovements(new FlameMovement[] { FlameMovement.UP, FlameMovement.DOWN });
        FlameMovement.LEFT.setAlternateMovements(new FlameMovement[] { FlameMovement.RIGHT, FlameMovement.UP, FlameMovement.DOWN });
        FlameMovement.LEFT.setForcedAlternateMovements(new FlameMovement[] { FlameMovement.UP, FlameMovement.DOWN });
        FlameMovement.UP.setAlternateMovements(new FlameMovement[] { FlameMovement.LEFT, FlameMovement.RIGHT, FlameMovement.DOWN });
        FlameMovement.UP.setForcedAlternateMovements(new FlameMovement[] { FlameMovement.LEFT, FlameMovement.RIGHT });
        FlameMovement.DOWN.setAlternateMovements(new FlameMovement[] { FlameMovement.LEFT, FlameMovement.RIGHT, FlameMovement.UP });
        FlameMovement.DOWN.setForcedAlternateMovements(new FlameMovement[] { FlameMovement.LEFT, FlameMovement.RIGHT });
    }

    public enum ANIM {
        flame_right,
        flame_left,
        flame_blue
    };

    private AnimatedSprite sprite = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileBelowLeftId = NO_TILE;
    private int tileBelowRightId = NO_TILE;
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

    public void setCurrentMovement(FlameMovement currentMovement) {
        this.currentMovement = currentMovement;
    }

    /**
     * Makes the flame move around.
     *
     * @param delta
     */
    public void move(float delta) {
        if(isDying() || disabled) {
            return;
        }

        float newX = sprite.getX() + currentMovement.getxSpeed() * delta;
        float newY = sprite.getY() + currentMovement.getySpeed() * delta;
//        float newX = sprite.getX();
//        float newY = sprite.getY();
        setPosition(newX, newY);
        if(this.flameState != currentMovement.getState()) {
            setState(currentMovement.getState());
        }

        if(currentMovement == FlameMovement.LEFT) {
            moveLeft();
        } else if(currentMovement == FlameMovement.RIGHT) {
            moveRight();
        } else if(currentMovement == FlameMovement.UP) {
            moveUp();
        } else if(currentMovement == FlameMovement.DOWN) {
            moveDown();
        }

        if(tileBelowId == TILE_BOMB_EXPLODING) {
            die();
        }
    }

    private void moveLeft() {
        if(tileBelowLeftId == NO_TILE) {
            currentMovement = FlameMovement.RIGHT;
        }
        if(SpriteUtil.isAlignedHorizontally(sprite)) {
            if(random.nextInt(100) > 85) {
                if(tileFurtherBelowId == TILE_LADDER_LEFT) {
                    currentMovement = FlameMovement.DOWN;
                }
            }
            if(random.nextInt(100) > 85) {
                if(tileBehindId == TILE_LADDER_LEFT) {
                    currentMovement = FlameMovement.UP;
                }
            }
        }
    }
    private void moveRight() {
        if(tileBelowRightId == NO_TILE) {
            currentMovement = FlameMovement.LEFT;
        }
        if(SpriteUtil.isAlignedHorizontally(sprite)) {
            if(random.nextInt(100) > 85) {
                if(tileFurtherBelowId == TILE_LADDER_LEFT) {
                    currentMovement = FlameMovement.DOWN;
                }
            }
            if(random.nextInt(100) > 85) {
                if(tileBehindId == TILE_LADDER_LEFT) {
                    currentMovement = FlameMovement.UP;
                }
            }
        }
    }
    private void moveUp() {
        if(SpriteUtil.isAlignedVertically(sprite)) {
            if(tileBehindId != TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_LEFT) {
                if(random.nextInt(100) > 95) {
                    currentMovement = FlameMovement.DOWN;
                } else {
                    if(random.nextInt(100) > 50) {
                        currentMovement = FlameMovement.LEFT;
                    } else {
                        currentMovement = FlameMovement.RIGHT;
                    }
                }
            }
        }
    }
    private void moveDown() {
        if(SpriteUtil.isAlignedVertically(sprite)) {
            if(tileFurtherBelowId != TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_LEFT) {
                if(random.nextInt(100) > 95) {
                    currentMovement = FlameMovement.UP;
                } else {
                    if(random.nextInt(100) > 50) {
                        currentMovement = FlameMovement.LEFT;
                    } else {
                        currentMovement = FlameMovement.RIGHT;
                    }
                }
            }
        }
    }

    public void die() {
        if(!dying) {
            dying = true;
            this.sprite.startAction(killFlameAction, null);
            currentMovement = FlameMovement.LEFT;
        }
    }

    public AnimatedSprite getSprite() {
        return this.sprite;
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
        tileBehindId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BELOW);
        tileBelowLeftId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BELOW_LEFT);
        tileBelowRightId = tileMap.getTileMapTile(this.sprite, TileMap.CELL_TYPE.BELOW_RIGHT);
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
}
