package github.io.lucunji.explayerenderer.api.config.model;

import github.io.lucunji.explayerenderer.Main;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for non-values within range {@code [minValue, maxValue]}
 */
public class SimpleNumericOption<T extends Number & Comparable<T>> extends SimpleOption<T> implements RangedConfigOption<T> {
    @NotNull
    private final T min;
    @NotNull
    private final T max;

    private static <T extends Number & Comparable<T>> boolean withinRangeInclusive(T value, T minValue, T maxValue) {
        return minValue.compareTo(value) <= 0 && maxValue.compareTo(value) >= 0;
    }

    public SimpleNumericOption(Identifier category, Identifier id, @NotNull T defaultValue, @NotNull T min, @NotNull T max) {
        super(category, id, defaultValue);
        this.min = min;
        this.max = max;
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("The maximum value must be greater than the minimum value");
        }
        if (!withinRangeInclusive(defaultValue, min, max)) {
            throw new IllegalArgumentException("The default value must be in range [minValue] to [maxValue]");
        }
    }

    @Override
    public T validate(T oldValue, T newValue) {
        var validated = super.validate(oldValue, newValue);
        if (!withinRangeInclusive(validated, min, max)) {
            Main.LOGGER.warn("The new value for option {} is outside the range [{}, {}], reset to the old value", this.getId().toString(), min, max);
            return oldValue;
        }
        return newValue;
    }

    @Override
    @NotNull
    public T getMax() {
        return max;
    }

    @Override
    @NotNull
    public T getMin() {
        return min;
    }
}
