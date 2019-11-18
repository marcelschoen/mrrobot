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

    private enum MRROBOT_STATE {
        CLIMBING_UP(true),
        CLIMBING_DOWN(true),
        SLIDING_RIGHT(true),
        SLIDING_LEFT(false),
        FALLING_RIGHT(true),
        FALLING_LEFT(false),
        STANDING_ON_LADDER(true),
        STANDING_RIGHT(true),
        STANDING_LEFT(false),
        WALKING_RIGHT(true),
        WALKING_LEFT(false);

        private boolean isFacingRight = true;

        MRROBOT_STATE(boolean isFacingRight) {
            this.isFacingRight = isFacingRight;
        }

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
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
    }

    public void setState(String animationName) {
        this.sprite.setState(animationName);
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
                if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                    tryMovingSideways(MRROBOT_STATE.WALKING_LEFT, ANIM.mrrobot_walk_left.name());
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                    tryMovingSideways(MRROBOT_STATE.WALKING_RIGHT, ANIM.mrrobot_walk_right.name());
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            tryClimbing(MRROBOT_STATE.CLIMBING_UP, ANIM.mrrobot_climb.name());
        } else {
            // Stop movement
            intendedMovement.mrrobot_state = stopMrRobot();
        }

        if(intendedMovement.mrrobot_state != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                mrRobotState = intendedMovement.mrrobot_state;
                if(intendedMovement.animationName != null) {
                    sprite.setState(intendedMovement.animationName);
                }
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
        intendedMovement.animationName = animationName;
        intendedMovement.mrrobot_state = mrRobotClimbing(intendedState);
    }

    public MRROBOT_STATE mrRobotClimbing(MRROBOT_STATE climbingType) {
        TiledMapTileLayer.Cell cell = tileMap.getTileMapCell(TileMap.CELL_TYPE.BEHIND);
        TiledMapTileLayer.Cell cellBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.FURTHER_BELOW);
        DebugOutput.log("behind: " + (cell == null ? "-" : cell.getTile().getId()), 80, 160);
        int tileId = cell != null && cell.getTile() != null ? cell.getTile().getId() : -1;
        int tileBelowId = cellBelow != null && cellBelow.getTile() != null ? cellBelow.getTile().getId() : -1;
//        DebugOutput.log("behind: " + tileId, 0, 40);
        boolean climb = false;
        boolean alignLeftTile = false;
        if(cell != null && (tileId == TILE_LADDER_LEFT || tileId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileId == TILE_LADDER_RIGHT;
            climb = true;
        } else if(cellBelow != null && (tileBelowId == TILE_LADDER_LEFT || tileBelowId == TILE_LADDER_RIGHT)) {
            alignLeftTile = tileBelowId == TILE_LADDER_RIGHT;
            climb = true;
        }
        if(climb && mrRobotIsNearlyAligned()) {
            alignMrRobotHorizontally(alignLeftTile);
            sprite.setState(ANIM.mrrobot_climb.name());
            mrRobotState = climbingType;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public MRROBOT_STATE stopMrRobot() {
        if(mrRobotIsClimbing()) {
            sprite.setState(ANIM.mrrobot_stand_on_ladder.name());
            return MRROBOT_STATE.STANDING_ON_LADDER;
        } else {
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    sprite.setState(ANIM.mrrobot_stand_right.name());
                    return MRROBOT_STATE.STANDING_RIGHT;
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    sprite.setState(ANIM.mrrobot_stand_left.name());
                    return MRROBOT_STATE.STANDING_LEFT;
                }
            }
        }
        return null;
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
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.STANDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.STANDING_LEFT;
        }
    }

    /**
     *
     */
    public void mrRobotStartsFalling() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.FALLING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.FALLING_LEFT;
        }
    }

    /**
     *
     */
    public void mrRobotStartsSliding() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.SLIDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.SLIDING_LEFT;
        }
    }

    /**
     * @return true if Mr. Robot is climbing
     */
    public boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP
                || mrRobotState == MRROBOT_STATE.STANDING_ON_LADDER;
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

        TiledMapTileLayer.Cell cell = tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW);

        if(tileMap.cellEmpty(cell)) {
            if(!mrRobotIsFalling()) {
                mrRobotStartsFalling();
            }
        } else {
            // TODO - CHECK ONLY FOR SOLID BLOCKS TO STAND ON, NOT DOWNWARD PIPES OR LADDERS
            int tileId = cell.getTile().getId();
            if(tileId != 0 && tileId != TILE_SLIDER) {
                if(mrRobotIsFalling() || mrRobotIsSliding()) {
                    mrRobotLands();
                    sprite.setPosition(sprite.getX(), line * 8f);
                }
            }
            if(cell.getTile().getId() == TILE_DOT) {
                tileMap.clearCell(cell);
                Hud.addScore(1);
            }
            if(cell.getTile().getId() == TILE_ROLL_LEFT_1 || cell.getTile().getId() == TILE_ROLL_LEFT_2) {
                sprite.setPosition(sprite.getX() - 20f * delta, sprite.getY());
            } else if(cell.getTile().getId() == TILE_ROLL_RIGHT_1 || cell.getTile().getId() == TILE_ROLL_RIGHT_2) {
                sprite.setPosition(sprite.getX() + 20f * delta, sprite.getY());
            }
        }

        if(line > 0) {
            cell = tileMap.getCell((int)col, (int)line - 1);
            if(cell != null && cell.getTile().getId() == TILE_SLIDER && !mrRobotIsSliding()) {
                mrRobotStartsSliding();
            }
        }
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

    public boolean mrRobotIsNearlyAligned() {
        float x = sprite.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        /////////DebugOutput.log("x: " + x + ", col: " + col + ", diff: " + diff, 0, 100);
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
