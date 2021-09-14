package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.collision.CollisionRectangle;
import games.play4ever.mrrobot.GameInput;

/**
 * Encapsulates the text and touch rectangle handling
 * of one menu option entry.
 *
 * @author Marcel Schoen
 */
public class MenuOption {

    private CharSequence label = "";
    private float xPos = 0;
    private float yPos = 0;
    private CollisionRectangle collisionRectangle;
    private BitmapFont font;
    private boolean currentlySelected = false;

    private MenuOption nextOption = null;
    private MenuOption previousOption = null;

    private static GlyphLayout layout = new GlyphLayout();

    public MenuOption(BitmapFont font, CharSequence label, float xPos, float yPos) {
        this.font = font;
        this.label = label;
        this.xPos = xPos;
        this.yPos = yPos;
        if(font != null) {
            layout.setText(font, label);
            collisionRectangle = new CollisionRectangle(xPos, yPos, layout.width, layout.height);
        } else {
            throw new IllegalStateException("Bitmap font not loaded!");
        }
    }

    public CharSequence getLabel() {
        return label;
    }

    public MenuOption getNextOption() {
        return nextOption;
    }

    public void setNextOption(MenuOption nextOption) {
        this.nextOption = nextOption;
    }

    public MenuOption getPreviousOption() {
        return previousOption;
    }

    public void setPreviousOption(MenuOption previousOption) {
        this.previousOption = previousOption;
    }

    public void draw(SpriteBatch batch) {
        font.draw(batch, label, xPos, yPos);
        if(currentlySelected) {
            font.draw(batch, ">", xPos - 16, yPos);
        }
    }

    public void setCurrentlySelected(boolean currentlySelected) {
        this.currentlySelected = currentlySelected;
    }

    public boolean isCurrentlySelected() {
        return currentlySelected;
    }

    public void performLogic() {
        if (GameInput.isUpJustPressed() && isCurrentlySelected()) {
            GameMenu.setSelectedOption(getPreviousOption());
        }
        if (GameInput.isDownJustPressed() && isCurrentlySelected()) {
            GameMenu.setSelectedOption(getNextOption());
        }
        for(int finger=0; finger<2; finger++) {
            if (Gdx.input.isTouched(finger) && !isCurrentlySelected()) {
                if(collisionRectangle.surroundsPoint(Gdx.input.getX(finger), Gdx.input.getY(finger))) {
                    GameMenu.setSelectedOption(this);
                }
            }
        }
    }
}
