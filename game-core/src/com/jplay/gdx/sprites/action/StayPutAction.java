package com.jplay.gdx.sprites.action;

public class StayPutAction extends Action {

    private String animationName = null;

    public void stayPut(String animationName, float duration) {
        this.animationName = animationName;
        setDuration(duration);
    }

    public void stayPut(float duration) {
        setDuration(duration);
    }

    @Override
    public void doStart() {
        if(animationName != null) {
            super.sprite.showAnimation(animationName);
        }
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
