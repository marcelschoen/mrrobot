package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

import games.play4ever.libgdx.collision.Collision;
import games.play4ever.libgdx.collision.CollisionListener;
import games.play4ever.libgdx.sprites.AnimatedSprite;
import games.play4ever.libgdx.sprites.Sprites;
import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.libgdx.sprites.action.ActionBuilder;
import games.play4ever.libgdx.sprites.action.ActionListener;
import games.play4ever.mrrobot.actions.DropDownAction;
import games.play4ever.mrrobot.actions.FallDownAction;
import games.play4ever.mrrobot.actions.JumpSidewaysAction;
import games.play4ever.mrrobot.actions.RiseUpAction;
import games.play4ever.mrrobot.actions.SlideDownAction;
import games.play4ever.mrrobot.actions.TeleportAction;
import games.play4ever.mrrobot.actions.TeleportCompletedAction;

import static games.play4ever.mrrobot.MrRobotState.STANDING_RIGHT;
import static games.play4ever.mrrobot.Tiles.NO_TILE;
import static games.play4ever.mrrobot.Tiles.TILE_BOMB;
import static games.play4ever.mrrobot.Tiles.TILE_DOT;
import static games.play4ever.mrrobot.Tiles.TILE_ELEVATOR;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_LEFT_1;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_LEFT_2;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_RIGHT_1;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_RIGHT_2;
import static games.play4ever.mrrobot.Tiles.TILE_SLIDER;

/**
 * Represents and handles the state and attributes of Mr. Robot.
 *
 * @author Marcel Schoen
 */
public class MrRobot implements ActionListener, CollisionListener {

    public static MrRobot instance = null;

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
    private static final float ROLLING_SPEED = 26;

    private AnimatedSprite mrrobotSprite = null;
    private AnimatedSprite shieldSprite = null;

    private TiledMapTileLayer.Cell cellBelow = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private int tileFurtherBelowId = NO_TILE;

    private TileMap tileMap;

    private MrRobotState mrRobotState = STANDING_RIGHT;

    /** Pre-defined actions. */
    private Action jumpSidewaysAction = null;
    private Action jumpUpAction = null;
    private Action teleportAction = null;
    private Action shieldAction = null;
    private Action slideDownAction = null;
    private Action dropDownAction = null;
    private Action riseUpAction = null;
    private Action killFlameAction = null;

    /**
     */
    public MrRobot() {
        if(instance != null) {
            throw new IllegalStateException("MrRobot must exist only once!");
        }
        instance = this;
        for(MrRobotState state : MrRobotState.values()) {
            for(MrRobotState state2 : MrRobotState.values()) {
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
        Collision.addRectangles(this.mrrobotSprite);

        this.shieldSprite = Sprites.createSprite(ANIM.mrrobot_shield.name(), SpriteTypes.SHIELDS);
        this.shieldSprite.setVisible(false);
        this.shieldSprite.attachToSprite(this.mrrobotSprite, 0, 0);

        // Initialize pre-defined actions for Mr. Robot

        // we keep a specific reference to the movement action within the sideays jump, to be
        // able to set the direction at the time of commencing the jump
        jumpSidewaysAction = new ActionBuilder()
                .custom(new JumpSidewaysAction(this))
                .custom(new FallDownAction(this))
                .build();
        jumpUpAction = new ActionBuilder()
                .moveTo(0, 18, 0.6f, Interpolation.circleOut)
                .custom(new DropDownAction(this))
                .build();
        teleportAction = new ActionBuilder()
                .setAnimation(ANIM.mrrobot_teleport.name())
                .custom(new TeleportAction(this))
                .custom(new TeleportCompletedAction(this))
                .build();
        slideDownAction = new ActionBuilder()
                .custom(new SlideDownAction(this))
                .build();
        dropDownAction = new ActionBuilder()
                .custom(new DropDownAction(this))
                .build();
        riseUpAction = new ActionBuilder()
                .custom(new RiseUpAction(this))
                .build();
        shieldAction = new ActionBuilder()
                .setVisibility(true, 3f)
                .setVisibility(false, 0.2f)
                .setVisibility(true, 0.8f)
                .setVisibility(false, 0.2f)
                .setVisibility(true, 0.8f)
                .setVisibility(false, 0.2f)
                .setVisibility(true, 0.8f)
                .setVisibility(false, 0.2f)
                .setVisibility(true, 0.4f)
                .setVisibility(false, 0.1f)
                .setVisibility(true, 0.3f)
                .setVisibility(false, 0.1f)
                .setVisibility(true, 0.3f)
                .setVisibility(false, 0.1f)
                .setVisibility(true, 0.3f)
                .setVisibility(false, 0.1f)
                .setVisibility(true, 0.3f)
                .setVisibility(false, 0.f)
                .build();

        killFlameAction = new ActionBuilder()

                .setVisibility(false, 0f)
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
        AnimatedSprite mrRobotSprite = spriteOne;
        AnimatedSprite otherSprite = spriteTwo;
        if(spriteOne.getType() != SpriteTypes.MR_ROBOT) {
            mrRobotSprite = spriteTwo;
            otherSprite = spriteOne;
        }
            if(otherSprite.getType() == SpriteTypes.SHIELDS) {
                shieldSprite.startAction(shieldAction, null);
                otherSprite.setVisible(false);
            }
            if(otherSprite.getType() == SpriteTypes.FLAMES) {
                if(shieldSprite.isVisible()) {
                    // Mr. Robot vanquishes flame with shield
                    otherSprite.startAction(killFlameAction, null);
                } else {
                    // Mr. Robot dies
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

    public void changeState(MrRobotState state) {
        this.mrRobotState = state.changeFrom(this.mrRobotState, state);
        this.mrrobotSprite.showAnimation(mrRobotState.getAnimationName());
    }

    /**
     * @param state Sets
     */
    public void setState(MrRobotState state) {
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

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || GamepadOverlay.isLeftPressed) {
            moveLeft();
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || GamepadOverlay.isRightPressed) {
            moveRight();
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP) || GamepadOverlay.isUpPressed) {
            if(!mrRobotIsClimbing()) {
                tryClimbingUp();
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || GamepadOverlay.isDownPressed) {
            if(tileBelowId == Tiles.TILE_TELEPORTER) {
                mrrobotSprite.startAction(teleportAction, null);
            } else if(!mrRobotIsClimbing()) {
                tryClimbingDown();
            }
        } else {
            if(mrRobotIsClimbing() || isWalking()) {
                // Stop movement
                stopMrRobot();
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || GamepadOverlay.isJumpPressed) {
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || GamepadOverlay.isRightPressed || GamepadOverlay.isLeftPressed) {
                    getMrrobotSprite().startAction(jumpSidewaysAction, null);
                } else {
                    changeState(MrRobotState.JUMPUP_RIGHT);
                    getMrrobotSprite().startAction(jumpUpAction, this);
                }
        }
        completeMovement();
    }

    public void moveRight() {
        if(mrRobotState != MrRobotState.WALKING_RIGHT) {
            boolean tryMoveRight = false;
            if(isOnLadder()) {
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
                tryMovingSideways(MrRobotState.WALKING_RIGHT, ANIM.mrrobot_walk_right.name());
            } else {
                stopMrRobot();
            }
        }
    }
    public void moveLeft() {
        if(mrRobotState != MrRobotState.WALKING_LEFT) {
            boolean tryMoveLeft = false;
            if(isOnLadder()) {
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
                tryMovingSideways(MrRobotState.WALKING_LEFT, ANIM.mrrobot_walk_left.name());
            } else {
                stopMrRobot();
            }
        }
    }
    public void completeMovement() {
        if(intendedMovement.MrRobotState != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!isFalling() && !isSlidingDown()) {
                setState(intendedMovement.MrRobotState);
            }
        }
    }

    @Override
    public void completed(Action completedAction) {
    }

    /**
     *
     * @return
     */
    public void stopMrRobot() {
        if(isOnLadder()) {
            changeState(MrRobotState.STANDING_ON_LADDER);
        } else {
            changeState(MrRobotState.STANDING_RIGHT);
        }
    }

    /**
     *
     * @param intendedState
     * @param animationName
     */
    private void tryMovingSideways(MrRobotState intendedState, String animationName) {
        intendedMovement.animationName = animationName;
        intendedMovement.MrRobotState = intendedState;
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
            intendedMovement.MrRobotState = MrRobotState.CLIMBING_UP;
        }
    }

    /**
     *
     */
    private void tryClimbingDown() {
        boolean climb = false;
        boolean alignLeftTile = false;
        if(isOnLadder()) {
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
            intendedMovement.MrRobotState = MrRobotState.CLIMBING_DOWN;
        }
    }

    /**
     *
     */
    public void mrRobotLands() {
        alignMrRobotVertically();
        stopMrRobot();
    }

    /**
     * @return true if Mr. Robot is climbing
     */
    public boolean mrRobotIsClimbing() {
        return mrRobotState == MrRobotState.CLIMBING_DOWN
                || mrRobotState == MrRobotState.CLIMBING_UP;
    }

    /**
     * @return True if Mr. Robot is jumping up or sideways
     */
    public boolean isJumping() {
        return mrRobotState == MrRobotState.JUMPUP_LEFT
                || mrRobotState == MrRobotState.JUMPUP_RIGHT
                || mrRobotState == MrRobotState.JUMP_LEFT
                || mrRobotState == MrRobotState.JUMP_RIGHT;
    }

    /**
     * @return True if Mr. Robot is on a ladder
     */
    public boolean isOnLadder() {
        return mrRobotState == MrRobotState.STANDING_ON_LADDER || mrRobotIsClimbing();
    }

    /**
     * @return True if Mr. Robot is walking
     */
    public boolean isWalking() {
        return mrRobotState == MrRobotState.WALKING_LEFT || mrRobotState == MrRobotState.WALKING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is falling
     */
    public boolean isFalling() {
        return mrRobotState == MrRobotState.DROP_LEFT || mrRobotState == MrRobotState.DROP_RIGHT
                || mrRobotState == MrRobotState.FALL_LEFT || mrRobotState == MrRobotState.FALL_RIGHT;
    }

    /**
     * @return True if Mr. Robot is sliding down
     */
    public boolean isSlidingDown() {
        return mrRobotState == MrRobotState.SLIDING_LEFT || mrRobotState == MrRobotState.SLIDING_RIGHT;
    }

    /**
     * @return True if Mr. Robot is rising up on an elevator.
     */
    public boolean isRisingUp() {
        return mrRobotState == MrRobotState.RISING_LEFT || mrRobotState == MrRobotState.RISING_RIGHT;
    }

    /**
     *
     * @param delta
     */
    public void moveMrRobot(float delta) {
        float x = mrrobotSprite.getX();
        float y = mrrobotSprite.getY();
        if (mrRobotState == MrRobotState.WALKING_RIGHT) {
            x += WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MrRobotState.WALKING_LEFT) {
            x -= WALK_SPEED * delta;
            y = alignMrRobotVertically();
        } else if (mrRobotState == MrRobotState.CLIMBING_UP) {
            y += WALK_SPEED * delta;
        } else if (mrRobotState == MrRobotState.CLIMBING_DOWN) {
            y -= WALK_SPEED * delta;
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
            if (!isFalling() && !isJumping()) {
                mrrobotSprite.startAction(dropDownAction, null);
            }
        } else if(mrRobotIsClimbing()) {
            if(mrRobotState == MrRobotState.CLIMBING_UP) {
                if(tileBehindId == NO_TILE && tileBelowId != TILE_LADDER_LEFT) {
                    changeState(MrRobotState.STANDING_RIGHT);
                    mrRobotLands();
                }
            } else {
                if(tileBehindId == TILE_LADDER_LEFT && tileBelowId != TILE_LADDER_LEFT) {
                    if(mrRobotIsNearlyAlignedVertically()) {
                        changeState(MrRobotState.STANDING_RIGHT);
                        mrRobotLands();
                    }
                }
            }
        } else {
            if(tileBelowId == TILE_DOT) {
                tileMap.clearCell(tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW));
                Hud.addScore(1);
            } else if(tileBelowId == TILE_BOMB && mrRobotIsNearlyAlignedVertically()) {
                Bombs.getInstance().igniteBomb((int)col, (int)line);
            }
            if(tileBelowId == TILE_ROLL_LEFT_1 || tileBelowId == TILE_ROLL_LEFT_2) {
                setPosition(mrrobotSprite.getX() - ROLLING_SPEED * delta, mrrobotSprite.getY());
            } else if(tileBelowId == TILE_ROLL_RIGHT_1 || tileBelowId == TILE_ROLL_RIGHT_2) {
                setPosition(mrrobotSprite.getX() + ROLLING_SPEED * delta, mrrobotSprite.getY());
            } else if(tileBelowId == TILE_ELEVATOR && tileBehindId == TILE_ELEVATOR && !isRisingUp() && !isJumping()) {
                mrrobotSprite.startAction(riseUpAction, null);
            }
        }

        if(line > 0) {
            if(tileFurtherBelowId == TILE_SLIDER && !isSlidingDown()
                    && !isJumping() && !isFalling()) {
                mrrobotSprite.startAction(slideDownAction, null);
            }
        }
    }

    public int getTileBehindId() {
        return tileBehindId;
    }
    public int getTileBelowId() {
        return tileBelowId;
    }
    public int getTileFurtherBelowId() {
        return tileFurtherBelowId;
    }
    public MrRobotState getState() {
        return mrRobotState;
    }

    /**
     * @return The TileMapTileLayer.Cell right below Mr. Robots feet.
     */
    public TiledMapTileLayer.Cell getCellBelow() {
        return cellBelow;
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrrobotSprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        setPosition(col * 8f + 11, mrrobotSprite.getY());
    }

    public float alignMrRobotVertically() {
        float y = mrrobotSprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        setPosition(mrrobotSprite.getX(), row * 8f);
        return mrrobotSprite.getY();
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his feet (lower sprite boundary).
     */
    public boolean mrRobotIsNearlyAlignedVertically() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his head (upper sprite boundary).
     */
    public boolean mrRobotIsNearlyAlignedVerticallyAtTop() {
        float y = mrrobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly aligned horizontally (center of sprite).
     */
    public boolean mrRobotIsNearlyAlignedHorizontally() {
        float x = mrrobotSprite.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        return Math.abs(diff) < 8f;
    }

    class IntendedMovement {
        public String animationName;
        public MrRobotState MrRobotState;

        public void clear() {
            animationName = null;
            MrRobotState = null;
        }
    }
}
