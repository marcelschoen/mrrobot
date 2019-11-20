package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jplay.gdx.Assets;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.MoveableEntity;

import static ch.marcelschoen.mrrobot.Tiles.TILE_DOT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

public class MrRobot {

    private IntendedMovement intendedMovement = new IntendedMovement();

    public enum ANIM {
        mrrobot_stand_right,
        mrrobot_walk_right,
        mrrobot_stand_left,
        mrrobot_walk_left,
        mrrobot_climb,
        mrrobot_stand_on_ladder
    };

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 24;

    private MoveableEntity sprite = new MoveableEntity("mrrobot", 0, 0);

    private Camera camera;
    private TileMap tileMap;

    public enum MRROBOT_STATE {
        CLIMBING_UP(true, ANIM.mrrobot_climb.name()),
        CLIMBING_DOWN(true, ANIM.mrrobot_climb.name()),
        SLIDING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        SLIDING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        FALLING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        FALLING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        STANDING_ON_LADDER(true, ANIM.mrrobot_stand_on_ladder.name()),
        STANDING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        STANDING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        WALKING_RIGHT(true, ANIM.mrrobot_walk_right.name()),
        WALKING_LEFT(false, ANIM.mrrobot_walk_left.name());

        private boolean isFacingRight = true;
        private String animationName = null;

        MRROBOT_STATE(boolean isFacingRight, String animationName) {
            this.isFacingRight = isFacingRight;
            this.animationName = animationName;
        }

        public String getAnimationName() { return this.animationName; }

        public boolean isFacingRight() {
            return this.isFacingRight;
        }
    }
    private MRROBOT_STATE mrRobotState = MRROBOT_STATE.STANDING_RIGHT;

    public MrRobot(Camera camera) {
        this.camera = camera;

        for(ANIM animation : ANIM.values()) {
            this.sprite.setSprite(animation.name(), Assets.instance().getAnimated2DSprite(animation.name()));
        }
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public void draw(SpriteBatch batch, float delta) {
        this.sprite.draw(batch, delta);

        TiledMapTileLayer.Cell cell = tileMap.getTileMapCell(TileMap.CELL_TYPE.BEHIND);
        TiledMapTileLayer.Cell cellBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW);
        TiledMapTileLayer.Cell cellFurtherBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.FURTHER_BELOW);
        DebugOutput.log("behind: " + (cell == null ? "x" : cell.getTile().getId()), 40, 60);
        DebugOutput.log("below: " + (cellBelow == null ? "x" : cellBelow.getTile().getId()), 40, 46);
        DebugOutput.log("below 2: " + (cellFurtherBelow == null ? "x" : cellFurtherBelow.getTile().getId()), 40, 32);
        DebugOutput.log("falling: " + mrRobotIsFalling(), 40, 16);
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
    }

    public void setState(MRROBOT_STATE state) {
        this.mrRobotState = state;
        this.sprite.setState(state.getAnimationName());
    }

    public float getX() {
        return this.sprite.getX();
    }
    public float getY() {
        return this.sprite.getY();
    }

    public void handleInput(float delta) {
        intendedMovement.clear();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(mrRobotState != MRROBOT_STATE.WALKING_LEFT) {
                if(!mrRobotIsOnLadder() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                    tryMovingSideways(MRROBOT_STATE.WALKING_LEFT, ANIM.mrrobot_walk_left.name());
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                if(!mrRobotIsOnLadder() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                    tryMovingSideways(MRROBOT_STATE.WALKING_RIGHT, ANIM.mrrobot_walk_right.name());
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(!mrRobotIsClimbing()) {
                tryClimbing(MRROBOT_STATE.CLIMBING_UP, ANIM.mrrobot_climb.name());
            }
        } else {
            if(mrRobotIsClimbing() || mrRobotIsWalking()) {
                // Stop movement
                stopMrRobot();
            }
        }

        if(intendedMovement.mrrobot_state != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                setState(intendedMovement.mrrobot_state);
            }
        }

        moveMrRobot(delta);
        checkMrRobot(delta);
    }

    /**
     *
     * @param intendedState
     * @param animationName
     */
    private void tryMovingSideways(MRROBOT_STATE intendedState, String animationName) {
        if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
            intendedMovement.animationName = animationName;
            intendedMovement.mrrobot_state = intendedState;
        }
    }

    /**
     *
     * @param intendedState
     * @param animationName
     */
    private void tryClimbing(MRROBOT_STATE intendedState, String animationName) {
        int tileId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        int tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        boolean climb = false;
        boolean alignLeftTile = false;
        if(tileId != -1 && (tileId == TILE_LADDER_LEFT || tileId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileId == TILE_LADDER_RIGHT;
            climb = true;
        } else if(tileFurtherBelowId != -1 && (tileFurtherBelowId == TILE_LADDER_LEFT || tileFurtherBelowId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileFurtherBelowId == TILE_LADDER_RIGHT;
            climb = true;
        }
        if(climb && mrRobotIsNearlyAlignedHorizontally()) {
            alignMrRobotHorizontally(alignLeftTile);
            intendedMovement.animationName = ANIM.mrrobot_climb.name();
            intendedMovement.mrrobot_state = intendedState;
        }
    }

    /**
     *
     * @return
     */
    public void stopMrRobot() {
        if(mrRobotIsClimbing()) {
            setState(MRROBOT_STATE.STANDING_ON_LADDER);
        } else {
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    setState(MRROBOT_STATE.STANDING_RIGHT);
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    setState(MRROBOT_STATE.STANDING_LEFT);
                }
            }
        }
    }

    /**
     *
     * @param delta
     */
    public void moveMrRobot(float delta) {
        float x = sprite.getX();
        float y = sprite.getY();
        if (mrRobotState == MRROBOT_STATE.WALKING_RIGHT) {
            x += WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.WALKING_LEFT) {
            x -= WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.FALLING_RIGHT || mrRobotState == MRROBOT_STATE.FALLING_LEFT
                || mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT) {
            y -= DOWN_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
            y += WALK_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_DOWN) {
            y -= WALK_SPEED * delta;
        }

        if(x < -5) {
            x = -5;
            if(mrRobotIsFalling()) {
                // TODO - REVERSE DIRECTION
            }
            // TODO - CONSTANT FOR MR.ROBOT SPRITE WIDTH
        } else if(x > MrRobotGame.VIRTUAL_WIDTH - 19) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
        }

        sprite.setPosition(x, y);
    }

    /**
     *
     */
    public void mrRobotLands() {
        stopMrRobot();
    }

    /**
     *
     */
    public void mrRobotStartsFalling() {
        if(mrRobotState.isFacingRight()) {
            setState(MRROBOT_STATE.FALLING_RIGHT);
        } else {
            setState(MRROBOT_STATE.FALLING_LEFT);
        }
    }

    /**
     *
     */
    public void mrRobotStartsSliding() {
        if(mrRobotState.isFacingRight()) {
            setState(MRROBOT_STATE.SLIDING_RIGHT);
        } else {
            setState(MRROBOT_STATE.SLIDING_LEFT);
        }
    }

    /**
     * @return true if Mr. Robot is climbing
     */
    public boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP;
    }

    public boolean mrRobotIsOnLadder() {
        return mrRobotState == MRROBOT_STATE.STANDING_ON_LADDER || mrRobotIsClimbing();
    }

    public boolean mrRobotIsWalking() {
        return mrRobotState == MRROBOT_STATE.WALKING_LEFT || mrRobotState == MRROBOT_STATE.WALKING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is falling
     */
    public boolean mrRobotIsFalling() {
        return mrRobotState == MRROBOT_STATE.FALLING_LEFT || mrRobotState == MRROBOT_STATE.FALLING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is sliding down
     */
    public boolean mrRobotIsSliding() {
        return mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT;
    }

    /**
     * Checks Mr. Robot's status and may trigger stuff.
     *
     * @param delta Time delta.
     */
    public void checkMrRobot(float delta) {
        float x = sprite.getX() + 12f;
        float y = sprite.getY()+ 7f;

        float col = x / 8f;
        float line = (y / 8f) - 1f;

        TiledMapTileLayer.Cell cellBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW);
        TiledMapTileLayer.Cell cellBehind = tileMap.getTileMapCell(TileMap.CELL_TYPE.BEHIND);

        if(tileMap.cellEmpty(cellBelow)) {
            if (!mrRobotIsFalling()) {
                mrRobotStartsFalling();
            }
        } else if(mrRobotIsFalling() || mrRobotIsSliding()) {
            // TODO - CHECK ONLY FOR SOLID BLOCKS TO STAND ON, NOT DOWNWARD PIPES OR LADDERS
            int tileId = cellBelow.getTile().getId();
            if (tileId != 0 && tileId != TILE_SLIDER) {
                if (mrRobotIsNearlyAlignedVertically()) {
                    mrRobotLands();
                    sprite.setPosition(sprite.getX(), line * 8f);
                }
            }
        } else if(mrRobotIsClimbing()) {
            if(mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
                if(tileMap.cellEmpty(cellBehind) || cellBehind.getTile().getId() != TILE_LADDER_LEFT) {
//                DebugOutput.flicker(Color.YELLOW);
                    mrRobotState = MRROBOT_STATE.STANDING_RIGHT;
                    mrRobotLands();
                }
            }
        } else {
            if(cellBelow.getTile().getId() == TILE_DOT) {
                tileMap.clearCell(cellBelow);
                Hud.addScore(1);
            }
            if(cellBelow.getTile().getId() == TILE_ROLL_LEFT_1 || cellBelow.getTile().getId() == TILE_ROLL_LEFT_2) {
                sprite.setPosition(sprite.getX() - 20f * delta, sprite.getY());
            } else if(cellBelow.getTile().getId() == TILE_ROLL_RIGHT_1 || cellBelow.getTile().getId() == TILE_ROLL_RIGHT_2) {
                sprite.setPosition(sprite.getX() + 20f * delta, sprite.getY());
            }
        }

        if(line > 0) {
            cellBelow = tileMap.getCell((int)col, (int)line - 1);
            if(cellBelow != null && cellBelow.getTile().getId() == TILE_SLIDER && !mrRobotIsSliding()) {
                mrRobotStartsSliding();
            }
        }
    }

    public boolean mrRobotIsNearlyAlignedVertically() {
        float y = sprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }

    public float alignMrRobotVertically() {
        float y = sprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        sprite.setPosition(sprite.getX(), row * 8f);
        return sprite.getY();
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = sprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        sprite.setPosition(col * 8f + 12, sprite.getY());
    }

    public boolean mrRobotIsNearlyAlignedHorizontally() {
        float x = sprite.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        return Math.abs(diff) < 8f;
    }

    class IntendedMovement {
        public String animationName;
        public MRROBOT_STATE mrrobot_state;

        public void clear() {
            animationName = null;
            mrrobot_state = null;
        }
    }
}
