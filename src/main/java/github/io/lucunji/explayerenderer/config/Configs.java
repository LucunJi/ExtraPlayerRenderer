package github.io.lucunji.explayerenderer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigDouble;

import java.util.ArrayList;
import java.util.List;

public class Configs {
    public static final ConfigDouble OFFSET_X = Category.PARAMETERS.add(new ConfigDouble("offsetX", 0.05, -0.5, 1.5, "X offset of rendered player."));
    public static final ConfigDouble OFFSET_Y = Category.PARAMETERS.add(new ConfigDouble("offsetY", 1.5, -0.5, 2.5, "Y offset of rendered player."));
//    public static final ConfigDouble SIZE = register("size", new DoubleSetting(1D));
//    public static final ConfigBoolean MIRROR = register("mirror", new BooleanSetting(false));
//
//    public static final BooleanSetting HURT_FLASH = register("hurt_flash", new BooleanSetting(true));
//    public static final BooleanSetting SWING_HANDS = register("swing_hands", new BooleanSetting(true));
//    public static final DoubleSetting LIGHT_DEGREE = register("light_degree", new DoubleSetting(0d));
//
//    public static final DoubleSetting PITCH_MIN = register("pitch_min", new DoubleSetting(-20d));
//    public static final DoubleSetting PITCH_MAX = register("pitch_max", new DoubleSetting(20d));
//    public static final DoubleSetting HEAD_YAW_MIN = register("head_yaw_min", new DoubleSetting(-15d));
//    public static final DoubleSetting HEAD_YAW_MAX = register("head_yaw_max", new DoubleSetting(-15d));
//    public static final DoubleSetting BODY_YAW_MIN = register("body_yaw_min", new DoubleSetting(0d));
//    public static final DoubleSetting BODY_YAW_MAX = register("body_yaw_max", new DoubleSetting(0d));
//
//    public static final DoubleSetting SNEAKING_OFFSET_Y = register("sneaking_offset_y", new DoubleSetting(-30d));
//    public static final DoubleSetting ELYTRA_OFFSET_Y = register("elytra_offset_y", new DoubleSetting(-120d));

    public static class Category {
        public static final Category PARAMETERS = new Category("explayerenderer.gui.setting_screen.parameters");

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
    }

}
