package games.play4ever.mrrobot;

import static games.play4ever.mrrobot.TileMap.COLUMNS;
import static games.play4ever.mrrobot.TileMap.ROWS;

/**
 * Manages trampolins.
 *
 * @author Marcel Schoen
 */
public class Trampolins {

    private static Trampolins instance = new Trampolins();

    private static TileMap tileMap;

    private static Trampolin[][] trampolins = new Trampolin[COLUMNS][ROWS];

    public static Trampolins getInstance() {
        return instance;
    }

    public void initialize(TileMap actualTimeMap) {
        tileMap = actualTimeMap;
        for(int col = 0; col < COLUMNS; col++) {
            for(int row = 0; row < ROWS; row++) {
                trampolins[col][row] = null;
                if(tileMap.getCell(col, row) != null) {
                    if(tileMap.getCell(col, row).getTile().getId() == Tiles.TILE_TRAMPOLINE) {
                        if (col > 0 && tileMap.getCell(col - 1, row).getTile().getId() == Tiles.TILE_TRAMPOLINE_LEFT) {
                            // first trampolin part to the left
                        } else if (col < COLUMNS - 1 && tileMap.getCell(col + 1, row).getTile().getId() == Tiles.TILE_TRAMPOLINE_RIGHT) {
                            // last trampolin part to the right
                        } else {
                            // some center trampolin part
                        }
                    }
                }
            }
        }
    }


    class Trampolin {
        public int row;
        public int leftCol;
        public int rightCol;
        private float timer = 0;

        public Trampolin(int col, int row) {
            this.row = row;
        }

    }
}
