package github.io.lucunji.explayerenderer.api.config;

import github.io.lucunji.explayerenderer.api.config.model.ConfigOption;

import java.nio.file.Path;
import java.util.List;

public interface ConfigPersistence {
    Path getPath();

    /**
     * Save config.
     * <br/>
     * To prevent crashing, it should log an error instead of throwing when it fails.
     *
     * @return save is successful
     */
    boolean save(List<? extends ConfigOption<?>> options);

    /**
     * Load config.
     * <br/>
     * To prevent crashing, it should log an error instead of throwing when it fails.
     *
     * @return load is successful
     */
    boolean load(List<? extends ConfigOption<?>> options);
}
