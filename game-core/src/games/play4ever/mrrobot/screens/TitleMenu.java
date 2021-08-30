package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.Assets;
import games.play4ever.mrrobot.GameDataStore;
import games.play4ever.mrrobot.MrRobotAssets;

public class TitleMenu {

    private int currentOption = 0;

    public static final CharSequence[] optionLabels = new CharSequence[] {
            "START",
            "CONTINUE",
            "LEVEL",
            "EXIT GAME"
    };

    private static final int LEVEL_OPTION = 2;

    public void render(SpriteBatch batch, float arg0) {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        if(font != null) {
            float y = 80;
            float x = 190;
            for(int i = 0; i < optionLabels.length; i++) {

                font.draw(batch, optionLabels[i], x, y);

                if(i == currentOption) {
                    font.draw(batch, ">", x - 16, y);
                }
                if(i == LEVEL_OPTION) {
                    font.draw(batch, "" + GameDataStore.getLastUnlockedLevel(), x + 50, y);
                }
                y -= 20;
            }
        }
    }
}
