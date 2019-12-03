package com.jplay.gdx.sprites.action;

import com.jplay.gdx.sprites.AnimatedSprite;

public abstract class Action {

    protected AnimatedSprite sprite = null;

    protected float executionTimer = -1f;
    protected float executionDuration = -1f;

    private boolean running = false;
    private boolean completed = false;

    protected Action precedingAction = null;
    protected Action followUpAction = null;
    private ActionListener listener;

    /**
     * Starts the action, with the given sprite and otpional listener.
     *
     * @param sprite The sprite for which to execute the action.
     * @param listener The listener for action events (can be null).
     */
    public void start(AnimatedSprite sprite, ActionListener listener) {
        this.listener = listener;
        this.running = true;
        this.completed = false;
        this.sprite = sprite;
        doStart();
    }

    /**
     * Performs initializations required to start the action, often based on properties of the sprite.
     */
    abstract void doStart();

    /**
     * Checks if this action, and all follow-up actions (if there are any) are still running.
     *
     * @return True if this and all follow-up actions are still running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if this action, and all follow-up actions (if there are any) are completed.
     *
     * @return True if this and all follow-up actions are completed.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Continues execution of this action.
     *
     * @param delta The time in seconds since last screen refresh.
     */
    public void executeAction(float delta) {
        execute(delta);
        executionTimer -= delta;
        if(isDone()) {
            completed();
        }
    }

    /**
     * Returns either this action itself, or the first action of the chain
     * if this action was in a set of chained actions. So, if a chain of actions
     * was created using "ActionBuilder.build()", this method will return the
     * same action reference returned there.
     *
     * @return The first action.
     */
    public Action getFirstActionInChain() {
        Action result = this;
        while(result.precedingAction != null) {
            result = result.precedingAction;
        }
        return result;
    }

    public Action runFollowUpAction() {
        if(followUpAction != null) {
            followUpAction.start(sprite, listener);
        }
        return followUpAction;
    }

    /**
     * Must indicate if this action is done, e.g. has
     * completed its work.
     *
     * @return True if the action is done.
     */
    protected abstract boolean isDone();

    /**
     * Must perform the actual work of the action.
     *
     * @param delta The time in seconds since last screen refresh.
     */
    protected abstract void execute(float delta);

    /**
     * Sets running/completion flag and notifies the listener.
     */
    private void completed() {
        running = false;
        completed = true;
        if(followUpAction == null && listener != null) {
            listener.completed(this);
        }
    }
}
