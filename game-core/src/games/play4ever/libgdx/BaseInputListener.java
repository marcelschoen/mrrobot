package games.play4ever.libgdx;

public class BaseInputListener implements InputListener {

    private int[] keyCodes = new int[0];

    /**
     * Creates an input listener which will check for the given list
     * of keycodes (and controller- and touch-input).
     *
     * @param keyCodesToProcess
     */
    public BaseInputListener(int[] keyCodesToProcess) {
        keyCodes = keyCodesToProcess;
    }

    public int[] getKeysToListenTo() {
        return keyCodes;
    }

    @Override
    public void directionUp() {
    }

    @Override
    public void directionUpOnce() {
    }

    @Override
    public void directionRight() {

    }

    @Override
    public void directionRightOnce() {

    }

    @Override
    public void directionLeft() {

    }

    @Override
    public void directionLeftOnce() {

    }

    @Override
    public void directionDown() {

    }

    @Override
    public void directionDownOnce() {

    }

    @Override
    public void keyPressed(int keyCode) {

    }

    @Override
    public void keyPressedOnce(int keyCode) {

    }

    @Override
    public void buttonPressed() {

    }

    @Override
    public void buttonPressedOnce() {

    }
}
