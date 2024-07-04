package github.io.lucunji.explayerenderer.api.config.model;

import github.io.lucunji.explayerenderer.Main;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Simplest implementation of {@link ConfigOption} with no callbacks, and only checks for non-null values.
 */
public class SimpleOption<T> implements ConfigOption<T> {
    @NotNull
    private T value;
    @NotNull
    private final T defaultValue;
    private final Identifier category;
    private final Identifier id;
    private final Text name;
    private final Text description;
    private final Class<T> type;

    public SimpleOption(Identifier category, Identifier id, @NotNull T defaultValue) {
        this.category = category;
        this.id = id;
        this.name = Text.translatable("config.%s.option.%s".formatted(id.getNamespace(), id.getPath()));
        this.description = Text.translatable("config.%s.option.%s.desc".formatted(id.getNamespace(), id.getPath()));
        this.value = this.defaultValue = defaultValue;
        //noinspection unchecked
        this.type = (Class<T>) defaultValue.getClass();
    }

    @Override
    public T validate(T oldValue, T newValue) {
        if (newValue == null) {
            Main.LOGGER.warn("The new value for option {} is null, reset to the old value", this.getId().toString());
            return oldValue;
        }
        return newValue;
    }

    @Override
    public boolean setValue(T value) {
        var validated = this.validate(this.value, value);
        if (!Objects.equals(value, this.value)) {
            this.value = validated;
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public T getValue() {
        return value;
    }

    @NotNull
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Identifier getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public Text getDescription() {
        return description;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
