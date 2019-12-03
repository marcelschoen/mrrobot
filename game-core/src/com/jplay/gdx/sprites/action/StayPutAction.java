package com.jplay.gdx.sprites.action;

public class StayPutAction extends Action {

    private float duration = 0;
    private String animationName = null;

    public void stayPut(String animationName, float duration) {
        this.animationName = animationName;
        this.duration = duration;
    }

    public void stayPut(float duration) {
        this.duration = duration;
    }

    @Override
    void doStart() {
        if(animationName != null) {
            super.sprite.showAnimation(animationName);
        }
    }

    @Override
    protected boolean isDone() {
        return duration <= 0f;
    }

    @Override
    protected void execute(float delta) {
        duration -= delta;
    }
}
