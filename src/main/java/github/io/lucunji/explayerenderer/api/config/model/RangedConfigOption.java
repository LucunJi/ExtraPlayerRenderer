package github.io.lucunji.explayerenderer.api.config.model;

import org.jetbrains.annotations.NotNull;

public interface RangedConfigOption<T extends Comparable<T>> extends ConfigOption<T> {
    @NotNull
    T getMax();

    @NotNull
    T getMin();
}
