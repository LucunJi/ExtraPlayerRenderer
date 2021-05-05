package github.io.lucunji.explayerenderer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigString;

import java.util.ArrayList;
import java.util.List;

public class Configs {
    public static final ConfigHotkey MENU_OPEN_KEY;
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
    public static final ConfigDouble ELYTRA_OFFSET_Y;

    public static final ConfigBoolean HURT_FLASH;
    public static final ConfigBoolean SWING_HANDS;
//    public static final ConfigDouble LIGHT_DEGREE;
    public static final ConfigBoolean USE_WORLD_LIGHT;

    static {
        MENU_OPEN_KEY = Category.PARAMETERS.add(new ConfigHotkey("openMenuKey", "F8", "explayerenderer.gui.settings.open_key"));
        SPECTATOR_AUTO_SWITCH = Category.PARAMETERS.add(new ConfigBoolean("spectatorAutoSwitch", true, "explayerenderer.gui.settings.spectator_auto_switch.desc"));
        PLAYER_NAME = Category.PARAMETERS.add(new ConfigString("playerName", "", "explayerenderer.gui.settings.player_name.desc"));

        OFFSET_X = Category.PARAMETERS.add(new ConfigDouble("offsetX", 0.05, -0.5, 1.5, "explayerenderer.gui.settings.offset_x.desc"));
        OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("offsetY", 1.5, -0.5, 2.5, "explayerenderer.gui.settings.offset_y.desc"));
        ROTATION_X = Category.PARAMETERS.add(new ConfigDouble("rotationX", 0, -180, 180, "explayerenderer.gui.settings.rotation_x.desc"));
        ROTATION_Y = Category.PARAMETERS.add(new ConfigDouble("rotationY", 0, -180, 180, "explayerenderer.gui.settings.rotation_y.desc"));
        ROTATION_Z = Category.PARAMETERS.add(new ConfigDouble("rotationZ", 0, -180, 180, "explayerenderer.gui.settings.rotation_z.desc"));
        SIZE = Category.PARAMETERS.add(new ConfigDouble("size", 0.5, 0, 2, "explayerenderer.gui.settings.size.desc"));
        MIRROR = Category.PARAMETERS.add(new ConfigBoolean("mirror", false, "explayerenderer.gui.settings.mirror.desc"));
        PITCH_MIN = Category.PARAMETERS.add(new ConfigDouble("pitchMin", -20, -180, 180, "explayerenderer.gui.settings.pitch_min.desc"));
        PITCH_MAX = Category.PARAMETERS.add(new ConfigDouble("pitchMax", 20, -180, 180, "explayerenderer.gui.settings.pitch_max.desc"));
        PITCH_OFFSET = Category.PARAMETERS.add(new ConfigDouble("pitchOffset", 0, -90, 90, "explayerenderer.gui.settings.pitch_offset.desc"));
        HEAD_YAW_MIN = Category.PARAMETERS.add(new ConfigDouble("headYawMin", -15, -180, 180, "explayerenderer.gui.settings.head_yaw_min.desc"));
        HEAD_YAW_MAX = Category.PARAMETERS.add(new ConfigDouble("headYawMax", -15, -180, 180, "explayerenderer.gui.settings.head_yaw_max.desc"));
        BODY_YAW_MIN = Category.PARAMETERS.add(new ConfigDouble("bodyYawMin", 0, -180, 180, "explayerenderer.gui.settings.body_yaw_min.desc"));
        BODY_YAW_MAX = Category.PARAMETERS.add(new ConfigDouble("bodyYawMax", 0, -180, 180, "explayerenderer.gui.settings.body_yaw_max.desc"));
        SNEAKING_OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("sneakingYOffset", -30, -100, 100, "explayerenderer.gui.settings.sneaking_y_offset.desc"));
        ELYTRA_OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("elytraYOffset", -120, -300, 300, "explayerenderer.gui.settings.elytra_y_offset.desc"));

        HURT_FLASH = Category.DETAILS.add(new ConfigBoolean("hurtFlash", true, "explayerenderer.gui.settings.hurt_flash.desc"));
        SWING_HANDS = Category.DETAILS.add(new ConfigBoolean("swingHands", true, "explayerenderer.gui.settings.swing_hands.desc"));
//        LIGHT_DEGREE = Category.DETAILS.add(new ConfigDouble("lightDegree", 0, -180, 180, "explayerenderer.gui.settings.light_degree.desc"));
        USE_WORLD_LIGHT = Category.DETAILS.add(new ConfigBoolean("useWorldLight", true, "explayerenderer.gui.settings.use_world_light.desc"));
    }

    public enum Category {
        PARAMETERS("explayerenderer.gui.settings.parameters"),
        DETAILS("explayerenderer.gui.settings.details");

        private final String key;
        private final List<IConfigBase> configs;

        Category(String key) {
            this.key = key;
            configs = new ArrayList<>();
        }

        protected <T extends IConfigBase> T add(T config) {
            this.configs.add(config);
            return config;
        }

        public List<IConfigBase> getConfigs() {
            return ImmutableList.copyOf(this.configs);
        }

        public String getKey() {
            return this.key;
        }
    }

}
