package github.io.lucunji.explayerenderer.api.config.model;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Objects;

public interface ConfigOption<T> {
    T getDefaultValue();

    /**
     * Invalid {@code value} must be logged as an {@link org.slf4j.event.Level#WARN}
     * @return a valid new value
     */
    T validate(T oldValue, T newValue);

    /**
     * @return if the value is changed
     */
    boolean setValue(T newValue);

    default boolean isValueDefault() {return Objects.equals(this.getValue(), this.getDefaultValue());}

    T getValue();

    Identifier getCategory();

    Identifier getId();

    Text getName();

    Text getDescription();

    Class<T> getType();
}
