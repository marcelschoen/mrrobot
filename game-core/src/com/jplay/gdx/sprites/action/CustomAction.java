package com.jplay.gdx.sprites.action;

public class CustomAction extends Action {

    private SpriteAction customAction = null;

    public void setCustomAction(SpriteAction customAction) {
        this.customAction = customAction;
    }

    @Override
    public void doStart() {
        this.customAction.start(super.sprite);
    }

    @Override
    protected boolean isDone() {
        return this.customAction.isFinished(super.sprite);
    }

    @Override
    protected void execute(float delta) {
        this.customAction.execute(super.sprite, delta);
    }
}
