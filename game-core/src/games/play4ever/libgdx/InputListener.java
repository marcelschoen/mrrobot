package games.play4ever.libgdx;

/**
 * Handles touch-, key- and controller input. There are some methods to
 * handle the typical 4 directions up, down, left and right independently
 * from the input source.
 */
public interface InputListener {

    public void directionUp();

    public void directionUpOnce();

    public void directionRight();

    public void directionRightOnce();

    public void directionLeft();

    public void directionLeftOnce();

    public void directionDown();

    public void directionDownOnce();

    public void keyPressed(int keyCode);

    public void keyPressedOnce(int keyCode);

    public void touched(int xCoordinate, int yCoordinate);

    public int[] getKeysToListenTo();

    public void buttonPressed();

    public void buttonPressedOnce();
}
