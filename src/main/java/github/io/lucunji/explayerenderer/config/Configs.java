package github.io.lucunji.explayerenderer.config;

import fi.dy.masa.malilib.config.options.*;

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
    public static final ConfigBoolean MIRROR;

    public static final ConfigDouble PITCH_MIN;
    public static final ConfigDouble PITCH_MAX;
    public static final ConfigDouble PITCH_OFFSET;
    public static final ConfigDouble HEAD_YAW_MIN;
    public static final ConfigDouble HEAD_YAW_MAX;
    public static final ConfigDouble BODY_YAW_MIN;
    public static final ConfigDouble BODY_YAW_MAX;
    public static final ConfigDouble SNEAKING_OFFSET_Y;
    public static final ConfigDouble SWIM_OFFSET_Y;
    public static final ConfigDouble ELYTRA_OFFSET_Y;

    public static final ConfigBoolean HURT_FLASH;
    public static final ConfigBoolean SWING_HANDS;
    public static final ConfigDouble LIGHT_DEGREE;
    public static final ConfigBoolean USE_WORLD_LIGHT;
    public static final ConfigInteger WORLD_LIGHT_MIN;

    static {
        MENU_OPEN_KEY = Category.GENERAL.add(new ConfigHotkey("openMenuKey", "F8", "explayerenderer.gui.settings.open_key"));
        ENABLED = Category.GENERAL.add(new ConfigBoolean("enabled", true, "explayerenderer.gui.settings.enabled.desc"));
        SPECTATOR_AUTO_SWITCH = Category.GENERAL.add(new ConfigBoolean("spectatorAutoSwitch", true, "explayerenderer.gui.settings.spectator_auto_switch.desc"));
        PLAYER_NAME = Category.GENERAL.add(new ConfigString("playerName", "", "explayerenderer.gui.settings.player_name.desc"));

        OFFSET_X = Category.GENERAL.add(new ConfigDouble("offsetX", 0.12, -0.5, 1.5, "explayerenderer.gui.settings.offset_x.desc"));
        OFFSET_Y = Category.GENERAL.add(new ConfigDouble("offsetY", 1.5, -0.5, 2.5, "explayerenderer.gui.settings.offset_y.desc"));
        ROTATION_X = Category.GENERAL.add(new ConfigDouble("rotationX", 0, -180, 180, "explayerenderer.gui.settings.rotation_x.desc"));
        ROTATION_Y = Category.GENERAL.add(new ConfigDouble("rotationY", 0, -180, 180, "explayerenderer.gui.settings.rotation_y.desc"));
        ROTATION_Z = Category.GENERAL.add(new ConfigDouble("rotationZ", 0, -180, 180, "explayerenderer.gui.settings.rotation_z.desc"));
        SIZE = Category.GENERAL.add(new ConfigDouble("size", 0.5, 0, 2, "explayerenderer.gui.settings.size.desc"));
        MIRROR = Category.GENERAL.add(new ConfigBoolean("mirror", false, "explayerenderer.gui.settings.mirror.desc"));

        PITCH_MIN = Category.DETAILS.add(new ConfigDouble("pitchMin", -20, -180, 180, "explayerenderer.gui.settings.pitch_min.desc"));
        PITCH_MAX = Category.DETAILS.add(new ConfigDouble("pitchMax", 20, -180, 180, "explayerenderer.gui.settings.pitch_max.desc"));
        PITCH_OFFSET = Category.DETAILS.add(new ConfigDouble("pitchOffset", 0, -90, 90, "explayerenderer.gui.settings.pitch_offset.desc"));
        HEAD_YAW_MIN = Category.DETAILS.add(new ConfigDouble("headYawMin", -15, -180, 180, "explayerenderer.gui.settings.head_yaw_min.desc"));
        HEAD_YAW_MAX = Category.DETAILS.add(new ConfigDouble("headYawMax", -15, -180, 180, "explayerenderer.gui.settings.head_yaw_max.desc"));
        BODY_YAW_MIN = Category.DETAILS.add(new ConfigDouble("bodyYawMin", 0, -180, 180, "explayerenderer.gui.settings.body_yaw_min.desc"));
        BODY_YAW_MAX = Category.DETAILS.add(new ConfigDouble("bodyYawMax", 0, -180, 180, "explayerenderer.gui.settings.body_yaw_max.desc"));
        SNEAKING_OFFSET_Y = Category.DETAILS.add(new ConfigDouble("sneakingYOffset", -30, -100, 100, "explayerenderer.gui.settings.sneaking_y_offset.desc"));
        SWIM_OFFSET_Y = Category.DETAILS.add(new ConfigDouble("swimYOffset", -120, -300, 300, "explayerenderer.gui.settings.swim_y_offset.desc"));
        ELYTRA_OFFSET_Y = Category.DETAILS.add(new ConfigDouble("elytraYOffset", -120, -300, 300, "explayerenderer.gui.settings.elytra_y_offset.desc"));

        HURT_FLASH = Category.DETAILS.add(new ConfigBoolean("hurtFlash", true, "explayerenderer.gui.settings.hurt_flash.desc"));
        SWING_HANDS = Category.DETAILS.add(new ConfigBoolean("swingHands", true, "explayerenderer.gui.settings.swing_hands.desc"));
        LIGHT_DEGREE = Category.DETAILS.add(new ConfigDouble("lightDegree", 0, -180, 180, "explayerenderer.gui.settings.light_degree.desc"));
        USE_WORLD_LIGHT = Category.DETAILS.add(new ConfigBoolean("useWorldLight", true, "explayerenderer.gui.settings.use_world_light.desc"));
        WORLD_LIGHT_MIN = Category.DETAILS.add(new ConfigInteger("worldLightMin", 2, 0, 15, "explayerenderer.gui.settings.world_light_min.desc"));
    }

}
