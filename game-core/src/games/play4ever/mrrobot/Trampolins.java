package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;

import static games.play4ever.mrrobot.TileMap.COLUMNS;
import static games.play4ever.mrrobot.TileMap.ROWS;

/**
 * Manages trampolins.
 *
 * @author Marcel Schoen
 */
public class Trampolins {

    private static Trampoline[][] trampolins = new Trampoline[COLUMNS][ROWS];

    public void reset() {
        trampolins = new Trampoline[COLUMNS][ROWS];
    }

    /**
     * Add the given trampoline, and the 3 tiles linked to it.
     *
     * @param column Column of the first, left-most tile of the 3 middle tiles of a trampoline.
     * @param row Row of the first, left-most tile of the 3 middle tiles of a trampoline.
     * @param trampoline The trampoline to which the tiles belong.
     */
    public static void addTrampoline(int column, int row, Trampoline trampoline) {
        for(int colCt = column; colCt < column + 7; colCt++) {
            trampolins[colCt][row] = trampoline;
        }
        Gdx.app.log("Trampolins", "Add trampolin at " + column + "," + row);
    }

    public static Trampoline getTrampolineForTile(int column, int row) {
        return trampolins[column][row];
    }
}
