package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.Game;

public class MrRobot extends Game {
    @Override
    public void create() {
        setScreen(new MainScreen(this));
    }
}
