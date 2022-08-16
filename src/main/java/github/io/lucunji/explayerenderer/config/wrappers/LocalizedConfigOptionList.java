package github.io.lucunji.explayerenderer.config.wrappers;

import com.google.common.base.CaseFormat;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.util.StringUtils;

import static github.io.lucunji.explayerenderer.config.Utils.*;

public class LocalizedConfigOptionList extends ConfigOptionList {
    private final String nameKey;

    public LocalizedConfigOptionList(String modid, String key, IConfigOptionListEntry defaultValue) {
        super(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key),
                defaultValue, getConfigI18nDescKey(modid, key));
        this.nameKey = getConfigI18nNameKey(modid, key);
    }

    @Override
    public String getConfigGuiDisplayName() {
        return StringUtils.translate(nameKey);
    }

}
