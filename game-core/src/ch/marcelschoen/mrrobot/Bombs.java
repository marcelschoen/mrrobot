package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.maps.tiled.TiledMapTile;

/**
 * Manages bombs and triggers their explosions.
 *
 * @author Marcel Schoen
 */
public class Bombs {

    private static final int COLUMNS = 40;
    private static final int ROWS = 22;

    private static Bombs instance = new Bombs();

    private static TileMap tileMap;

    private static TiledMapTile IGNITED_TILE = null;
    private static TiledMapTile BURNING_TILE = null;
    private static TiledMapTile EXPLODING_TILE = null;

    private static Bomb[][] bombs = new Bomb[COLUMNS][ROWS];

    public static Bombs getInstance() {
        return instance;
    }

    public void initialize(TileMap actualTimeMap) {
        tileMap = actualTimeMap;
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

    public void igniteBomb(int col, int row) {
        Bomb bomb = bombs[col][row];
        tileMap.getCell(bomb.col, bomb.row).setTile(IGNITED_TILE);
        bomb.timer = 3.0f;
        bomb.state = BombState.IGNITED;
    }

    public void processBombs(float delta) {
        for(int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS; row++) {
                processBomb(bombs[col][row], delta);
            }
        }
    }

    private void processBomb(Bomb bomb, float delta) {
        if(bomb.state != BombState.DEFAULT) {
            float timer = bomb.timer;
            // Ignition starts with 3.0
            if(timer > 2.5) {
                bomb.state = BombState.BURNING; // POSSIBLY OBSOLETE
                tileMap.getCell(bomb.col, bomb.row).setTile(BURNING_TILE);
            } else if(timer > 0.4) {
                bomb.state = BombState.EXPLODING; // POSSIBLY OBSOLETE
                tileMap.getCell(bomb.col, bomb.row).setTile(EXPLODING_TILE);

                // TODO - EXPLOSIONS COLLISION WITH MR. ROBOT / FLAMES

            } else if(timer <= 0) {
                bomb.state = null;
                tileMap.getCell(bomb.col, bomb.row).setTile(null);
            }
            bomb.timer -= delta;
        }
    }

    class Bomb {
        private int col;
        private int row;
        private BombState state;
        private float timer = 0;

        public Bomb(int col, int row) {
            this.col = col;
            this.row = row;
        }

        public void execute(float delta) {
            if(timer > 0) {
                timer -= delta;
            } else {
                if(state == BombState.IGNITED) {
                    timer = 0.2f;
                    tileMap.getCell(col, row).setTile(IGNITED_TILE);
                } else if(state == BombState.BURNING) {
                    timer = 3f;
                    tileMap.getCell(col, row).setTile(BURNING_TILE);
                } else if(state == BombState.EXPLODING) {
                    timer = 0.6f;
                    tileMap.getCell(col, row).setTile(EXPLODING_TILE);
                }
            }
        }
    }
}
