package games.play4ever.mrrobot;

/**
 * All the states that Mr. Robot can be in during gameplay. Each state also
 * holds information about which direction Mr. Robot is facing (left/right)
 * and which animation to use.
 *
 * @author Marcel Schoen
 */
public enum MrRobotState {
    JUMP_RIGHT(true, MrRobot.ANIM.mrrobot_jump_right.name(), true),
    JUMP_LEFT(false, MrRobot.ANIM.mrrobot_jump_left.name(), true),
    JUMPUP_RIGHT(true, MrRobot.ANIM.mrrobot_jump_right.name(), true),
    JUMPUP_LEFT(false, MrRobot.ANIM.mrrobot_jump_left.name(), true),
    CLIMBING_UP(true, MrRobot.ANIM.mrrobot_climb.name(), false),
    CLIMBING_DOWN(true, MrRobot.ANIM.mrrobot_climb.name(), false),
    SLIDING_RIGHT(true, MrRobot.ANIM.mrrobot_stand_right.name(), true),
    SLIDING_LEFT(false, MrRobot.ANIM.mrrobot_stand_left.name(), true),
    DYING(true, MrRobot.ANIM.mrrobot_dies.name(), true, true),
    FALL_RIGHT(true, MrRobot.ANIM.mrrobot_jump_right.name(), true),
    FALL_LEFT(false, MrRobot.ANIM.mrrobot_jump_left.name(), true),
    DROP_RIGHT(true, MrRobot.ANIM.mrrobot_fall.name(), true),
    DROP_LEFT(false, MrRobot.ANIM.mrrobot_fall.name(), true),
    STANDING_ON_LADDER(true, MrRobot.ANIM.mrrobot_stand_on_ladder.name(), false),
    RISING_RIGHT(true, MrRobot.ANIM.mrrobot_stand_right.name(), true),
    RISING_LEFT(false, MrRobot.ANIM.mrrobot_stand_left.name(), true),
    STANDING_RIGHT(true, MrRobot.ANIM.mrrobot_stand_right.name(), false),
    STANDING_LEFT(false, MrRobot.ANIM.mrrobot_stand_left.name(), false),
    TELEPORTING(false, MrRobot.ANIM.mrrobot_teleport.name(), true),
    WALKING_RIGHT(true, MrRobot.ANIM.mrrobot_walk_right.name(), false),
    WALKING_LEFT(false, MrRobot.ANIM.mrrobot_walk_left.name(), false);

    private boolean isFacingRight = true;
    private String animationName = null;
    private MrRobotState reverse;
    private boolean dying = false;

    /** During some states, all input is completely ignored. */
    private boolean isInputBlocked = false;

    /**
     * Creates a state instance.
     *
     * @param isFacingRight True if that state means Mr. Robot is looking to the right side.
     * @param animationName The name of the animation to use for that state (must not be null).
     * @param isInputBlocked True if this state should ignore all player input.
     */
    MrRobotState(boolean isFacingRight, String animationName, boolean isInputBlocked) {
        this.isFacingRight = isFacingRight;
        if(animationName == null) {
            throw new IllegalArgumentException("Animation name must not be null!");
        }
        this.animationName = animationName;
        this.isInputBlocked = isInputBlocked;
    }

    /**
     * Creates a state instance.
     *
     * @param isFacingRight True if that state means Mr. Robot is looking to the right side.
     * @param animationName The name of the animation to use for that state (must not be null).
     * @param isInputBlocked True if this state should ignore all player input.
     */
    MrRobotState(boolean isFacingRight, String animationName, boolean isInputBlocked, boolean isDying) {
        this(isFacingRight, animationName, isInputBlocked);
        this.dying = isDying;
    }

    public boolean isDying() {
        return this.dying;
    }

    /**
     * @return True if input must be ignored during this state.
     */
    public boolean isInputBlocked() {
        return this.isInputBlocked;
    }

    /**
     * Allows to change from one state to the other, whereas the final new state will
     * automatically be adjusted to be the one that faces the same direction as the old one.
     * So, when the old state is WALKING_LEFT and the new state is DROP_RIGHT, the final state
     * will actually be DROP_LEFT, in order to match the same direction as the old state.
     *
     * @param oldState The old (current) state from which to switch to a new one.
     * @param targetState The right-facing version of the new state to set Mr. Robot to.
     * @return The resulting state which is either the given target state, or its left-facing version.
     */
    public static MrRobotState changeFrom(MrRobotState oldState, MrRobotState targetState) {
        MrRobotState result = targetState;
        if(!(oldState.isFacingRight() && result.isFacingRight())) {
            result = result.getReverse();
        }
        return result;
    }

    /**
     * Allows to get the state which represents the same as the current one,
     * but with Mr. Robot facing the opposite direction.
     *
     * @return The reversed state (can be the same).
     */
    public MrRobotState getReverse() {
        if(reverse == null) {
            throw new IllegalStateException("No reverse state defined for: " + name());
        }
        return reverse;
    }

    @Override
    public String toString() {
        return "[state:" + name() + "/anim:" + getAnimationName() + "]";
    }

    public void setReverse(MrRobotState state) {
        reverse = state;
    }

    public String getAnimationName() { return this.animationName; }

    public boolean isFacingRight() {
        return this.isFacingRight;
    }
}
