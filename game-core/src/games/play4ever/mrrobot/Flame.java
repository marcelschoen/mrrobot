package games.play4ever.mrrobot;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;

import java.util.ArrayList;
import java.util.List;

import games.play4ever.libgdx.collision.Collision;
import games.play4ever.libgdx.sprites.AnimatedSprite;
import games.play4ever.libgdx.sprites.Sprites;
import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.libgdx.sprites.action.ActionBuilder;

import static games.play4ever.mrrobot.Tiles.NO_TILE;

public class Flame {

    private IntendedMovement intendedMovement = new IntendedMovement();

    private Action killFlameAction;

    public enum ANIM {
        flame_right,
        flame_left,
        flame_blue
    };

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 40;
    private static final float ROLLING_SPEED = 26;

    private AnimatedSprite sprite = new AnimatedSprite(SpriteTypes.FLAMES);

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private Camera camera;
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

    public Flame(Camera camera) {
        this.camera = camera;
        List<String> animationNames = new ArrayList<>();
        for(ANIM animation : ANIM.values()) {
            animationNames.add(animation.name());
        }
        this.sprite = Sprites.createSprite(animationNames, SpriteTypes.FLAMES);
        this.sprite.setVisible(true);
        this.sprite.setDefaultCollisionBounds(0, 0, 18, 24);
        Collision.addRectangles(this.sprite);

        killFlameAction = new ActionBuilder()
                .setAnimation(Flame.ANIM.flame_blue.name())
                .moveTo(4, 5, 0.2f, Interpolation.bounce)
                .moveTo(2, -200, 1.5f, Interpolation.slowFast)
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
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    public void setState(FLAME_STATE state) {
        this.flameState = state;
        this.sprite.showAnimation(state.getAnimationName());
    }

    public float getX() {
        return this.sprite.getX();
    }
    public float getY() {
        return this.sprite.getY();
    }

    class IntendedMovement {
        public String animationName;
        public FLAME_STATE flame_state;

        public void clear() {
            animationName = null;
            flame_state = null;
        }
    }
}
