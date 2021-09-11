package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.libgdx.screens.TransitionScreen;
import games.play4ever.libgdx.screens.transitions.ScreenTransitions;
import games.play4ever.mrrobot.GameDataStore;
import games.play4ever.mrrobot.GameInput;
import games.play4ever.mrrobot.MrRobotAssets;
import games.play4ever.mrrobot.MrRobotGame;

public class TitleMenu {

    private int currentOption = 0;
    private int selectedLevel = 0;

    public static final CharSequence[] optionLabels = new CharSequence[] {
            "START",
            "CONTINUE",
            "LEVEL",
            "EXIT GAME"
    };

    public static final int START_OPTION = 0;
    public static final int CONTINUE_OPTION = 1;
    public static final int LEVEL_OPTION = 2;
    public static final int EXIT_OPTION = 3;

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public int getSelectedLevel() {
        return this.selectedLevel;
    }

    public void increaseOption() {
        currentOption++;
        if(currentOption == optionLabels.length) {
            currentOption = 0;
        }
    }

    public void decreaseOption() {
        currentOption--;
        if(currentOption < 0) {
            currentOption = optionLabels.length - 1;
        }
    }

    public CharSequence getCurrentOption() {
        return optionLabels[currentOption];
    }

    public void performLogic(TitleScreen titleScreen, float delta) {
        if(GameInput.isUpJustPressed()) {
            decreaseOption();
        }
        if(GameInput.isDownJustPressed()) {
            increaseOption();
        }
        if(GameInput.isRightJustPressed() && getCurrentOption() == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            selectedLevel ++;
            if(selectedLevel > GameDataStore.getLastUnlockedLevel()) {
                selectedLevel = 0;
            }
            setSelectedLevel(selectedLevel);
        }
        if(GameInput.isLeftJustPressed() && getCurrentOption() == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            selectedLevel --;
            if(selectedLevel < 0) {
                selectedLevel = GameDataStore.getLastUnlockedLevel();
            }
            setSelectedLevel(selectedLevel);
        }
        if(GameInput.isButtonOkPressed()) {
            if(getCurrentOption() == TitleMenu.optionLabels[TitleMenu.START_OPTION]) {
                beginGame(titleScreen, getSelectedLevel());
            } if(getCurrentOption() == TitleMenu.optionLabels[TitleMenu.CONTINUE_OPTION]) {
                beginGame(titleScreen, GameDataStore.getLastUnlockedLevel());
            } if(getCurrentOption() == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            } if(getCurrentOption() == TitleMenu.optionLabels[TitleMenu.EXIT_OPTION]) {
                Gdx.app.exit();
            }
        }
    }

    private void beginGame(AbstractBaseScreen titleScreen, int level) {
        MrRobotGame.instance().playScreen.startGame(level);
        MrRobotAssets.playMenuMusic();
        TransitionScreen.setupAndShowTransition(
                MrRobotGame.instance(),
                2f, ScreenTransitions.ALPHA_FADE.getTransition(),
                titleScreen, MrRobotGame.instance().playScreen);
    }

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
                    font.draw(batch, "" + (selectedLevel + 1), x + 50, y);
                }
                y -= 20;
            }
        }
    }
}
