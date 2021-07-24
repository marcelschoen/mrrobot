package games.play4ever.mrrobot.actions;

import games.play4ever.libgdx.sprites.action.Action;
import games.play4ever.mrrobot.MrRobot;

import static games.play4ever.mrrobot.MrRobotState.JUMP_RIGHT;

/**
 * Lets Mr. Robot jump sideways.
 *
 * @author Marcel Schoen
 */
public class JumpSidewaysAction extends Action {

    private static final float JUMPING_SPEED = 60;

    /** Reference to Mr. Robot */
    private MrRobot mrRobot = null;

    /**
     * Creates the teleport action.
     *
     * @param mrRobot The Mr. Robot instance.
     */
    public JumpSidewaysAction(MrRobot mrRobot) {
        this.mrRobot = mrRobot;
    }

    @Override
    public void doStart() {
        setDuration(0.5f);
        mrRobot.changeState(JUMP_RIGHT);
    }

    @Override
    protected void execute(float delta) {
        float x = mrRobot.getX();
        float y = mrRobot.getY();
        if(executionTimer > 0.1) {
            y += JUMPING_SPEED * delta;
        }
        if(mrRobot.getState() == JUMP_RIGHT) {
            x += JUMPING_SPEED * delta;
        } else {
            x -= JUMPING_SPEED * delta;
        }
        if(mrRobot.isCrossingLeftScreenBoundary(x)) {
            mrRobot.setState(mrRobot.getState().getReverse());
        } else if(mrRobot.isCrossingRightScreenBoundary(x)) {
            mrRobot.setState(mrRobot.getState().getReverse());
        }
        mrRobot.setPosition(x, y);
    }
}
