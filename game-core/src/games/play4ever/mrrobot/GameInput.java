package games.play4ever.mrrobot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Handles input controls for the Mr. Robot game, specifically supporting physical keyboards,
 * touchscreen overlay and possibly external gamepads.
 */
public class GameInput implements ControllerListener, InputProcessor {

    private static GameInput instance = new GameInput();

    private static boolean connected = false;
    private static boolean controllerUp = false;
    private static boolean controllerDown = false;
    private static boolean controllerLeft = false;
    private static boolean controllerRight = false;
    private static boolean controllerOk = false;
    private static boolean controllerBack = false;
    private static boolean backKeyPressed = false;

    private GameInput() {
        Controllers.addListener(this);
        Gdx.input.setInputProcessor(this);
        // Catch Android "back" button
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        for(Controller controller : Controllers.getControllers()) {
            Gdx.app.log("GameInput", "Controller found: " + controller.getName());
        }
        if(Controllers.getControllers().size > 0) {
            Gdx.app.log("GameInput", "At least one controller found.");
            connected = true;
        } else {
            Gdx.app.log("GameInput", "No controllers found.");
        }
    }

    public static boolean isButtonOkPressed() {
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.X)
                || (connected && controllerOk)
                || GamepadOverlay.isJumpPressed) {
            return true;
        }
        return false;
    }

    public static boolean isButtonBackPressed() {
        if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.B)
                || Gdx.input.isKeyPressed(Input.Keys.Y)
                || (connected && controllerBack)
                || backKeyPressed) {
            return true;
        }
        return false;
    }

    public static boolean isDownPressed() {
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)
                || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)
                || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)
                || (connected && controllerDown)
                || GamepadOverlay.isDownPressed) {
            return true;
        }
        return false;
    }

    public static boolean isUpPressed() {
        if(Gdx.input.isKeyPressed(Input.Keys.UP)
                || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)
                || (connected && controllerUp)
                || GamepadOverlay.isUpPressed) {
            return true;
        }
        return false;
    }

    public static boolean isRightPressed() {
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)
                || (connected && controllerRight)
                || GamepadOverlay.isRightPressed) {
            return true;
        }
        return false;
    }

    public static boolean isLeftPressed() {
        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)
                || Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)
                || (connected && controllerLeft)
                || GamepadOverlay.isLeftPressed) {
            return true;
        }
        return false;
    }

    private static void logDebugInfo() {
        Gdx.app.log("GameInput", "controller right: " + controllerRight);
        Gdx.app.log("GameInput", "controller left: " + controllerLeft);
        Gdx.app.log("GameInput", "controller up: " + controllerUp);
        Gdx.app.log("GameInput", "controller down: " + controllerDown);
    }

    @Override
    public void connected(Controller controller) {
        Gdx.app.log("GameInput", "Controller connected: " + controller.getName());
        connected = true;
    }

    @Override
    public void disconnected(Controller controller) {
        Gdx.app.log("GameInput", "Controller disconnected: " + controller.getName());
        connected = false;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        connected = true;
        controllerRight = false;
        controllerLeft = false;
        controllerUp = false;
        controllerDown = false;
        controllerOk = false;
        controllerBack = false;
        if(buttonCode == controller.getMapping().buttonDpadDown) {
            controllerDown = true;
        }
        if(buttonCode == controller.getMapping().buttonDpadUp) {
            controllerUp = true;
        }
        if(buttonCode == controller.getMapping().buttonDpadLeft) {
            controllerLeft = true;
        }
        if(buttonCode == controller.getMapping().buttonDpadRight) {
            controllerRight = true;
        }
        if(buttonCode == controller.getMapping().buttonA) {
            controllerOk = true;
        }
        if(buttonCode == controller.getMapping().buttonB) {
            controllerBack = true;
        }
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        connected = true;
        if(buttonCode == controller.getMapping().buttonDpadDown) {
            controllerDown = false;
        }
        if(buttonCode == controller.getMapping().buttonDpadUp) {
            controllerUp = false;
        }
        if(buttonCode == controller.getMapping().buttonDpadLeft) {
            controllerLeft = false;
        }
        if(buttonCode == controller.getMapping().buttonDpadRight) {
            controllerRight = false;
        }
        if(buttonCode == controller.getMapping().buttonA) {
            controllerOk = false;
        }
        if(buttonCode == controller.getMapping().buttonB) {
            controllerBack = false;
        }
        return true;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        controllerRight = false;
        controllerLeft = false;
        controllerUp = false;
        controllerDown = false;
        if(controller.getAxis(XBox360Pad.AXIS_LEFT_X) > 0.2f) {
            controllerRight = true;
        }
        if(controller.getAxis(XBox360Pad.AXIS_LEFT_X) < -0.2f) {
            controllerLeft = true;
        }
        if(controller.getAxis(XBox360Pad.AXIS_LEFT_Y) > 0.2f) {
            controllerDown = true;
        }
        if(controller.getAxis(XBox360Pad.AXIS_LEFT_Y) < -0.2f) {
            controllerUp = true;
        }
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log(getClass().getName(), "==============>>>  keyDown: " + keycode);
        if(keycode == Input.Keys.BACK) {
            backKeyPressed = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log(getClass().getName(), "keyUp: " + keycode);
        if(keycode == Input.Keys.BACK) {
            backKeyPressed = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log(getClass().getName(), "keyTyped: " + character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log(getClass().getName(), "touchDown");
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        Gdx.app.log(getClass().getName(), "touchUp");
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Gdx.app.log(getClass().getName(), "touchDragged");
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
//        Gdx.app.log(getClass().getName(), "mouseMoved");
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
//        Gdx.app.log(getClass().getName(), "scrolled");
        return false;
    }
}
