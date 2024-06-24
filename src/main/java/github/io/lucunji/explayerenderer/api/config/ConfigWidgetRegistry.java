package github.io.lucunji.explayerenderer.api.config;

import com.google.common.collect.ImmutableList;
import github.io.lucunji.explayerenderer.Main;
import github.io.lucunji.explayerenderer.api.config.model.ConfigOption;
import github.io.lucunji.explayerenderer.api.config.model.RangedConfigOption;
import github.io.lucunji.explayerenderer.api.config.view.ListWidget;
import github.io.lucunji.explayerenderer.api.config.view.Retextured;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static github.io.lucunji.explayerenderer.Main.id;

public class ConfigWidgetRegistry {
    private static final String ON_LANGKEY = "config.%s.on".formatted(Main.MOD_ID);
    private static final String OFF_LANGKEY = "config.%s.off".formatted(Main.MOD_ID);
    private static final String RESET_LANGKEY = "config.%s.reset".formatted(Main.MOD_ID);
    public static final int RESET_BUTTON_WIDTH = 50;
    private static final int WIDGET_WIDTH = 150;
    private static final int WIDGET_HEIGHT = 20;

    public static final ConfigWidgetRegistry DEFAULT = new ConfigWidgetRegistry();

    private final List<OptionWidgetEntry<?, ?>> defaultWidgets = new ArrayList<>();

    private ConfigWidgetRegistry() {
        defaultWidgets.add(new OptionWidgetEntry<>(Boolean.class, ConfigOption.class, ConfigWidgetRegistry::getOnOffButton));
        defaultWidgets.add(new OptionWidgetEntry<>(Enum.class, ConfigOption.class, ConfigWidgetRegistry::getEnumCycleButton));
        defaultWidgets.add(new OptionWidgetEntry<>(Double.class, RangedConfigOption.class, ConfigWidgetRegistry::getDoubleSlider));
        defaultWidgets.add(new OptionWidgetEntry<>(Integer.class, RangedConfigOption.class, ConfigWidgetRegistry::getIntegerSlider));
        defaultWidgets.add(new OptionWidgetEntry<>(String.class, ConfigOption.class, ConfigWidgetRegistry::getStringField));
    }

    public <T, U extends ConfigOption<T>> Optional<ListWidget.ListEntry> getConfigEntry(U option) {
        for (var entry : defaultWidgets) {
            if (entry.type.isAssignableFrom(option.getType())) {
                //noinspection unchecked
                var entryCast = (OptionWidgetEntry<T, U>) entry;
                return Optional.of(entryCast.widgetProvider.apply(option));
            }
        }
        return Optional.empty();
    }

    private static <T> ButtonWidget getResetButton(ConfigOption<T> option, ConfigValueUpdater<T> setter) {
        var reset = new ConfigButton(RESET_BUTTON_WIDTH, WIDGET_HEIGHT, Text.translatable(RESET_LANGKEY), _ -> setter.setValue(option.getDefaultValue()));
        reset.active = !option.isValueDefault();
        return reset;
    }

    private static ListWidget.ListEntry getStringField(ConfigOption<String> option) {
        var setter = new ConfigValueUpdater<String>();
        var field = new ConfigTextField(WIDGET_WIDTH, WIDGET_HEIGHT);
        field.setText(option.getValue());
        field.setChangedListener(setter::setValue);
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            field.setText(option.getValue());
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.of(option.getDescription()), field, reset);
    }

    private static ListWidget.ListEntry getDoubleSlider(RangedConfigOption<Double> option) {
        var setter = new ConfigValueUpdater<Double>();
        var slider = new ConfigSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                (option.getValue() - option.getMin()) / (option.getMax() - option.getMin()),
                val -> setter.setValue(MathHelper.lerp(val, option.getMin(), option.getMax())),
                _ -> Text.of("%.2f".formatted(option.getValue())));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            slider.setValueNoEvent((option.getValue() - option.getMin()) / (option.getMax() - option.getMin()));
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.of(option.getDescription()), slider, reset);
    }

    private static ListWidget.ListEntry getIntegerSlider(RangedConfigOption<Integer> option) {
        var setter = new ConfigValueUpdater<Integer>();
        double steps = option.getMax() - option.getMin();
        var slider = new ConfigSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                (option.getValue() - option.getMin()) / steps,
                val -> setter.setValue((int) Math.round(val * steps) + option.getMin()),
                _ -> Text.of(Integer.toString(option.getValue())));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            slider.setValueNoEvent((option.getValue() - option.getMin()) / steps);
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.of(option.getDescription()), slider, reset);
    }

    private static ListWidget.ListEntry getOnOffButton(ConfigOption<Boolean> option) {
        var setter = new ConfigValueUpdater<Boolean>();
        var btn = new ConfigButton(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                Text.translatable(option.getValue() ? ON_LANGKEY : OFF_LANGKEY),
                _ -> setter.setValue(!option.getValue()));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            btn.setMessage(Text.translatable(option.getValue() ? ON_LANGKEY : OFF_LANGKEY));
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.of(option.getDescription()), btn, reset);
    }

    private static <T extends Enum<?>> ListWidget.ListEntry getEnumCycleButton(ConfigOption<T> option) {
        var setter = new ConfigValueUpdater<T>();
        var values = option.getType().getEnumConstants();
        var translations = new Text[values.length];
        for (int i = 0; i < values.length; i++) {
            translations[i] = Text.translatable("config.%s.enum.%s.%s".formatted(Main.MOD_ID, option.getType().getSimpleName(), values[i].name()));
        }
        var btn = new ConfigButton(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                translations[option.getValue().ordinal()],
                _ -> setter.setValue(values[(option.getValue().ordinal() + 1) % values.length]));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            btn.setMessage(translations[option.getValue().ordinal()]);
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.of(option.getDescription()), btn, reset);
    }

    record OptionWidgetEntry<T, U extends ConfigOption<T>>(Class<T> type, Class<U> optionType,
                                                           Function<U, ListWidget.ListEntry> widgetProvider) {}

    public static class ConfigButton extends ButtonWidget implements Retextured {
        public ConfigButton(int width, int height, Text message, PressAction action) {
            super(0, 0, width, height, message, action, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public Identifier retexture(Identifier oldTexture) {return id(oldTexture.getPath());}
    }

    private static class ConfigTextField extends TextFieldWidget implements Retextured {
        public ConfigTextField(int width, int height) {
            super(MinecraftClient.getInstance().textRenderer, width, height, Text.empty());
        }

        @Override
        public Identifier retexture(Identifier oldTexture) {return id(oldTexture.getPath());}
    }

    private static class ConfigSlider extends SliderWidget implements Retextured {
        @NotNull private final Consumer<Double> changeListener;
        @NotNull private final Function<Double, Text> messageProvider;

        public ConfigSlider(int width, int height, double value,
                            @NotNull Consumer<Double> changeListener, @NotNull Function<Double, Text> messageProvider) {
            super(0, 0, width, height, Text.empty(), value);
            this.changeListener = changeListener;
            this.messageProvider = messageProvider;
            this.updateMessage();
        }

        @Override
        public Identifier retexture(Identifier oldTexture) {return id(oldTexture.getPath());}

        @Override
        protected void applyValue() {this.changeListener.accept(this.value);}

        @Override
        protected void updateMessage() {this.setMessage(this.messageProvider.apply(this.value));}

        public void setValueNoEvent(double value) {
            this.value = value;
            this.updateMessage();
        }
    }

    /**
     * Mediates operations of widgets, and decouples them from data models.
     */
    private static class ConfigValueUpdater<T> {
        private Consumer<T> updateHandler = null;
        private boolean preventUpdates = false;

        public void setUpdateHandler(Consumer<T> updateHandler) {
            this.updateHandler = updateHandler;
        }

        public void setValue(T newValue) {
            if (this.preventUpdates || this.updateHandler == null) return;
            this.preventUpdates = true;
            this.updateHandler.accept(newValue);
            this.preventUpdates = false;
        }
    }

    private static class ConfigEntry extends ListWidget.ListEntry {
        private static final int LABEL_Y_OFFSET = 7;
        private static final int GAP_WIDTH = 10;

        private final List<ClickableWidget> children;
        private final TextWidget label;
        private final ClickableWidget widget;
        private final ButtonWidget reset;

        private ConfigEntry(Text label, Tooltip labelTooltip, ClickableWidget widget, ButtonWidget resetButton) {
            var labelWidget = new TextWidget(label, MinecraftClient.getInstance().textRenderer);
            labelWidget.setTooltip(labelTooltip);
            this.label = labelWidget;
            this.widget = widget;
            this.reset = resetButton;
            this.children = ImmutableList.of(this.widget, this.reset);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return this.children;
        }

        @Override
        public List<? extends Element> children() {
            return this.children;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            label.setPosition(x, y + LABEL_Y_OFFSET);
            reset.setPosition(x + entryWidth - reset.getWidth(), y);
            widget.setPosition(x + entryWidth - reset.getWidth() - widget.getWidth() - GAP_WIDTH, y);
            widget.render(context, mouseX, mouseY, tickDelta);
            reset.render(context, mouseX, mouseY, tickDelta);
            label.render(context, mouseX, mouseY, tickDelta);
        }
    }

}
