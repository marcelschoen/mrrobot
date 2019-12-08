package com.jplay.gdx.sprites.action;

public class SetAnimationAction extends Action {

    private String animationName = null;
    private boolean done = false;

    public void setAnimation(String animationName) {
        this.animationName = animationName;
    }

    @Override
    public void doStart() {
        super.sprite.showAnimation(animationName);
        done = true;
    }

    @Override
    protected boolean isDone() {
        return done;
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
