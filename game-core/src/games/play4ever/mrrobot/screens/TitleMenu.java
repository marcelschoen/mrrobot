package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import games.play4ever.libgdx.Assets;
import games.play4ever.libgdx.screens.AbstractBaseScreen;
import games.play4ever.libgdx.screens.TransitionScreen;
import games.play4ever.libgdx.screens.transitions.ScreenTransitions;
import games.play4ever.mrrobot.MrRobotAssets;
import games.play4ever.mrrobot.MrRobotGame;

public class TitleMenu extends GameMenu {

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

    public static void initialize() {
        BitmapFont font = Assets.instance().getFont(MrRobotAssets.FONT_ID.LOADING);
        float y = 80;
        float x = 190;
        for(int i = 0; i < optionLabels.length; i++) {
            addOption(font, optionLabels[i], x, y, i == 0);
            y -= 20;
        }

        for(MenuOption option : getOptions()) {
            System.out.println("=========> OPTION: " + option.getLabel() + " <============");
            System.out.println("--> previous: " + (option.getPreviousOption() == null ? "" : option.getPreviousOption().getLabel()));
            System.out.println("--> next: " + (option.getNextOption() == null ? "" : option.getNextOption().getLabel()));
        }
    }

    public CharSequence getCurrentOption() {
        return getCurrentlySelected().getLabel();
    }

    public void performLogic(TitleScreen titleScreen, float delta) {
        // handle basic option selection (up/down etc.)
        super.performLogic(titleScreen, delta);
/*
        if (GameInput.isRightJustPressed() && getCurrentOption() == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            selectedLevel++;
            if (selectedLevel > GameDataStore.getLastUnlockedLevel()) {
                selectedLevel = 0;
            }
            setSelectedLevel(selectedLevel);
        }
        if (GameInput.isLeftJustPressed() && getCurrentOption() == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            selectedLevel--;
            if (selectedLevel < 0) {
                selectedLevel = GameDataStore.getLastUnlockedLevel();
            }
            setSelectedLevel(selectedLevel);
        }

        MenuOption activatedOption = getActivatedOption();
        if(activatedOption != null) {
            if (activatedOption == TitleMenu.optionLabels[TitleMenu.START_OPTION]) {
                beginGame(titleScreen, getSelectedLevel());
            } else if (activatedOption == TitleMenu.optionLabels[TitleMenu.CONTINUE_OPTION]) {
                beginGame(titleScreen, GameDataStore.getLastUnlockedLevel());
            } else if (activatedOption == TitleMenu.optionLabels[TitleMenu.LEVEL_OPTION]) {
            } else if (activatedOption == TitleMenu.optionLabels[TitleMenu.EXIT_OPTION]) {
                Gdx.app.exit();
            }
        }

 */
    }
/*
    private static MenuOption getActivatedOption() {
        for(MenuOption option : options) {
            if(option.isTouched()
                    || (GameInput.isButtonOkPressed() && option.isCurrentlySelected())) {
                return option;
            }
        }
        return null;
    }

 */

    private void beginGame(AbstractBaseScreen titleScreen, int level) {
        MrRobotGame.instance().playScreen.startGame(level);
        MrRobotAssets.playMenuMusic();
        TransitionScreen.setupAndShowTransition(
                MrRobotGame.instance(),
                2f, ScreenTransitions.ALPHA_FADE.getTransition(),
                titleScreen, MrRobotGame.instance().playScreen);
    }
/*
    public void render(SpriteBatch batch, float arg0) {
        for(MenuOption option : options) {
            option.draw(batch);
        }
    }

 */
}
