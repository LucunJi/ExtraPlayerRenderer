package github.io.lucunji.explayerenderer.config;

public class Utils {
    public static final String CONFIG_PREFIX = "config";
    public static final String CONFIG_OPTION_PREFIX = "config_option";

    public static String getConfigI18nNameKey(String modid, String key) {
        return CONFIG_PREFIX + "." + modid + "." + key;
    }

    public static String getConfigI18nDescKey(String modid, String key) {
        return CONFIG_PREFIX + "." + modid + "." + key + ".desc";
    }

    public static String getConfigOptionI18nNameKey(String modid, String configKey, String key) {
        return CONFIG_OPTION_PREFIX + "." + modid + "." + configKey + "." + key;
    }
}
