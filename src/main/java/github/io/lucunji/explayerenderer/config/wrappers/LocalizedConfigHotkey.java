package github.io.lucunji.explayerenderer.config.wrappers;

import com.google.common.base.CaseFormat;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.StringUtils;

import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nDescKey;
import static github.io.lucunji.explayerenderer.config.Utils.getConfigI18nNameKey;

public class LocalizedConfigHotkey extends ConfigHotkey {
    private final String nameKey;

    public LocalizedConfigHotkey(String modid, String key, String defaultStorageString) {
        super(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key),
                defaultStorageString, getConfigI18nDescKey(modid, key));
        this.nameKey = getConfigI18nNameKey(modid, key);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(nameKey);
    }
}
