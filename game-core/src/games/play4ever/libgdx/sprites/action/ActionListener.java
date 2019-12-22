package games.play4ever.libgdx.sprites.action;

/**
 * Callback interface for a listener to an action execution event.
 */
public interface ActionListener {

    /**
     * Notifies the listener that the action is completed.
     *
     * @param action The action
     */
    public void completed(Action action);
}
