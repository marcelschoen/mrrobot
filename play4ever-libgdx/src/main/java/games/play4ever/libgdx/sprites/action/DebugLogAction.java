package games.play4ever.libgdx.sprites.action;

import com.badlogic.gdx.Gdx;

/**
 * Writes debug log text.
 *
 * @author Marcel Schoen
 */
public class DebugLogAction extends Action {

    /** The debug message to log. */
    private String logMessage = null;

    /**
     * Prints a log message and waits for a given time.
     *
     * @param debugMessage The debug log message to print.
     * @param duration The time for the action; if set to 0 or smaller, it will be done immediately.
     */
    public void log(String debugMessage, float duration) {
        logMessage = debugMessage;
        setDuration(duration);
    }

    @Override
    public void doStart() {
        if(logMessage != null) {
            Gdx.app.log("DebugLogAction", logMessage);
        }
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
