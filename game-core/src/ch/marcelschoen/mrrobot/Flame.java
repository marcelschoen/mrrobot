package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.Assets;

import java.util.ArrayList;
import java.util.List;

import ch.marcelschoen.aseprite.Animated2DSprite;

import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;

public class Flame {

    private IntendedMovement intendedMovement = new IntendedMovement();

    public enum ANIM {
        flame_right,
        flame_left,
    };

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 40;
    private static final float ROLLING_SPEED = 26;

    private Animated2DSprite sprite = new Animated2DSprite();

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private Camera camera;
    private TileMap tileMap;

    public static List<Flame> flames = new ArrayList<>();

    public enum FLAME_STATE {
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

    public Flame(Camera camera) {
        this.camera = camera;

        for(ANIM animation : ANIM.values()) {
            this.sprite.addAnimation(animation.name(), Assets.instance().getAnimation(animation.name()));
        }
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public void draw(SpriteBatch batch, float delta) {
        this.sprite.draw(batch, delta);
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    public void setState(FLAME_STATE state) {
        this.flameState = state;
        this.sprite.setAnimation(state.getAnimationName());
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
