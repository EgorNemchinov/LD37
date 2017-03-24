package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by luna on 23.03.17.
 */

public class DrawingAssistant {

    private static float lineTime = 0;

    /**
     * Draws a dotted line between to points (x1,y1) and (x2,y2).
     * @param shapeRenderer
     * @param dotDist (distance between dots)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public static void drawDottedLine(ShapeRenderer shapeRenderer, int dotDist, float x1, float y1, float x2, float y2, boolean moved) {
        Color previousColor = shapeRenderer.getColor();
        Color lineColor = Color.WHITE;
        shapeRenderer.setColor(lineColor);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        Vector2 vec2 = new Vector2(x2, y2).sub(new Vector2(x1, y1));
        float length = vec2.len();
        for(int i = moved ? dotDist/2 : 0; i < length; i += dotDist) {
            vec2.clamp(length - i, length - i);
            shapeRenderer.point(x1 + vec2.x, y1 + vec2.y, -100);
        }

        shapeRenderer.end();
        shapeRenderer.setColor(previousColor);
    }

    /**
     * Draws a dotted line between to points (x1,y1) and (x2,y2).
     * @param shapeRenderer
     * @param dotDist (distance between dots)
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param timeInterval (how often should it blink)
     * @param runTime
     */
    public static void drawPulsingDottedLine(ShapeRenderer shapeRenderer, int dotDist,
                                             float x1, float y1, float x2, float y2, float timeInterval, float runTime) {
        lineTime = runTime % (2*timeInterval);
        if(lineTime < timeInterval) {
            drawDottedLine(shapeRenderer, dotDist, x1, y1, x2, y2, false);
        } else {
            drawDottedLine(shapeRenderer, dotDist, x1, y1, x2, y2, true);
        }
    }
}
