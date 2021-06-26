package github.io.lucunji.explayerenderer.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;

import java.util.ArrayList;
import java.util.List;

public enum Category {
    GENERAL("config.explayerenderer.general"),
    DETAILS("config.explayerenderer.details");

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
