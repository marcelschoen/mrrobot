package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jplay.gdx.Assets;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.MoveableEntity;

import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;
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

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

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
        DebugOutput.log("y: " + getY(), 40, 75);
        DebugOutput.log("behind: " + tileBehindId, 40, 60);
        DebugOutput.log("below: " + tileBelowId, 40, 46);
        DebugOutput.log("below 2: " + tileFurtherBelowId, 40, 32);
    }

    public void setPosition(float x, float y) {
        this.sprite.setPosition(x, y);
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
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
                boolean tryMoveLeft = false;
                if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                    if(mrRobotIsOnLadder()) {
                        if(tileBelowId != -1 && tileBelowId != TILE_LADDER_LEFT ) {
                            tryMoveLeft = true;
                        } else if(tileBehindId != -1 && tileBehindId != TILE_LADDER_LEFT
                                && mrRobotIsNearlyAlignedVerticallyAtTop()) {
                            tryMoveLeft = true;
                        }
                    } else {
                        tryMoveLeft = true;
                    }
                }
                if(tryMoveLeft) {
                    alignMrRobotVertically();
                    tryMovingSideways(MRROBOT_STATE.WALKING_LEFT, ANIM.mrrobot_walk_left.name());
                } else if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                    stopMrRobot();
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                boolean tryMoveRight = false;
                if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                    if(mrRobotIsOnLadder()) {
                        if(tileBelowId != -1 && tileBelowId != TILE_LADDER_LEFT ) {
                            tryMoveRight = true;
                        } else if(tileBehindId != -1 && tileBehindId != TILE_LADDER_LEFT
                                    && mrRobotIsNearlyAlignedVerticallyAtTop()) {
                            tryMoveRight = true;
                        }
                    } else {
                        tryMoveRight = true;
                    }
                }
                if(tryMoveRight) {
                    alignMrRobotVertically();
                    tryMovingSideways(MRROBOT_STATE.WALKING_RIGHT, ANIM.mrrobot_walk_right.name());
                } else if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                    stopMrRobot();
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(!mrRobotIsClimbing()) {
                tryClimbingUp();
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(!mrRobotIsClimbing()) {
                tryClimbingDown();
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
     * @return
     */
    public void stopMrRobot() {
        if(mrRobotIsOnLadder()) {
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
     * @param intendedState
     * @param animationName
     */
    private void tryMovingSideways(MRROBOT_STATE intendedState, String animationName) {
        if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
            intendedMovement.animationName = animationName;
            intendedMovement.mrrobot_state = intendedState;
        }
    }

    /**
     *
     */
    private void tryClimbingUp() {
        boolean climb = false;
        boolean alignLeftTile = false;
        if(tileBehindId == TILE_LADDER_LEFT || tileBehindId == TILE_LADDER_RIGHT) {
            alignLeftTile = tileBehindId == TILE_LADDER_RIGHT;
            climb = true;
        } else if(tileFurtherBelowId == TILE_LADDER_LEFT || tileFurtherBelowId == TILE_LADDER_RIGHT) {
            if(tileBehindId != -1) {
                alignLeftTile = tileFurtherBelowId == TILE_LADDER_RIGHT;
                climb = true;
            }
        }
        if(climb && mrRobotIsNearlyAlignedHorizontally()) {
            alignMrRobotHorizontally(alignLeftTile);
            intendedMovement.animationName = ANIM.mrrobot_climb.name();
            intendedMovement.mrrobot_state = MRROBOT_STATE.CLIMBING_UP;
        }
    }

    /**
     *
     */
    private void tryClimbingDown() {
        boolean climb = false;
        boolean alignLeftTile = false;
        if(mrRobotIsOnLadder()) {
            alignLeftTile = tileBelowId == TILE_LADDER_RIGHT;
            climb = true;
        }
        if(tileFurtherBelowId == TILE_LADDER_LEFT || tileFurtherBelowId == TILE_LADDER_RIGHT) {
            alignLeftTile = tileFurtherBelowId == TILE_LADDER_RIGHT;
            climb = true;
        }
        if(climb && mrRobotIsNearlyAlignedHorizontally()) {
            alignMrRobotHorizontally(alignLeftTile);
            intendedMovement.animationName = ANIM.mrrobot_climb.name();
            intendedMovement.mrrobot_state = MRROBOT_STATE.CLIMBING_DOWN;
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

        setPosition(x, y);
    }

    /**
     *
     */
    public void mrRobotLands() {
        alignMrRobotVertically();
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

        if(tileBelowId == NO_TILE) {
            if (!mrRobotIsFalling()) {
                mrRobotStartsFalling();
            }
        } else if(mrRobotIsFalling() || mrRobotIsSliding()) {
            // TODO - CHECK ONLY FOR SOLID BLOCKS TO STAND ON, NOT DOWNWARD PIPES OR LADDERS
            if (tileBelowId != NO_TILE && tileBelowId != TILE_SLIDER) {
                if (mrRobotIsNearlyAlignedVertically()) {
                    mrRobotLands();
                    setPosition(sprite.getX(), line * 8f);
                }
            }
        } else if(mrRobotIsClimbing()) {
            if(mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
                if(tileBehindId == NO_TILE && tileBelowId != TILE_LADDER_LEFT) {
                    setState(MRROBOT_STATE.STANDING_RIGHT);
                    mrRobotLands();
                }
            } else {
                if(tileBehindId == TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_LEFT) {
                    if(mrRobotIsNearlyAlignedVertically()) {
                        setState(MRROBOT_STATE.STANDING_RIGHT);
                        mrRobotLands();
                    }
                }
            }
        } else {
            if(tileBelowId == TILE_DOT) {
                tileMap.clearCell(tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW));
                Hud.addScore(1);
            }
            if(tileBelowId == TILE_ROLL_LEFT_1 || tileBelowId == TILE_ROLL_LEFT_2) {
                setPosition(sprite.getX() - 20f * delta, sprite.getY());
            } else if(tileBelowId == TILE_ROLL_RIGHT_1 || tileBelowId == TILE_ROLL_RIGHT_2) {
                setPosition(sprite.getX() + 20f * delta, sprite.getY());
            }
        }

        if(line > 0) {
            if(tileFurtherBelowId == TILE_SLIDER && !mrRobotIsSliding()) {
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

    public boolean mrRobotIsNearlyAlignedVerticallyAtTop() {
        float y = sprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    public float alignMrRobotVertically() {
        float y = sprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        setPosition(sprite.getX(), row * 8f);
        return sprite.getY();
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = sprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        setPosition(col * 8f + 11, sprite.getY());
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
