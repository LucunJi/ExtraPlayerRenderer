package github.io.lucunji.explayerenderer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.*;

import java.util.ArrayList;
import java.util.List;

public class Configs {
    public static final ConfigString PLAYER_NAME;

    public static final ConfigDouble OFFSET_X;
    public static final ConfigDouble OFFSET_Y;
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
    public static final ConfigDouble LIGHT_DEGREE;

    static {
        PLAYER_NAME = Category.PARAMETERS.add(new ConfigString("playerName", "", "The name of player to render. It will be your name in default."));

        OFFSET_X = Category.PARAMETERS.add(new ConfigDouble("offsetX", 0.05, -0.5, 1.5, "X offset of rendered player."));
        OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("offsetY", 1.5, -0.5, 2.5, "Y offset of rendered player."));
        SIZE = Category.PARAMETERS.add(new ConfigDouble("size", 0.5, 0, 2, "Size rendered player."));
        MIRROR = Category.PARAMETERS.add(new ConfigBoolean("mirror", false, "If rendered player is flipped respect to y-axis."));
        PITCH_MIN = Category.PARAMETERS.add(new ConfigDouble("pitchMin", -20, -180, 180, "Lower bound of pitch."));
        PITCH_MAX = Category.PARAMETERS.add(new ConfigDouble("pitchMax", 20, -180, 180, "Upper bound of pitch."));
        PITCH_OFFSET = Category.PARAMETERS.add(new ConfigDouble("pitchOffset", 0, -90, 90, "Offset of pitch."));
        HEAD_YAW_MIN = Category.PARAMETERS.add(new ConfigDouble("headYawMin", -15, -180, 180, "Lower bound of head yaw."));
        HEAD_YAW_MAX = Category.PARAMETERS.add(new ConfigDouble("headYawMax", -15, -180, 180, "Upper bound of head yaw."));
        BODY_YAW_MIN = Category.PARAMETERS.add(new ConfigDouble("bodyYawMin", 0, -180, 180, "Lower bound of body yaw."));
        BODY_YAW_MAX = Category.PARAMETERS.add(new ConfigDouble("bodyYawMax", 0, -180, 180, "Upper bound of body yaw."));
        SNEAKING_OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("sneakingYOffset", -30, -100, 100, "Y offset when player is sneaking."));
        ELYTRA_OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("elytraYOffset", -120, -300, 300, "Y offset when player is flying using an elytra."));

        HURT_FLASH = Category.DETAILS.add(new ConfigBoolean("hurtFlash", true, "If the reddish flash will be rendered when player takes damage."));
        SWING_HANDS = Category.DETAILS.add(new ConfigBoolean("swingHands", true, "If the hands of player will swing when player moves or tries to break block."));
        LIGHT_DEGREE = Category.DETAILS.add(new ConfigDouble("lightDegree", 0, -180, 180, "The degree of light source which produces shadow effects"));
    }

    public enum Category {
        PARAMETERS("explayerenderer.gui.setting_screen.parameters"),
        DETAILS("explayerenderer.gui.setting_screen.details");

        private final String key;
        private final List<IConfigBase> configs;

        private Category(String key) {
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
