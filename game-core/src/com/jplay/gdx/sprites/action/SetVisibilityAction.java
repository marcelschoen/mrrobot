package com.jplay.gdx.sprites.action;

public class SetVisibilityAction extends Action {

    private boolean visible = false;

    public void setVisibility(boolean visible, float duration) {
        this.visible = visible;
        setDuration(duration);
    }

    @Override
    public void doStart() {
        System.out.println(">>> Start visibility action / set visible to " + visible);
        super.sprite.setVisible(visible);
    }

    @Override
    protected void execute(float delta) {
        // do nothing
    }
}
