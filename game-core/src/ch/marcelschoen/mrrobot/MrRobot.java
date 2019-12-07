package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.collision.Collision;
import com.jplay.gdx.collision.CollisionListener;
import com.jplay.gdx.sprites.AnimatedSprite;
import com.jplay.gdx.sprites.Sprites;
import com.jplay.gdx.sprites.action.Action;
import com.jplay.gdx.sprites.action.ActionBuilder;
import com.jplay.gdx.sprites.action.ActionListener;

import java.util.ArrayList;
import java.util.List;

import ch.marcelschoen.mrrobot.actions.TeleportAction;
import ch.marcelschoen.mrrobot.actions.TeleportCompletedAction;

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

/**
 * Represents and handles the state and attributes of Mr. Robot.
 *
 * @author Marcel Schoen
 */
public class MrRobot implements ActionListener, CollisionListener {

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
        mrrobot_teleport,
        mrrobot_shield,
        shield_item
    };

    private static final float WALK_SPEED = 40;
    private static final float DOWN_SPEED = 32;
    private static final float RISING_SPEED = 10;
    private static final float ROLLING_SPEED = 26;

    private AnimatedSprite mrrobotSprite = null;
    private AnimatedSprite shieldSprite = null;

    private TiledMapTileLayer.Cell cellBelow = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private TileMap tileMap;

    /**
     * All possible states of Mr. Robot
     */
    public enum MRROBOT_STATE {
        JUMP_RIGHT(true, ANIM.mrrobot_jump_right.name(), true),
        JUMP_LEFT(false, ANIM.mrrobot_jump_left.name(), true),
        JUMPUP_RIGHT(true, ANIM.mrrobot_jump_right.name(), true),
        JUMPUP_LEFT(false, ANIM.mrrobot_jump_left.name(), true),
        CLIMBING_UP(true, ANIM.mrrobot_climb.name(), false),
        CLIMBING_DOWN(true, ANIM.mrrobot_climb.name(), false),
        SLIDING_RIGHT(true, ANIM.mrrobot_stand_right.name(), true),
        SLIDING_LEFT(false, ANIM.mrrobot_stand_left.name(), true),
        FALL_RIGHT(true, ANIM.mrrobot_jump_right.name(), true),
        FALL_LEFT(false, ANIM.mrrobot_jump_left.name(), true),
        DROP_RIGHT(true, ANIM.mrrobot_fall.name(), true),
        DROP_LEFT(false, ANIM.mrrobot_fall.name(), true),
        STANDING_ON_LADDER(true, ANIM.mrrobot_stand_on_ladder.name(), false),
        RISING_RIGHT(true, ANIM.mrrobot_stand_right.name(), true),
        RISING_LEFT(false, ANIM.mrrobot_stand_left.name(), true),
        STANDING_RIGHT(true, ANIM.mrrobot_stand_right.name(), false),
        STANDING_LEFT(false, ANIM.mrrobot_stand_left.name(), false),
        TELEPORTING(false, ANIM.mrrobot_teleport.name(), true),
        WALKING_RIGHT(true, ANIM.mrrobot_walk_right.name(), false),
        WALKING_LEFT(false, ANIM.mrrobot_walk_left.name(), false);

        private boolean isFacingRight = true;
        private String animationName = null;
        private MRROBOT_STATE reverse;
        /** During some states, all input is completely ignored. */
        private boolean isInputBlocked = false;

        MRROBOT_STATE(boolean isFacingRight, String animationName, boolean isInputBlocked) {
            this.isFacingRight = isFacingRight;
            this.animationName = animationName;
            this.isInputBlocked = isInputBlocked;
        }

        /**
         * @return True if input must be ignored during this state.
         */
        public boolean isInputBlocked() {
            return this.isInputBlocked;
        }

        public MRROBOT_STATE changeFrom(MRROBOT_STATE oldState, MRROBOT_STATE targetState) {
            MRROBOT_STATE result = targetState;
            if(!(oldState.isFacingRight() && result.isFacingRight())) {
                result = result.getReverse();
            }
            return result;
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
    private Action teleportAction = null;

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
        this.mrrobotSprite = Sprites.createSprite(animationNames, SpriteTypes.MR_ROBOT);
        this.mrrobotSprite.setDefaultCollisionBounds(6, 0, 12, 24);
        this.mrrobotSprite.setVisible(true);

        this.shieldSprite = Sprites.createSprite(ANIM.mrrobot_shield.name(), SpriteTypes.SHIELDS);
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
        teleportAction = new ActionBuilder()
                .setAnimation(ANIM.mrrobot_teleport.name())
                .custom(new TeleportAction(this))
                .custom(new TeleportCompletedAction(this))
                .build();

        Collision.addListener(this);
        Collision.initialize();
    }

    /**
     * @param tileMap Sets the current tilemap instance.
     */
    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    @Override
    public void spritesCollided(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle) {
        if(spriteOne.getType() == SpriteTypes.MR_ROBOT || spriteTwo.getType() == SpriteTypes.MR_ROBOT) {
            if(spriteOne.getType() == SpriteTypes.SHIELDS || spriteTwo.getType() == SpriteTypes.SHIELDS) {
                shieldSprite.setVisible(true);
                if(spriteOne.getType() == SpriteTypes.SHIELDS) {
                    spriteOne.setVisible(false);
                } else if(spriteTwo.getType() == SpriteTypes.SHIELDS) {
                    spriteTwo.setVisible(false);
                }
            }
            if(spriteOne.getType() == SpriteTypes.FLAMES || spriteTwo.getType() == SpriteTypes.FLAMES) {
                DebugOutput.flicker(Color.RED);
            }
        }
    }

    /**
     * @return The Mr. Robot sprite instance.
     */
    public AnimatedSprite getMrrobotSprite() {
        return this.mrrobotSprite;
    }

    /**
     * Sets the position of Mr. Robot on screen.
     *
     * @param x The x-coordinate in pixels.
     * @param y The y-coordinate in pixels.
     */
    public void setPosition(float x, float y) {
        this.mrrobotSprite.setPosition(x, y);
        cellBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW);
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);
        tileBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BELOW);
        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    private void changeState(MRROBOT_STATE state) {
        this.mrRobotState = state.changeFrom(this.mrRobotState, state);
        this.mrrobotSprite.showAnimation(mrRobotState.getAnimationName());
    }

    /**
     * @param state Sets
     */
    public void setState(MRROBOT_STATE state) {
        this.mrRobotState = state;
        this.mrrobotSprite.showAnimation(state.getAnimationName());
    }

    /**
     * @return The x-coordinate of the Mr. Robot sprite in pixels.
     */
    public float getX() {
        return this.mrrobotSprite.getX();
    }

    /**
     * @return The y-coordinate of the Mr. Robot sprite in pixels.
     */
    public float getY() {
        return this.mrrobotSprite.getY();
    }

    /**
     * Handles input for Mr. Robot movements.
     *
     * @param delta time since last screen refresh in seconds.
     */
    public void handleInput(float delta) {
/*
        DebugOutput.log("y: " + getY(), 40, 75);
        DebugOutput.log("behind: " + tileBehindId, 40, 60);
        DebugOutput.log("below: " + tileBelowId, 40, 46);
        DebugOutput.log("below 2: " + tileFurtherBelowId, 40, 32);
*/

        if(mrRobotState.isInputBlocked()) {
            return;
        }

        intendedMovement.clear();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
////                && !mrRobotIsJumping() && !mrRobotIsFalling() && !mrRobotIsSlidingDown() && !mrRobotIsRisingUp()) {
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
/////                } else if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
                } else {
                    stopMrRobot();
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
////                && !mrRobotIsJumping() && !mrRobotIsFalling() && !mrRobotIsSlidingDown() && !mrRobotIsRisingUp()) {
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
////                } else if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
                } else {
                    stopMrRobot();
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(!mrRobotIsClimbing()) {
                tryClimbingUp();
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(tileBelowId == Tiles.TILE_TELEPORTER) {
                mrrobotSprite.startAction(teleportAction, null);
            } else if(!mrRobotIsClimbing()) {
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
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    jumpSideways();
                } else {
                    jumpUp();
                }
        }

        if(intendedMovement.mrrobot_state != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSlidingDown()) {
                setState(intendedMovement.mrrobot_state);
            }
        }

    }

    private void jumpUp() {
        changeState(MRROBOT_STATE.JUMPUP_RIGHT);
        getMrrobotSprite().startAction(jumpUpAction, this);
    }

    private void jumpSideways() {
        changeState(MRROBOT_STATE.JUMP_RIGHT);
        if(mrRobotState.isFacingRight()) {
            getMrrobotSprite().startAction(jumpRightAction, this);
        } else {
            getMrrobotSprite().startAction(jumpLeftAction, this);
        }
    }

    @Override
    public void completed(Action completedAction) {
        Action firstAction = completedAction.getFirstActionInChain();
        if(firstAction == jumpRightAction || firstAction == jumpLeftAction) {
            changeState(MRROBOT_STATE.FALL_RIGHT);
        } else if(firstAction == jumpUpAction) {
            changeState(MRROBOT_STATE.DROP_RIGHT);
        }
    }

    /**
     *
     * @return
     */
    public void stopMrRobot() {
        if(mrRobotIsOnLadder()) {
            changeState(MRROBOT_STATE.STANDING_ON_LADDER);
        } else {
            changeState(MRROBOT_STATE.STANDING_RIGHT);
        }
    }

    /**
     *
     * @param intendedState
     * @param animationName
     */
    private void tryMovingSideways(MRROBOT_STATE intendedState, String animationName) {
        intendedMovement.animationName = animationName;
        intendedMovement.mrrobot_state = intendedState;
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
     */
    private void mrRobotLands() {
        alignMrRobotVertically();
        stopMrRobot();
    }

    /**
     * @return true if Mr. Robot is climbing
     */
    private boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP;
    }

    /**
     * @return True if Mr. Robot is jumping up or sideways
     */
    private boolean mrRobotIsJumping() {
        return mrRobotState == MRROBOT_STATE.JUMPUP_LEFT
                || mrRobotState == MRROBOT_STATE.JUMPUP_RIGHT
                || mrRobotState == MRROBOT_STATE.JUMP_LEFT
                || mrRobotState == MRROBOT_STATE.JUMP_RIGHT;
    }

    /**
     * @return True if Mr. Robot is on a ladder
     */
    private boolean mrRobotIsOnLadder() {
        return mrRobotState == MRROBOT_STATE.STANDING_ON_LADDER || mrRobotIsClimbing();
    }

    /**
     * @return True if Mr. Robot is walking
     */
    private boolean mrRobotIsWalking() {
        return mrRobotState == MRROBOT_STATE.WALKING_LEFT || mrRobotState == MRROBOT_STATE.WALKING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is falling
     */
    private boolean mrRobotIsFalling() {
        return mrRobotState == MRROBOT_STATE.DROP_LEFT || mrRobotState == MRROBOT_STATE.DROP_RIGHT
                || mrRobotState == MRROBOT_STATE.FALL_LEFT || mrRobotState == MRROBOT_STATE.FALL_RIGHT;
    }

    /**
     * @return True if Mr. Robot is sliding down
     */
    private boolean mrRobotIsSlidingDown() {
        return mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is rising up on an elevator.
     */
    private boolean mrRobotIsRisingUp() {
        return mrRobotState == MRROBOT_STATE.RISING_LEFT || mrRobotState == MRROBOT_STATE.RISING_RIGHT;
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
        } else if(x > MrRobotGame.VIRTUAL_WIDTH - 19) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
            setState(mrRobotState.getReverse());
        }

        setPosition(x, y);
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
                changeState(MRROBOT_STATE.DROP_RIGHT);
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
        } else if(mrRobotIsRisingUp()) {
            if (tileBelowId != TILE_ELEVATOR) {
                if (mrRobotIsNearlyAlignedVertically()) {
                    mrRobotLands();
                    setPosition(mrrobotSprite.getX(), line * 8f);
                }
            }
        } else if(mrRobotIsClimbing()) {
            if(mrRobotState == MRROBOT_STATE.CLIMBING_UP) {
                if(tileBehindId == NO_TILE && tileBelowId != TILE_LADDER_LEFT) {
                    changeState(MRROBOT_STATE.STANDING_RIGHT);
                    mrRobotLands();
                }
            } else {
                if(tileBehindId == TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_LEFT) {
                    if(mrRobotIsNearlyAlignedVertically()) {
                        changeState(MRROBOT_STATE.STANDING_RIGHT);
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
                changeState(MRROBOT_STATE.RISING_RIGHT);
            }
        }

        if(line > 0) {
            if(tileFurtherBelowId == TILE_SLIDER && !mrRobotIsSlidingDown()
                    && !mrRobotIsJumping() && !mrRobotIsFalling()) {
                changeState(MRROBOT_STATE.SLIDING_RIGHT);
            }
        }
    }

    /**
     * @return The TileMapTileLayer.Cell right below Mr. Robots feet.
     */
    public TiledMapTileLayer.Cell getCellBelow() {
        return cellBelow;
    }

    private void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrrobotSprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        setPosition(col * 8f + 11, mrrobotSprite.getY());
    }

    private float alignMrRobotVertically() {
        float y = mrrobotSprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        setPosition(mrrobotSprite.getX(), row * 8f);
        return mrrobotSprite.getY();
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his feet (lower sprite boundary).
     */
    private boolean mrRobotIsNearlyAlignedVertically() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his head (upper sprite boundary).
     */
    private boolean mrRobotIsNearlyAlignedVerticallyAtTop() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly aligned horizontally (center of sprite).
     */
    private boolean mrRobotIsNearlyAlignedHorizontally() {
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
