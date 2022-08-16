package github.io.lucunji.explayerenderer.config.wrappers;

import com.google.common.base.CaseFormat;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.util.StringUtils;

import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nDescKey;
import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nNameKey;

public class LocalizedConfigDouble extends ConfigDouble {
    private final String nameKey;

    public LocalizedConfigDouble(String modid, String key, double defaultValue, double minValue, double maxValue) {
        this(modid, key, defaultValue, minValue, maxValue, false);
    }

    public LocalizedConfigDouble(String modid, String key, double defaultValue, double minValue, double maxValue, boolean useSlider) {
        super(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key),
                defaultValue, minValue, maxValue, useSlider, getConfigI18nDescKey(modid, key));
        this.nameKey = getConfigI18nNameKey(modid, key);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(nameKey);
    }
}
