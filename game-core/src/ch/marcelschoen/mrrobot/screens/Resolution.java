/*
 * $Header: /stuff/cvs-repos/bombermaniacs-gdx/src/com/jplay/gdx/screens/ScreenUtil.java,v 1.1 2013/02/09 16:05:05 msc Exp $
 *
 * @Copyright: Marcel Schoen, Switzerland, 2013, All Rights Reserved.
 */

package ch.marcelschoen.mrrobot.screens;

import java.awt.Dimension;

/**
 * Resolution (either a physical screen resolution, or
 * the virtual resolution of the game).
 *
 * @author Marcel Schoen
 */
public class Resolution {

    private int width = -1;
    private int height = -1;
    private static Dimension size = null;

    /**
     * Creates a gameResolution holder.
     *
     * @param width The width in number of pixels.
     * @param height The height in number of pixels.
     */
    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Dimension getSize() {
        return size;
    }
}
