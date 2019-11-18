package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.jplay.gdx.Assets;
import com.jplay.gdx.DebugOutput;
import com.jplay.gdx.MoveableEntity;
import com.jplay.gdx.screens.ScreenUtil;

import static ch.marcelschoen.mrrobot.Tiles.TILE_DOT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_LEFT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_LADDER_RIGHT;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_LEFT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_1;
import static ch.marcelschoen.mrrobot.Tiles.TILE_ROLL_RIGHT_2;
import static ch.marcelschoen.mrrobot.Tiles.TILE_SLIDER;

public class MrRobot {

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

    private MoveableEntity mrRobot = new MoveableEntity("mrrobot", 0, 0);

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
            this.mrRobot.setSprite(animation.name(), Assets.instance().getAnimated2DSprite(animation.name()));
        }
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public void draw(SpriteBatch batch, float delta) {
        this.mrRobot.draw(batch, delta);
    }

    public void setPosition(float x, float y) {
        this.mrRobot.setPosition(x, y);
    }

    public void setState(String animationName) {
        this.mrRobot.setState(animationName);
    }

    public float getX() {
        return this.mrRobot.getX();
    }
    public float getY() {
        return this.mrRobot.getY();
    }

    public void handleInput(float delta) {
        if(Gdx.input.isTouched()) {
            camera.position.x += 100 * delta;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            ScreenUtil.dispose();
            System.exit(-1);
        }
        MRROBOT_STATE intendedState = null;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x -= 100 * delta;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_LEFT) {
                    if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                        mrRobot.setState("mrrobot_walk_left");
                        intendedState = MRROBOT_STATE.WALKING_LEFT;
                    }
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camera.position.x += 100 * delta;
            } else {
                if(mrRobotState != MRROBOT_STATE.WALKING_RIGHT) {
                    if(!mrRobotIsClimbing() && !mrRobotIsFalling() && !mrRobotIsSliding()) {
                        mrRobot.setState("mrrobot_walk_right");
                        intendedState = MRROBOT_STATE.WALKING_RIGHT;
                    }
                }
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            intendedState = mrRobotClimbing(MRROBOT_STATE.CLIMBING_UP);
        } else {
            // Stop movement
            intendedState = stopMrRobot();
        }

        if(intendedState != null) {
            // Mr. Roobot can only move around if he isn't currently falling down
            if(!mrRobotIsFalling() && !mrRobotIsSliding()) {
                mrRobotState = intendedState;
            }
        }

        moveMrRobot(delta);
        checkMrRobot(delta);
    }

    public MRROBOT_STATE stopMrRobot() {
        if(mrRobotIsClimbing()) {
            mrRobot.setState("mrrobot_stand_on_ladder");
            return MRROBOT_STATE.STANDING_ON_LADDER;
        } else {
            if(mrRobotState.isFacingRight) {
                if(mrRobotState != MRROBOT_STATE.STANDING_RIGHT) {
                    mrRobot.setState("mrrobot_stand_right");
                    return MRROBOT_STATE.STANDING_RIGHT;
                }
            } else {
                if(mrRobotState != MRROBOT_STATE.STANDING_LEFT) {
                    mrRobot.setState("mrrobot_stand_left");
                    return MRROBOT_STATE.STANDING_LEFT;
                }
            }
        }
        return null;
    }

    public void moveMrRobot(float delta) {
        float x = mrRobot.getX();
        float y = mrRobot.getY();
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

        mrRobot.setPosition(x, y);
    }

    public void mrRobotLands() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.STANDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.STANDING_LEFT;
        }
    }

    public void mrRobotStartsFalling() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.FALLING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.FALLING_LEFT;
        }
    }

    public void mrRobotStartsSliding() {
        if(mrRobotState.isFacingRight()) {
            mrRobotState = MRROBOT_STATE.SLIDING_RIGHT;
        } else {
            mrRobotState = MRROBOT_STATE.SLIDING_LEFT;
        }
    }

    public boolean mrRobotIsClimbing() {
        return mrRobotState == MRROBOT_STATE.CLIMBING_DOWN
                || mrRobotState == MRROBOT_STATE.CLIMBING_UP
                || mrRobotState == MRROBOT_STATE.STANDING_ON_LADDER;
    }

    public boolean mrRobotIsFalling() {
        return mrRobotState == MRROBOT_STATE.FALLING_LEFT || mrRobotState == MRROBOT_STATE.FALLING_RIGHT;
    }

    public boolean mrRobotIsSliding() {
        return mrRobotState == MRROBOT_STATE.SLIDING_LEFT || mrRobotState == MRROBOT_STATE.SLIDING_RIGHT;
    }

    public void checkMrRobot(float delta) {
        float x = mrRobot.getX() + 12f;
        float y = mrRobot.getY()+ 7f;

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
                    mrRobot.setPosition(mrRobot.getX(), line * 8f);
                }
            }
            if(cell.getTile().getId() == TILE_DOT) {
                tileMap.clearCell(cell);
                Hud.addScore(1);
            }
            if(cell.getTile().getId() == TILE_ROLL_LEFT_1 || cell.getTile().getId() == TILE_ROLL_LEFT_2) {
                mrRobot.setPosition(mrRobot.getX() - 20f * delta, mrRobot.getY());
            } else if(cell.getTile().getId() == TILE_ROLL_RIGHT_1 || cell.getTile().getId() == TILE_ROLL_RIGHT_2) {
                mrRobot.setPosition(mrRobot.getX() + 20f * delta, mrRobot.getY());
            }
        }

        if(line > 0) {
            cell = tileMap.getCell((int)col, (int)line - 1);
            if(cell != null && cell.getTile().getId() == TILE_SLIDER && !mrRobotIsSliding()) {
                mrRobotStartsSliding();
            }
        }
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
            mrRobot.setState("mrrobot_climb");
            mrRobotState = climbingType;
        }
        return null;
    }

    public float alignMrRobotVertically() {
        float y = mrRobot.getY() + 12;
        int row = (int)(y / 8f) - 1;
        mrRobot.setPosition(mrRobot.getX(), row * 8f);
        return mrRobot.getY();
    }

    public void alignMrRobotHorizontally(boolean alignLeftTile) {
        float x = mrRobot.getX() + 12;
        int col = (int)(x / 8f) - 2;
        if(alignLeftTile) {
            col --;
        }
        mrRobot.setPosition(col * 8f + 12, mrRobot.getY());
    }

    public boolean mrRobotIsNearlyAligned() {
        float x = mrRobot.getX() + 6f;
        int col = (int)(x / 8f) + 1;
        float diff = x - (col * 8f);
        /////////DebugOutput.log("x: " + x + ", col: " + col + ", diff: " + diff, 0, 100);
        return Math.abs(diff) < 8f;
    }
}
