package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;
import java.util.List;

/**
 * On-screen menu with multiple options, selectable either by
 * keyboard (cursor keys, ENTER) or by touch.
 *
 * @author Marcel Schoen
 */
public class GameMenu {

    private static List<MenuOption> options = new LinkedList<>();

    public static void addOption(BitmapFont font, CharSequence label, float x, float y, boolean selected) {
        MenuOption option = new MenuOption(font, label, x, y);
        option.setCurrentlySelected(selected);
        MenuOption previousOption = null;
        if(!options.isEmpty()) {
            previousOption = options.get(options.size() - 1);
        }
        options.add(option);
        if(previousOption != null) {
            option.setPreviousOption(previousOption);
            previousOption.setNextOption(option);
        }
        if(options.size() > 1) {
            options.get(0).setPreviousOption(option);
            option.setNextOption(options.get(0));
        }
    }

    public static List<MenuOption> getOptions() {
        return options;
    }

    public static void setSelectedOption(MenuOption selectedOption) {
        System.out.println(">> Select option: " + selectedOption.getLabel());
        for(MenuOption option : options) {
            System.out.println("De-selection option: " + option.getLabel());
            option.setCurrentlySelected(false);
        }
        System.out.println("select option: " + selectedOption.getLabel());
        selectedOption.setCurrentlySelected(true);
    }

    public void render(SpriteBatch batch, float arg0) {
        for(MenuOption option : options) {
            option.draw(batch);
        }
    }

    public void performLogic(TitleScreen titleScreen, float delta) {
        for(MenuOption option : options) {
            option.performLogic();
        }

        /*
        if (GameInput.isUpJustPressed()) {
            setSelectedOption(getCurrentlySelected().getPreviousOption());
        }
        if (GameInput.isDownJustPressed()) {
            setSelectedOption(getCurrentlySelected().getNextOption());
        }
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

    public static MenuOption getCurrentlySelected() {
        for(MenuOption option : options) {
            if(option.isCurrentlySelected()) {
                return option;
            }
        }
        throw new IllegalStateException("No menu option is currently selected!");
    }
}
