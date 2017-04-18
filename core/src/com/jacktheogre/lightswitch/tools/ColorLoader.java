package com.jacktheogre.lightswitch.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.jacktheogre.lightswitch.ai.LevelManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luna on 13.04.17.
 */

public class ColorLoader {
    public static Color MAINMENU_SCREEN_BACKGROUND;
    public static Color LEVEL_CHOOSING_SCREEN_BACKGROUND;
    public static Color LEVEL_FRAME_UNLOCKED_BACKGROUND;
    public static Color LEVEL_FRAME_LOCKED_BACKGROUND;
    public static Color LEVEL_FRAME_STROKE;
    public static Color LEVEL_LABEL_COLOR;
    public static Color RESOURSES_LABELS_COLOR;
    public static Color GENERATING_SCREEN_BACKGROUND;
    public static Color RESOURSES_LEFT_LABELS_COLOR;
    public static Color PLAYING_SCREEN_BACKGROUND;
    public static Color TIMER_LABEL_COLOR;
    public static Color AMBIENT_LIGHT_BOY_COLOR;
    public static Color ACTOR_LIGHT_COLOR;
    public static Color GLOBAL_LIGHTS_COLOR;
    public static Color GAMEOVER_SCREEN_BACKGROUND;
    public static Color GAMEOVER_LABELS_COLOR;

    public static Map<String, Color> colorMap = new HashMap();

    public static void load() {
        FileHandle colors = Gdx.files.internal("data/colors");
        String[] lines = colors.readString().split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String[] columns = lines[i].split(" ");
            if(columns.length < 2)
                continue;
            String colorName = columns[0];
            String colorString = columns[1];
            float alpha = 1f;
            if(columns.length > 2) {
                try {
                    alpha = Float.parseFloat(columns[2]);
                } catch (Exception e) {
                    Gdx.app.error("ColorLoader", "Wrong alpha value.", e);
                    Gdx.app.exit();
                    return;
                }
            }
            colorMap.put(colorName, parseColor(colorString, alpha));
        }
    }

    private static Color parseColor(String s, float alpha) {
        char[] chars = s.toCharArray();
        int index = 0;
        Color color = new Color();
        if(chars[0] == '#') index++;
        String r = s.substring(index, index + 2);
        color.r = Integer.parseInt(r, 16) / 255f;
        index += 2;
        String g = s.substring(index, index + 2);
        color.g = Integer.parseInt(g, 16) / 255f;
        index += 2;
        String b = s.substring(index, index + 2);
        color.b = Integer.parseInt(b, 16) / 255f;
        color.a = alpha;
        return color;
    }
}
