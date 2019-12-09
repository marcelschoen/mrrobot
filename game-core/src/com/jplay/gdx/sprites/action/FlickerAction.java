package com.jplay.gdx.sprites.action;

import com.badlogic.gdx.graphics.Color;
import com.jplay.gdx.DebugOutput;

public class FlickerAction extends Action {

    private Color color = null;

    public void flickerBackground(Color color) {
        this.color = color;
    }

    @Override
    public void doStart() {
        DebugOutput.flicker(color);
    }

    @Override
    protected void execute(float delta) {
        // do nothing
    }

    @Override
    protected boolean isDone() {
        return true;
    }
}
