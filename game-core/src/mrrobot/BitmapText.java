package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BitmapText extends Actor {

    BitmapFont font;
    String text;

    public BitmapText(BitmapFont font, String text){
        this.font = font;
        this.text = text;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        font.draw(batch, text, 60, 60);
    }
}
