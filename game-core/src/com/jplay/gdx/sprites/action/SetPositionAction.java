package com.jplay.gdx.sprites.action;

public class SetPositionAction extends Action {

    private float x = 0;
    private float y = 0;

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void doStart() {
        super.sprite.setPosition(x, y);
    }

    @Override
    protected boolean isDone() {
        return true;
    }

    @Override
    protected void execute(float delta) {
        // nothing to do
    }
}
