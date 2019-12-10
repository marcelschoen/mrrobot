package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.jplay.gdx.DebugOutput;

import static ch.marcelschoen.mrrobot.TileMap.COLUMNS;
import static ch.marcelschoen.mrrobot.TileMap.ROWS;

/**
 * Manages bombs and triggers their explosions.
 *
 * @author Marcel Schoen
 */
public class Bombs {

    /** Singleton reference of this class. */
    private static Bombs instance = new Bombs();

    /** Reference to the current tilemap. */
    private static TileMap tileMap;

    /** Reference to animated ignited bomb tile. */
    private static TiledMapTile IGNITED_TILE = null;

    /** Reference to animated burning bomb tile. */
    private static TiledMapTile BURNING_TILE = null;

    /** Reference to animated exploding bomb tile. */
    private static TiledMapTile EXPLODING_TILE = null;

    /** Array with all bombs in the tilemap. */
    private static Bomb[][] bombs = new Bomb[COLUMNS][ROWS];

    /**
     * Returns the instance to the Bombs management class.
     *
     * @return The singleton instances.
     */
    public static Bombs getInstance() {
        return instance;
    }

    /**
     * Initializes the bomb management based on the given tilemap.
     *
     * @param actualTileMap The tilemap instance.
     */
    public void initialize(TileMap actualTileMap) {
        tileMap = actualTileMap;
        for(int col = 0; col < COLUMNS; col++) {
            for(int row = 0; row < ROWS; row++) {
                bombs[col][row] = null;
                if(tileMap.getCell(col, row) != null &&
                        tileMap.getCell(col, row).getTile().getId() == Tiles.TILE_BOMB) {
                    bombs[col][row] = new Bomb(col, row);
                }
            }
        }
        IGNITED_TILE = tileMap.getTileSet().getTile(Tiles.TILE_BOMB_IGNITING);
        BURNING_TILE = tileMap.getTileSet().getTile(Tiles.TILE_BOMB_BURNING);
        EXPLODING_TILE = tileMap.getTileSet().getTile(Tiles.TILE_BOMB_EXPLODING);
    }

    /**
     * Ignites the bomb in the given column and row of the tilemap.
     *
     * @param column The column in the tilemap.
     * @param row The row in the tilemap.
     */
    public void igniteBomb(int column, int row) {
        System.out.println("* IGNITE BOMB AT: " + column + " / " + row);
        DebugOutput.flicker(Color.YELLOW);
        Bomb bomb = bombs[column][row];
        bomb.setState(BombState.IGNITED);
    }

    /**
     * Processes all bombs in the tilemap. If any of them are ignited, this
     * will make them continue burning and finally exploding.
     *
     * @param delta The time delta in seconds since the last refresh.
     */
    public void processBombs(float delta) {
        for(int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS; row++) {
                if(bombs[col][row] != null) {
                    bombs[col][row].execute(delta);
                }
            }
        }
    }

    /**
     * Holds the state of one bomb in the tilemap.
     */
    class Bomb {
        private int col;
        private int row;
        private BombState state;
        private float timer = 0;

        public Bomb(int col, int row) {
            this.col = col;
            this.row = row;
            setState(BombState.DEFAULT);
        }

        public void setState(BombState state) {
            this.state = state;
            this.timer = state.threshold;
            if(state == BombState.IGNITED) {
                tileMap.getCell(col, row).setTile(IGNITED_TILE);
            } else if(state == BombState.BURNING) {
                tileMap.getCell(col, row).setTile(BURNING_TILE);
            } else if(state == BombState.EXPLODING) {
                tileMap.getCell(col, row).setTile(EXPLODING_TILE);
            } else if(state == BombState.GONE) {
                tileMap.getCell(col, row).setTile(null);
            }
        }

        public void execute(float delta) {
            if (timer > 0) {
                timer -= delta;
                if(state == BombState.EXPLODING) {
                    // Check for collision with Mr. Robot or Flames

                }
                if (timer <= 0) {
                    if (state.nextState != null) {
                        setState(state.nextState);
                    }
                }
            }
        }
    }
}
