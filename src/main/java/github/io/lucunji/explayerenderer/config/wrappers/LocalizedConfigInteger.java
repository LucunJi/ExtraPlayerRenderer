package github.io.lucunji.explayerenderer.config.wrappers;

import com.google.common.base.CaseFormat;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.util.StringUtils;

import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nDescKey;
import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nNameKey;

public class LocalizedConfigInteger extends ConfigInteger {
    private final String nameKey;

    public LocalizedConfigInteger(String modid, String key, int defaultValue, int minValue, int maxValue) {
        this(modid, key, defaultValue, minValue, maxValue, false);
    }

    public LocalizedConfigInteger(String modid, String key, int defaultValue, int minValue, int maxValue, boolean useSlider) {
        super(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key),
                defaultValue, minValue, maxValue, useSlider, getConfigI18nDescKey(modid, key));
        this.nameKey = getConfigI18nNameKey(modid, key);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(nameKey);
    }
}
