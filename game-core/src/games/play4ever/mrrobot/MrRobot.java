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
import games.play4ever.mrrobot.screens.PlayScreen;

import static games.play4ever.mrrobot.MrRobotState.STANDING_RIGHT;
import static games.play4ever.mrrobot.Tiles.NO_TILE;
import static games.play4ever.mrrobot.Tiles.TILE_BOMB;
import static games.play4ever.mrrobot.Tiles.TILE_BOMB_EXPLODING;
import static games.play4ever.mrrobot.Tiles.TILE_DOT;
import static games.play4ever.mrrobot.Tiles.TILE_ELEVATOR;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_LEFT;
import static games.play4ever.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_LEFT_1;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_LEFT_2;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_RIGHT_1;
import static games.play4ever.mrrobot.Tiles.TILE_ROLL_RIGHT_2;
import static games.play4ever.mrrobot.Tiles.TILE_SLIDER;
import static games.play4ever.mrrobot.Tiles.TILE_TRAMPOLINE_MIDDLE;

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
        mrrobot_dies,
        mrrobot_teleport,
        mrrobot_shield,
        shield_item,
        magnet_left,
        magnet_right,
        trampoline,
        trampoline_still,
        trampoline_small,
        trampoline_big
    };

    private static final float WALK_SPEED = 40;
    private static final float ROLLING_SPEED = 26;
    private static final float PULLING_SPEED = 30;

    private AnimatedSprite mrRobotSprite = null;
    private AnimatedSprite shieldSprite = null;

    private TiledMapTileLayer.Cell cellBelow = null;

    private int tileBehindId = NO_TILE;
    private int tileBelowId = NO_TILE;
    private TiledMapTileCellWrapper tileBelow = null;
    private int tileFurtherBelowId = NO_TILE;

    private TileMap tileMap;

    private boolean magnetRightActive = false;
    private boolean magnetLeftActive = false;

    private MrRobotState mrRobotState = STANDING_RIGHT;

    /** Pre-defined actions. */
    private Action jumpSidewaysRightAction = null;
    private Action jumpSidewaysLeftAction = null;
    private Action jumpUpAction = null;
    private Action dyingAction = null;
    private Action teleportAction = null;
    private Action shieldAction = null;
    private Action magnetLeftAction = null;
    private Action magnetRightAction = null;
    private Action slideDownAction = null;
    private Action dropDownAction = null;
    private Action riseUpAction = null;

    private PlayScreen playScreen = null;

    /**
     */
    public MrRobot(PlayScreen playScreen) {
        this.playScreen = playScreen;
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

        createSprites();

        // Initialize pre-defined actions for Mr. Robot

        // we keep a specific reference to the movement action within the sideays jump, to be
        // able to set the direction at the time of commencing the jump
        jumpSidewaysRightAction = new ActionBuilder()
                .custom(new JumpSidewaysAction(this, false))
                .custom(new FallDownAction(this))
                .build();
        jumpSidewaysLeftAction = new ActionBuilder()
                .custom(new JumpSidewaysAction(this, true))
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
        magnetLeftAction = new ActionBuilder()
                .debugLog("Picked up magnet left....", 10)
                .build();
        magnetRightAction = new ActionBuilder()
                .debugLog("Picked up magnet right....", 10)
                .build();
        dyingAction = new ActionBuilder()
                .setAnimation(ANIM.mrrobot_dies.name())
                .stayPut(0.3f)
                .setAnimation(ANIM.mrrobot_downfall.name())
                .moveTo(2, -120, 1.4f, Interpolation.slowFast)
                .setVisibility(false, 0.1f)
                .build();

        Collision.addListener(this);
    }

    public void createSprites() {
        List<String> animationNames = new ArrayList<>();
        for(ANIM animation : ANIM.values()) {
            animationNames.add(animation.name());
        }
        this.mrRobotSprite = Sprites.createSprite(animationNames, SpriteTypes.MR_ROBOT);
        this.mrRobotSprite.setDefaultCollisionBounds(7, 4, 12, 14);
        this.mrRobotSprite.setVisible(true);
        Collision.addRectangles(this.mrRobotSprite);

        this.shieldSprite = Sprites.createSprite(ANIM.mrrobot_shield.name(), SpriteTypes.SHIELDS);
        this.shieldSprite.setVisible(false);
        this.shieldSprite.attachToSprite(this.mrRobotSprite, 0, 0);

        this.shieldSprite.setZ(AnimatedSprite.FOREGROUND);
        this.mrRobotSprite.setZ(AnimatedSprite.FOREGROUND);
    }

    /**
     * @param tileMap Sets the current tilemap instance.
     */
    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    @Override
    public void spritesCollided(AnimatedSprite spriteOne, AnimatedSprite spriteTwo, Rectangle overlapRectangle) {
        AnimatedSprite otherSprite = spriteTwo;
        if(spriteOne.getType() != SpriteTypes.MR_ROBOT) {
            otherSprite = spriteOne;
        }
        if(otherSprite.getType() == SpriteTypes.MAGNET_LEFT) {
            otherSprite.startAction(magnetLeftAction, this);
            otherSprite.setVisible(false);
            magnetLeftActive = true;
        }
        if(otherSprite.getType() == SpriteTypes.MAGNET_RIGHT) {
            otherSprite.startAction(magnetRightAction, this);
            otherSprite.setVisible(false);
            magnetRightActive = true;
        }
        if(otherSprite.getType() == SpriteTypes.SHIELDS) {
            Hud.addScore(100);
            shieldSprite.startAction(shieldAction, this);
            otherSprite.setVisible(false);
        }
        if(otherSprite.getType() == SpriteTypes.ONE_UP) {
            Hud.addLive();
            otherSprite.setVisible(false);
        }
        if(otherSprite.getType() == SpriteTypes.FLAMES) {
            if(shieldSprite.getCurrentAction() != null && shieldSprite.getCurrentAction().isRunning()) {
                if(!Flame.getFlameOfSprite(otherSprite).isDying()) {
                    // Mr. Robot vanquishes flame with shield
                    Flame.getFlameOfSprite(otherSprite).die();
                    Hud.addScore(150);
                }
            } else if(!Flame.getFlameOfSprite(otherSprite).isDying()) {
                // Mr. Robot dies (if flame isn't already blue / dying)
                die();
            }
        }
    }

    public void die() {
        if(!mrRobotState.isDying()) {
            setState(MrRobotState.DYING);
            this.mrRobotSprite.startAction(dyingAction, this);
        }
    }

    /**
     * @return The Mr. Robot sprite instance.
     */
    public AnimatedSprite getMrRobotSprite() {
        return this.mrRobotSprite;
    }

    /**
     * Sets the position of Mr. Robot on screen.
     *
     * @param x The x-coordinate in pixels.
     * @param y The y-coordinate in pixels.
     */
    public void setPosition(float x, float y) {
        if(isCrossingLeftScreenBoundary(x)) {
            x = -5;
        } else if(isCrossingRightScreenBoundary(x)) {
            x = MrRobotGame.VIRTUAL_WIDTH - 19;
        }
        this.mrRobotSprite.setPosition(x, y);
        cellBelow = tileMap.getTileMapCell(TileMap.CELL_TYPE.BELOW);
        tileBehindId = tileMap.getTileMapTile(TileMap.CELL_TYPE.BEHIND);

        tileBelow = (TiledMapTileCellWrapper)tileMap.getTileMapCell(this.mrRobotSprite, TileMap.CELL_TYPE.BELOW);
        tileBelowId = tileBelow == null ? -1 : tileBelow.getTile().getId();

        tileFurtherBelowId = tileMap.getTileMapTile(TileMap.CELL_TYPE.FURTHER_BELOW);
    }

    public void changeState(MrRobotState state) {
        this.mrRobotState = state.changeFrom(this.mrRobotState, state);
        this.mrRobotSprite.showAnimation(mrRobotState.getAnimationName());
    }

    public boolean isCrossingLeftScreenBoundary(float x) {
        return x < -5;
    }

    public boolean isCrossingRightScreenBoundary(float x) {
        return x > MrRobotGame.VIRTUAL_WIDTH - 19;
    }

    public boolean isCrossingLowerScreenBoundary(float y) {
        return y < 0;
    }

    /**
     * @param state Sets
     */
    public void setState(MrRobotState state) {
        this.mrRobotState = state;
        this.mrRobotSprite.showAnimation(state.getAnimationName());
    }

    /**
     * @return The x-coordinate of the Mr. Robot sprite in pixels.
     */
    public float getX() {
        return this.mrRobotSprite.getX();
    }

    /**
     * @return The y-coordinate of the Mr. Robot sprite in pixels.
     */
    public float getY() {
        return this.mrRobotSprite.getY();
    }

    /**
     * Handles input for Mr. Robot movements.
     *
     * @param delta time since last screen refresh in seconds.
     */
    public void handleInput(float delta) {

        if(mrRobotState.isInputBlocked()) {
            return;
        }

        if(GameInput.isButtonBackPressed()) {
            MrRobotGame.instance().setScreen(MrRobotGame.instance().titleScreen);
        }

        intendedMovement.clear();

//        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || GamepadOverlay.isLeftPressed) {
        if(GameInput.isLeftPressed()) {
            moveLeft();
//        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || GamepadOverlay.isRightPressed) {
        } else if(GameInput.isRightPressed()) {
            moveRight();
//        } else if(Gdx.input.isKeyPressed(Input.Keys.UP) || GamepadOverlay.isUpPressed) {
        } else if(GameInput.isUpPressed()) {
            if(!mrRobotIsClimbing()) {
                tryClimbingUp();
            }
//        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || GamepadOverlay.isDownPressed) {
        } else if(GameInput.isDownPressed()) {
            if(tileBelowId == Tiles.TILE_TELEPORTER) {
                mrRobotSprite.startAction(teleportAction, null);
            } else if(!mrRobotIsClimbing()) {
                tryClimbingDown();
            }
        } else {
            if((mrRobotIsClimbing() || isWalking()) && !mrRobotState.isDying()) {
                // Stop movement
                stopMrRobot();
            }
        }


        // ====================== TEMPORARY - ALLOW RESTART BY F1 =====================
        if(MrRobotGame.testing) {
            if(Gdx.input.isKeyPressed(Input.Keys.F1)) {
                Gdx.app.log("MrRobot", ">>>> F1");
                // F1 - restart level
                tileMap.restart();
            } else if(Gdx.input.isKeyPressed(Input.Keys.F2)) {
                Gdx.app.log("MrRobot", ">>>> F2");
                // F2 - next level
                TileMap.switchToNextMap();
                playScreen.startLevel();
            } else if(Gdx.input.isKeyPressed(Input.Keys.F3)) {
                Gdx.app.log("MrRobot", ">>>> F3");
                // F3 - back to menu
            }
        }
        // ====================== TEMPORARY - ALLOW RESTART BY F1 =====================

        if(GameInput.isButtonOkPressed()) {
            if(GameInput.isRightPressed()) {
                mrRobotSprite.startAction(jumpSidewaysRightAction, null);
            } else if(GameInput.isLeftPressed()) {
                mrRobotSprite.startAction(jumpSidewaysLeftAction, null);
            } else {
                changeState(MrRobotState.JUMPUP_RIGHT);
                mrRobotSprite.startAction(jumpUpAction, this);
            }
        }

/*
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || GamepadOverlay.isJumpPressed) {
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || GamepadOverlay.isRightPressed) {
                    getMrRobotSprite().startAction(jumpSidewaysRightAction, null);
                } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || GamepadOverlay.isLeftPressed) {
                    getMrRobotSprite().startAction(jumpSidewaysLeftAction, null);
                } else {
                    changeState(MrRobotState.JUMPUP_RIGHT);
                    getMrRobotSprite().startAction(jumpUpAction, this);
                }
        }
        */
        completeMovement();
    }

    public void startLevel() {
        Sprites.clearAll();
        Collision.clearRectangles();
        createSprites();
        this.tileMap = new TileMap(TileMap.getCurrentMap(), this);
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
            if(!isFalling() && !isSlidingDown() && !mrRobotState.isDying()) {
                setState(intendedMovement.MrRobotState);
            }
        }
    }

    // ================== ActionListener =====================

    @Override
    public void completed(Action completedAction) {
        if(completedAction.getFirstActionInChain() == dyingAction) {
            // handle death
            Hud.removeLive();
            // TODO - some fade-out / fade-in effect?

            // reset Mr. Robot and flames and optionally rest of map
            this.tileMap.restart();
        } else if(completedAction.getFirstActionInChain() == magnetLeftAction) {
            // magnet left item is used up
            magnetLeftActive = false;
        } else if(completedAction.getFirstActionInChain() == magnetRightAction) {
            // magnet right item is used up
            magnetRightActive = false;
        }
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
        float x = mrRobotSprite.getX();
        float y = mrRobotSprite.getY();
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
        float x = mrRobotSprite.getX() + 12f;
        float y = mrRobotSprite.getY() + 7f;

        float line = (y / 8f) - 1f;

        if(isFalling()) {
            if(tileBelowId == TILE_TRAMPOLINE_MIDDLE && mrRobotIsNearlyAlignedVertically()) {
                Gdx.app.log("MrRobot", "* Fell on trampoline *");
            }
        }
        if(isCrossingLowerScreenBoundary(y)) {
            // falling out of screen
            die();
        } else if(tileBelowId == NO_TILE) {
            if (!isFalling() && !isJumping() && !mrRobotState.isDying()) {
                mrRobotSprite.startAction(dropDownAction, null);
            }
        } else if(tileBelowId == TILE_BOMB_EXPLODING) {
            die();
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
                tileMap.decreaseNumberOfDots();
                Hud.addScore(1);
            } else if(tileBelowId == TILE_BOMB && mrRobotIsNearlyAlignedVertically()) {
                Bombs.getInstance().igniteBomb(tileBelow.getColumn(), tileBelow.getRow());
            }
            if(magnetLeftActive && (getState() == MrRobotState.JUMP_RIGHT || getState() == MrRobotState.FALL_RIGHT) && Magnets.isMagnetLeftClose(getX(), getY())) {
                setPosition(mrRobotSprite.getX() + PULLING_SPEED * delta, mrRobotSprite.getY());
            } else if(magnetRightActive && (getState() == MrRobotState.JUMP_LEFT || getState() == MrRobotState.FALL_LEFT) && Magnets.isMagnetRightClose(getX(), getY())) {
                setPosition(mrRobotSprite.getX() - PULLING_SPEED * delta, mrRobotSprite.getY());
            }
            if(tileBelowId == TILE_ROLL_LEFT_1 || tileBelowId == TILE_ROLL_LEFT_2) {
                setPosition(mrRobotSprite.getX() - ROLLING_SPEED * delta, mrRobotSprite.getY());
            } else if(tileBelowId == TILE_ROLL_RIGHT_1 || tileBelowId == TILE_ROLL_RIGHT_2) {
                setPosition(mrRobotSprite.getX() + ROLLING_SPEED * delta, mrRobotSprite.getY());
            } else if(tileBelowId == TILE_ELEVATOR && tileBehindId == TILE_ELEVATOR && !isRisingUp() && !isJumping()) {
                mrRobotSprite.startAction(riseUpAction, null);
            }
        }

        if(line > 0) {
            if(tileFurtherBelowId == TILE_SLIDER && !isSlidingDown()
                    && !isJumping() && !isFalling()) {
                mrRobotSprite.startAction(slideDownAction, null);
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
    public TiledMapTileCellWrapper getCellBelow() {
        return (TiledMapTileCellWrapper)cellBelow;
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrRobotSprite.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        setPosition(col * 8f + 11, mrRobotSprite.getY());
    }

    public float alignMrRobotVertically() {
        float y = mrRobotSprite.getY() + 12;
        int row = (int)(y / 8f) - 1;
        setPosition(mrRobotSprite.getX(), row * 8f);
        return mrRobotSprite.getY();
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his feet (lower sprite boundary).
     */
    public boolean mrRobotIsNearlyAlignedVertically() {
        float y = mrRobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return Math.abs(diff) < 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly vertically aligned with his head (upper sprite boundary).
     */
    public boolean mrRobotIsNearlyAlignedVerticallyAtTop() {
        float y = mrRobotSprite.getY();
        int row = (int)(y / 8f);
        float diff = y - (row * 8f);
        return diff > 1.5f;
    }

    /**
     * @return True if Mr. Robot is nearly aligned horizontally (center of sprite).
     */
    public boolean mrRobotIsNearlyAlignedHorizontally() {
        float x = mrRobotSprite.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        return Math.abs(diff) < 8f;
    }

    /**
     * @return The tilemap row BELOW Mr. Robot (i.e. the row Mr. Robot is standing on)
     */
    public int getTileMapRow() {
        return TileMap.getRow(mrRobotSprite);
    }

    /**
     * @return The tilemap column of the middle of Mr. Robot
     */
    public int getTileMapColumn() {
        return TileMap.getColumn(mrRobotSprite);
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
