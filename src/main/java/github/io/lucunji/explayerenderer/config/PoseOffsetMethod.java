package github.io.lucunji.explayerenderer.config;

import com.google.common.base.CaseFormat;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import github.io.lucunji.explayerenderer.Main;

public enum PoseOffsetMethod implements IConfigOptionListEntry {
    AUTO, MANUAL, FORCE_STANDING, DISABLED;

    public final String nameKey;

    PoseOffsetMethod() {
        this.nameKey = Utils.getConfigOptionI18nNameKey(Main.MOD_ID, Configs.POSE_OFFSET_METHOD_KEY,
                CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, this.toString()));
    }


    @Override
    public String getStringValue() {
        return this.toString();
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate(nameKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        IConfigOptionListEntry[] values = PoseOffsetMethod.values();
        return forward ? values[(this.ordinal() + 1) % values.length]
                : values[(this.ordinal() + values.length - 1) % values.length];
    }

    @Override
    public IConfigOptionListEntry fromString(String value) {
        return PoseOffsetMethod.valueOf(value);
    }
}
