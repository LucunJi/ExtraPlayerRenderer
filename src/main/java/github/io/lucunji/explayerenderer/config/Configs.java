package github.io.lucunji.explayerenderer.config;

import fi.dy.masa.malilib.config.options.*;
import github.io.lucunji.explayerenderer.config.wrappers.*;

import static github.io.lucunji.explayerenderer.Main.MOD_ID;

public class Configs {
    public static final ConfigHotkey MENU_OPEN_KEY;
    public static final ConfigBoolean ENABLED;
    public static final ConfigBoolean SPECTATOR_AUTO_SWITCH;
    public static final ConfigString PLAYER_NAME;

    public static final ConfigDouble OFFSET_X;
    public static final ConfigDouble OFFSET_Y;
    public static final ConfigDouble ROTATION_X;
    public static final ConfigDouble ROTATION_Y;
    public static final ConfigDouble ROTATION_Z;
    public static final ConfigDouble SIZE;
    public static final ConfigBoolean MIRRORED;

    public static final ConfigOptionList POSE_OFFSET_METHOD;
    public static final String POSE_OFFSET_METHOD_KEY = "pose_offset_method";
    public static final ConfigDouble SNEAK_OFFSET_Y;
    public static final ConfigDouble SWIM_CRAWL_OFFSET_Y;
    public static final ConfigDouble ELYTRA_OFFSET_Y;

    public static final ConfigDouble PITCH_MIN;
    public static final ConfigDouble PITCH_MAX;
    public static final ConfigDouble PITCH_OFFSET;
    public static final ConfigDouble HEAD_YAW_MIN;
    public static final ConfigDouble HEAD_YAW_MAX;
    public static final ConfigDouble BODY_YAW_MIN;
    public static final ConfigDouble BODY_YAW_MAX;

    public static final ConfigBoolean HURT_FLASH;
    public static final ConfigBoolean SWING_HANDS;
    public static final ConfigDouble LIGHT_DEGREE;
    public static final ConfigBoolean USE_WORLD_LIGHT;
    public static final ConfigInteger WORLD_LIGHT_MIN;

    static {
        MENU_OPEN_KEY = Category.GENERAL.add(new LocalizedConfigHotkey(MOD_ID, "open_menu_key", "F8"));
        ENABLED = Category.GENERAL.add(new LocalizedConfigBoolean(MOD_ID, "enabled", true));
        SPECTATOR_AUTO_SWITCH = Category.GENERAL.add(new LocalizedConfigBoolean(MOD_ID, "spectator_auto_switch", true));
        PLAYER_NAME = Category.GENERAL.add(new LocalizedConfigString(MOD_ID, "player_name", ""));

        OFFSET_X = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "offset_x", 0.12, -0.5, 1.5));
        OFFSET_Y = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "offset_y", 1.5, -0.5, 2.5));
        ROTATION_X = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "rotation_x", 0, -180, 180));
        ROTATION_Y = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "rotation_y", 0, -180, 180));
        ROTATION_Z = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "rotation_z", 0, -180, 180));
        SIZE = Category.GENERAL.add(new LocalizedConfigDouble(MOD_ID, "size", 0.5, 0, 2));
        MIRRORED = Category.GENERAL.add(new LocalizedConfigBoolean(MOD_ID, "mirror", false));

        POSE_OFFSET_METHOD = Category.POSTURES.add(new LocalizedConfigOptionList(MOD_ID, POSE_OFFSET_METHOD_KEY, PoseOffsetMethod.AUTO));
        SNEAK_OFFSET_Y = Category.POSTURES.add(new LocalizedConfigDouble(MOD_ID, "sneaking_y_offset", -30, -100, 100));
        SWIM_CRAWL_OFFSET_Y = Category.POSTURES.add(new LocalizedConfigDouble(MOD_ID, "swim_crawl_y_offset", -120, -300, 300));
        ELYTRA_OFFSET_Y = Category.POSTURES.add(new LocalizedConfigDouble(MOD_ID, "elytra_y_offset", -120, -300, 300));

        PITCH_MIN = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "pitch_min", -20, -180, 180));
        PITCH_MAX = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "pitch_max", 20, -180, 180));
        PITCH_OFFSET = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "pitch_offset", 0, -90, 90));
        HEAD_YAW_MIN = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "head_yaw_min", -15, -180, 180));
        HEAD_YAW_MAX = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "head_yaw_max", -15, -180, 180));
        BODY_YAW_MIN = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "body_yaw_min", 0, -180, 180));
        BODY_YAW_MAX = Category.ROTATIONS.add(new LocalizedConfigDouble(MOD_ID, "body_yaw_max", 0, -180, 180));

        HURT_FLASH = Category.DETAILS.add(new LocalizedConfigBoolean(MOD_ID, "hurt_flash", true));
        SWING_HANDS = Category.DETAILS.add(new LocalizedConfigBoolean(MOD_ID, "swing_hands", true));
        LIGHT_DEGREE = Category.DETAILS.add(new LocalizedConfigDouble(MOD_ID, "light_degree", 0, -180, 180));
        USE_WORLD_LIGHT = Category.DETAILS.add(new LocalizedConfigBoolean(MOD_ID, "use_world_light", true));
        WORLD_LIGHT_MIN = Category.DETAILS.add(new LocalizedConfigInteger(MOD_ID, "world_light_min", 2, 0, 15));
    }

}
