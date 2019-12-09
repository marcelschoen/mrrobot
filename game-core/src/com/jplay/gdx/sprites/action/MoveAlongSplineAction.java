package com.jplay.gdx.sprites.action;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;

/**
 * Moves a sprite along a spline.
 *
 * Package-restricted access because the functionality of this class
 * is to be accessed through "ActionBuilder".
 *
 * @author Marcel Schoen
 */
public class MoveAlongSplineAction extends Action {

    private boolean moveSpline = false;
    private int splineFidelity = 100; //increase splineFidelity for more splineFidelity to the spline
    private Vector2[] points = new Vector2[splineFidelity];
    float splineMoveSpeed = 0.15f;
    float current = 0;

    public void moveAlongSpline(Vector2[] dataSet) {
        CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(dataSet, true);
        for(int i = 0; i < splineFidelity; ++i) {
            points[i] = new Vector2();
            myCatmull.valueAt(points[i], ((float)i)/((float) splineFidelity -1));
        }
    }

    @Override
    public void doStart() {
        moveSpline = true;
    }

    @Override
    protected void execute(float delta) {
        if(moveSpline) {
            current += delta * splineMoveSpeed;
            if (current >= 1) {
                current -= 1;
            }
            float place = current * splineFidelity;
            Vector2 first = points[(int) place];
            Vector2 second;
            if (((int) place + 1) >= splineFidelity) {
                moveSpline = false;
            } else {
                second = points[(int) place + 1];
                float t = place - ((int) place); //the decimal part of place
                super.sprite.setX(first.x + (second.x - first.x) * t);
                super.sprite.setY(first.y + (second.y - first.y) * t);
            }
        }
    }

    @Override
    protected boolean isDone() {
        return !moveSpline;
    }
}
