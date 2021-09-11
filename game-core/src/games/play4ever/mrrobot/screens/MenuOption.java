package games.play4ever.mrrobot.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import games.play4ever.libgdx.collision.CollisionRectangle;

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

    public boolean isTouched() {
        for(int finger=0; finger<2; finger++) {
            if (Gdx.input.isTouched(finger)) {
////                transformTouchCoordinates(Gdx.input.getX(finger), Gdx.input.getY(finger), camera);
            }
        }
        return false;
    }
}
