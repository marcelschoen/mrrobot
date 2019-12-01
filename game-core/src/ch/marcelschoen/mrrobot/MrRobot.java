package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.Sprites;
import com.jplay.gdx.sprites.action.Action;
import com.jplay.gdx.sprites.action.ActionBuilder;
import com.jplay.gdx.sprites.action.ActionListener;

import java.util.ArrayList;
import java.util.List;

import static ch.marcelschoen.mrrobot.Tiles.NO_TILE;
import static ch.marcelschoen.mrrobot.Tiles.TILE_DOT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ELEVATOR;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

public class MrRobot implements ActionListener {

    private IntendedMovement intendedMovement = new IntendedMovement();

    public enum ANIM {
        mrrobot_stand_right,
        mrrobot_walk_right,
        mrrobot_stand_left,
        mrrobot_walk_left,
        mrrobot_climb,
        mrrobot_jump_right,
        mrrobot_jump_left,
        mrrobot_fall,
        mrrobot_stand_on_ladder,
        mrrobot_downfall,
        mrrobot_shield
    };

    private static final float WALK_SPEED = 32;
    private static final float DOWN_SPEED = 32;
    private static final float RISING_SPEED = 10;
    private static final float ROLLING_SPEED = 26;

    private AnimatedSprite mrrobotSprite = null;
    private AnimatedSprite shieldSprite = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private TileMap tileMap;

    public enum MRROBOT_STATE {
        JUMP_RIGHT(true, ANIM.mrrobot_jump_right.name()),
        JUMP_LEFT(false, ANIM.mrrobot_jump_left.name()),
        JUMPUP_RIGHT(true, ANIM.mrrobot_jump_right.name()),
        JUMPUP_LEFT(false, ANIM.mrrobot_jump_left.name()),
        CLIMBING_UP(true, ANIM.mrrobot_climb.name()),
        CLIMBING_DOWN(true, ANIM.mrrobot_climb.name()),
        SLIDING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        SLIDING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        FALL_RIGHT(true, ANIM.mrrobot_jump_right.name()),
        FALL_LEFT(false, ANIM.mrrobot_jump_left.name()),
        DROP_RIGHT(true, ANIM.mrrobot_fall.name()),
        DROP_LEFT(false, ANIM.mrrobot_fall.name()),
        STANDING_ON_LADDER(true, ANIM.mrrobot_stand_on_ladder.name()),
        RISING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        RISING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        STANDING_RIGHT(true, ANIM.mrrobot_stand_right.name()),
        STANDING_LEFT(false, ANIM.mrrobot_stand_left.name()),
        WALKING_RIGHT(true, ANIM.mrrobot_walk_right.name()),
        WALKING_LEFT(false, ANIM.mrrobot_walk_left.name());

        private boolean isFacingRight = true;
        private String animationName = null;
        private MRROBOT_STATE reverse;

        MRROBOT_STATE(boolean isFacingRight, String animationName) {
            this.isFacingRight = isFacingRight;
            this.animationName = animationName;
        }

        public MRROBOT_STATE getReverse() {
            if(reverse == null) {
                throw new IllegalStateException("No reverse state defined for: " + name());
            }
            return reverse;
        }

        @Override
        public String toString() {
            return "[state:" + name() + "/anim:" + getAnimationName() + "]";
        }

        public void setReverse(MRROBOT_STATE state) {
            reverse = state;
        }

        public String getAnimationName() { return this.animationName; }

        public boolean isFacingRight() {
            return this.isFacingRight;
        }
    }
    private MRROBOT_STATE mrRobotState = MRROBOT_STATE.STANDING_RIGHT;

    /** Pre-defined actions. */
    private Action jumpRightAction = null;
    private Action jumpLeftAction = null;
    private Action jumpUpAction = null;

    /**
     */
    public MrRobot() {
        for(MRROBOT_STATE state : MRROBOT_STATE.values()) {
            for(MRROBOT_STATE state2 : MRROBOT_STATE.values()) {
                if(state != state2 && state.name().contains("_") && state2.name().contains("_")) {
                    String part1a = state.name().substring(0, state.name().indexOf("_"));
                    String part1b = state2.name().substring(0, state2.name().indexOf("_"));
                    if(part1a.equals(part1b)) {
                        String part2a = state.name().substring(state.name().indexOf("_") + 1);
                        String part2b = state2.name().substring(state2.name().indexOf("_") + 1);
                        if( (part2a.equals("LEFT") && part2b.equals("RIGHT"))
                            || (part2a.equals("RIGHT") && part2b.equals("LEFT"))) {
                            state.setReverse(state2);
                            state2.setReverse(state);
                        }
                    }
                }
            }
        }

        List<String> animationNames = new ArrayList<>();
        for(ANIM animation : ANIM.values()) {
            animationNames.add(animation.name());
        }
        this.mrrobotSprite = Sprites.createSprite(animationNames);
        this.mrrobotSprite.setVisible(true);

        this.shieldSprite = Sprites.createSprite(ANIM.mrrobot_shield.name());
        this.shieldSprite.setVisible(false);
        this.shieldSprite.attachToSprite(this.mrrobotSprite, 0, 0);

        // Initialize pre-defined actions for Mr. Robot
        jumpRightAction = new ActionBuilder()
                .moveTo(22, 24, 0.6f, Interpolation.linear)
                .build();
        jumpLeftAction = new ActionBuilder()
                .moveTo(-22, 24, 0.6f, Interpolation.linear)
                .build();
        jumpUpAction = new ActionBuilder()
                .moveTo(0, 18, 0.6f, Interpolation.circleOut)
                .build();
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public void draw(SpriteBatch batch, float delta) {
        this.mrrobotSprite.draw(batch, delta);
        /*
        DebugOutput.log("y: " + getY(), 40, 75);
        DebugOutput.log("behind: " + tileBehindId, 40, 60);
        DebugOutput.log("below: " + tileBelowId, 40, 46);
        DebugOutput.log("below 2: " + tileFurtherBelowId, 40, 32);
         */
    }

    public AnimatedSprite getMrrobotSprite() {
        return this.mrrobotSprite;
    }

    public void setPosition(float x, float y) {
        this.mrrobotSprite.setPosition(x, y);
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    public void setState(MRROBOT_STATE state) {
        this.mrRobotState = state;
        this.mrrobotSprite.showAnimation(state.getAnimationName());
    }

    public float getX() {
        return this.mrrobotSprite.getX();
    }
    public float getY() {
        return this.mrrobotSprite.getY();
    }

    public void handleInput(float delta) {
        intendedMovement.clear();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)
                && !mrRobotIsJumping() && !mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
            if(mrRobotState != MRROBOT_STATE.WALKING_LEFT) {
                boolean tryMoveLeft = false;
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
                if(tryMoveLeft) {
                    alignMrRobotVertically();
                    tryMovingSideways(MRROBOT_STATE.WALKING_LEFT, ANIM.mrrobot_walk_left.name());
                } else if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
                    stopMrRobot();
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                && !mrRobotIsJumping() && !mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
            if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                boolean tryMoveRight = false;
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
                if(tryMoveRight) {
                    alignMrRobotVertically();
                    tryMovingSideways(MRROBOT_STATE.WALKING_RIGHT, ANIM.mrrobot_walk_right.name());
                } else if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
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
/*
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Vector2[] points = new Vector2[3];
            points[0] = new Vector2(getX(), getY() - 20);
            points[1] = new Vector2(getX()+30, getY() - 40);
            points[2] = new Vector2(getX()+50, getY() - 80);
            getMrrobotSprite().moveAlongSpline(points);
        }
*/
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (!mrRobotIsFalling() && !mrRobotIsSlidingDown() && !mrRobotIsClimbing() && !mrRobotIsJumping() && !mrRobotIsRisingUp()) {
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    tryJumping();
                } else {
                    tryJumpingUp();
                }
            }
        }

        if(intendedMovement.mrrobot_state != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
                setState(intendedMovement.mrrobot_state);
            }
        }

        moveMrRobot(delta);
        checkMrRobot(delta);
    }

    private void tryJumpingUp() {
        if(mrRobotState.isFacingRight()) {
            setState(MRROBOT_STATE.JUMPUP_RIGHT);
        } else {
            setState(MRROBOT_STATE.JUMPUP_LEFT);
        }
        getMrrobotSprite().startAction(jumpUpAction, this);
    }

    private void tryJumping() {
        if(mrRobotState.isFacingRight()) {
            setState(MRROBOT_STATE.JUMP_RIGHT);
            getMrrobotSprite().startAction(jumpRightAction, this);
        } else {
            setState(MRROBOT_STATE.JUMP_LEFT);
            getMrrobotSprite().startAction(jumpLeftAction, this);
        }
    }

    @Override
    public void completed(Action action) {
        Action firstAction = action.getFirstActionInChain();
        if(firstAction == jumpRightAction || firstAction == jumpLeftAction) {
            if(mrRobotState.isFacingRight()) {
                setState(MRROBOT_STATE.FALL_RIGHT);
            } else {
                setState(MRROBOT_STATE.FALL_LEFT);
            }
        } else if(firstAction == jumpUpAction) {
            if(mrRobotState.isFacingRight()) {
                setState(MRROBOT_STATE.DROP_RIGHT);
            } else {
                setState(MRROBOT_STATE.DROP_LEFT);
            }
        }
    }

    /**
     *
     * @return
     */
    private void stopMrRobot() {
        if(mrRobotIsOnLadder()) {
            setState(MRROBOT_STATE.STANDING_ON_LADDER);
        } else {
            if(mrRobotState.isFacingRight()) {
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
        if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
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
        float x = mrrobotSprite.getX();
        float y = mrrobotSprite.getY();
        if (mrRobotState == MRROBOT_STATE.WALKING_RIGHT) {
            x += WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.WALKING_LEFT) {
            x -= WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MRROBOT_STATE.FALL_RIGHT) {
            x += WALK_SPEED * delta;
            y -= DOWN_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.FALL_LEFT) {
            x -= WALK_SPEED * delta;
            y -= DOWN_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.DROP_RIGHT || mrRobotState == MRROBOT_STATE.DROP_LEFT
                || mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT) {
            y -= DOWN_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.RISING_RIGHT || mrRobotState == MRROBOT_STATE.RISING_LEFT) {
            y += RISING_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
            y += WALK_SPEED * delta;
        } else if (mrRobotState == MRROBOT_STATE.CLIMBING_DOWN) {
            y -= WALK_SPEED * delta;
        }

        if(x < -5) {
            x = -5;
            if(mrRobotIsFalling()) {
                setState(mrRobotState.getReverse());
            }
            // TODO - CONSTANT FOR MR.ROBOT SPRITE WIDTH
        } else if(x > MrRobotGame.VIRTUAL_WIDTH - 19) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
            setState(mrRobotState.getReverse());
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
            setState(MRROBOT_STATE.DROP_RIGHT);
        } else {
            setState(MRROBOT_STATE.DROP_LEFT);
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

    public void mrRobotStartsRisingUp() {
        if(mrRobotState.isFacingRight()) {
            setState(MRROBOT_STATE.RISING_RIGHT);
        } else {
            setState(MRROBOT_STATE.RISING_LEFT);
        }
    }

    /**
     * @return true if Mr. Robot is climbing
     */
    public boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP;
    }

    public boolean mrRobotIsJumping() {
        return mrRobotState == MRROBOT_STATE.JUMPUP_LEFT
                || mrRobotState == MRROBOT_STATE.JUMPUP_RIGHT
                || mrRobotState == MRROBOT_STATE.JUMP_LEFT
                || mrRobotState == MRROBOT_STATE.JUMP_RIGHT;
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
        return mrRobotState == MRROBOT_STATE.DROP_LEFT || mrRobotState == MRROBOT_STATE.DROP_RIGHT
                || mrRobotState == MRROBOT_STATE.FALL_LEFT || mrRobotState == MRROBOT_STATE.FALL_RIGHT;
    }

    /**
     * @return True if Mr. Robot is sliding down
     */
    public boolean mrRobotIsSlidingDown() {
        return mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is rising up on an elevator.
     */
    public boolean mrRobotIsRisingUp() {
        return mrRobotState == MRROBOT_STATE.RISING_LEFT || mrRobotState == MRROBOT_STATE.RISING_RIGHT;
    }

    /**
     * Checks Mr. Robot's status and may trigger stuff.
     *
     * @param delta Time delta.
     */
    public void checkMrRobot(float delta) {
        float x = mrrobotSprite.getX() + 12f;
        float y = mrrobotSprite.getY()+ 7f;

        float col = x / 8f;
        float line = (y / 8f) - 1f;

        if(tileBelowId == NO_TILE) {
            if (!mrRobotIsFalling() && !mrRobotIsJumping()) {
                mrRobotStartsFalling();
            }
        } else if(mrRobotIsFalling() || mrRobotIsSlidingDown()) {
            // TODO - CHECK ONLY FOR SOLID BLOCKS TO STAND ON, NOT DOWNWARD PIPES OR LADDERS
            if (tileBelowId != NO_TILE && tileBelowId != TILE_SLIDER
                    && tileBelowId != TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_RIGHT) {
                if (mrRobotIsNearlyAlignedVertically()) {
                    mrRobotLands();
                    setPosition(mrrobotSprite.getX(), line * 8f);
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
                setPosition(mrrobotSprite.getX() - ROLLING_SPEED * delta, mrrobotSprite.getY());
            } else if(tileBelowId == TILE_ROLL_RIGHT_1 || tileBelowId == TILE_ROLL_RIGHT_2) {
                setPosition(mrrobotSprite.getX() + ROLLING_SPEED * delta, mrrobotSprite.getY());
            } else if(tileBelowId == TILE_ELEVATOR && tileBehindId == TILE_ELEVATOR && !mrRobotIsRisingUp()) {
                mrRobotStartsRisingUp();
            }
        }

        if(line > 0) {
            if(tileFurtherBelowId == TILE_SLIDER && !mrRobotIsSlidingDown()
                    && !mrRobotIsJumping() && !mrRobotIsFalling()) {
                mrRobotStartsSliding();
            }
        }
    }

    public boolean mrRobotIsNearlyAlignedVertically() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }

    public boolean mrRobotIsNearlyAlignedVerticallyAtTop() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    public float alignMrRobotVertically() {
        float y = mrrobotSprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        setPosition(mrrobotSprite.getX(), row * 8f);
        return mrrobotSprite.getY();
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrrobotSprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        setPosition(col * 8f + 11, mrrobotSprite.getY());
    }

    public boolean mrRobotIsNearlyAlignedHorizontally() {
        float x = mrrobotSprite.getX() + 6f;
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
