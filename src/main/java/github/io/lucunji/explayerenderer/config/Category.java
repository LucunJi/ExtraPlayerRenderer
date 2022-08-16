package github.io.lucunji.explayerenderer.config;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import github.io.lucunji.explayerenderer.Main;

import java.util.ArrayList;
import java.util.List;

public enum Category {
    GENERAL, ROTATIONS, POSTURES, DETAILS;

    private final String key;
    private final List<IConfigBase> configs;

    Category() {
        this.key = Utils.getConfigI18nNameKey(Main.MOD_ID, CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, this.toString()));
        configs = new ArrayList<>();
    }

    <T extends IConfigBase> T add(T config) {
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
