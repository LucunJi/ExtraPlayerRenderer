/*
 *     Highly configurable paper doll mod.
 *     Copyright (C) 2024  LucunJi, And all  Contributors
 *
 *     This file is part of Extra Player Renderer.
 *
 *     Extra Player Renderer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extra Player Renderer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Extra Player Renderer.  If not, see <https://www.gnu.org/licenses/>.
 */

package github.io.lucunji.extraplayerrenderer.config;

import com.google.common.collect.ImmutableList;
import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import github.io.lucunji.extraplayerrenderer.config.model.ConfigOption;
import github.io.lucunji.extraplayerrenderer.config.model.RangedConfigOption;
import github.io.lucunji.extraplayerrenderer.config.view.ListWidget;
import github.io.lucunji.extraplayerrenderer.config.view.Retextured;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConfigWidgetRegistry {
    public static final int RESET_BUTTON_WIDTH = 50;
    public static final ConfigWidgetRegistry DEFAULT = new ConfigWidgetRegistry();
    private static final String ON_LANGKEY = "config.%s.on".formatted(ExtraPlayerRenderer.MOD_ID);
    private static final String OFF_LANGKEY = "config.%s.off".formatted(ExtraPlayerRenderer.MOD_ID);
    private static final String RESET_LANGKEY = "config.%s.reset".formatted(ExtraPlayerRenderer.MOD_ID);
    private static final int WIDGET_WIDTH = 150;
    private static final int WIDGET_HEIGHT = 20;
    private final List<OptionWidgetEntry<?, ?>> defaultWidgets = new ArrayList<>();

    private ConfigWidgetRegistry() {
        defaultWidgets.add(new OptionWidgetEntry<>(Boolean.class, ConfigOption.class, ConfigWidgetRegistry::getOnOffButton));
        defaultWidgets.add(new OptionWidgetEntry<>(Enum.class, ConfigOption.class, ConfigWidgetRegistry::getEnumCycleButton));
        defaultWidgets.add(new OptionWidgetEntry<>(Double.class, RangedConfigOption.class, ConfigWidgetRegistry::getDoubleSlider));
        defaultWidgets.add(new OptionWidgetEntry<>(Integer.class, RangedConfigOption.class, ConfigWidgetRegistry::getIntegerSlider));
        defaultWidgets.add(new OptionWidgetEntry<>(String.class, ConfigOption.class, ConfigWidgetRegistry::getStringField));
    }

    private static <T> Button getResetButton(ConfigOption<T> option, ConfigValueUpdater<T> setter) {
        var reset = new ConfigButton(RESET_BUTTON_WIDTH, WIDGET_HEIGHT, Component.translatable(RESET_LANGKEY), self -> setter.setValue(option.getDefaultValue()));
        reset.active = !option.isValueDefault();
        return reset;
    }

    private static ListWidget.ListEntry getStringField(ConfigOption<String> option) {
        var setter = new ConfigValueUpdater<String>();
        var field = new ConfigTextField(WIDGET_WIDTH, WIDGET_HEIGHT);
        field.setValue(option.getValue());
        field.setResponder(setter::setValue);
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            field.setValue(option.getValue());
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.create(option.getDescription()), field, reset);
    }

    private static ListWidget.ListEntry getDoubleSlider(RangedConfigOption<Double> option) {
        var setter = new ConfigValueUpdater<Double>();
        var slider = new ConfigSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                (option.getValue() - option.getMin()) / (option.getMax() - option.getMin()),
                val -> setter.setValue(Mth.lerp(val, option.getMin(), option.getMax())),
                val -> Component.nullToEmpty("%.2f".formatted(option.getValue())));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            slider.setValueNoEvent((option.getValue() - option.getMin()) / (option.getMax() - option.getMin()));
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.create(option.getDescription()), slider, reset);
    }

    private static ListWidget.ListEntry getIntegerSlider(RangedConfigOption<Integer> option) {
        var setter = new ConfigValueUpdater<Integer>();
        double steps = option.getMax() - option.getMin();
        var slider = new ConfigSlider(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                (option.getValue() - option.getMin()) / steps,
                val -> setter.setValue((int) Math.round(val * steps) + option.getMin()),
                val -> Component.nullToEmpty(Integer.toString(option.getValue())));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            slider.setValueNoEvent((option.getValue() - option.getMin()) / steps);
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.create(option.getDescription()), slider, reset);
    }

    private static ListWidget.ListEntry getOnOffButton(ConfigOption<Boolean> option) {
        var setter = new ConfigValueUpdater<Boolean>();
        var btn = new ConfigButton(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                Component.translatable(option.getValue() ? ON_LANGKEY : OFF_LANGKEY),
                self -> setter.setValue(!option.getValue()));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            btn.setMessage(Component.translatable(option.getValue() ? ON_LANGKEY : OFF_LANGKEY));
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.create(option.getDescription()), btn, reset);
    }

    private static <T extends Enum<?>> ListWidget.ListEntry getEnumCycleButton(ConfigOption<T> option) {
        var setter = new ConfigValueUpdater<T>();
        var values = option.getType().getEnumConstants();
        var translations = new Component[values.length];
        for (int i = 0; i < values.length; i++) {
            translations[i] = Component.translatable("config.%s.enum.%s.%s".formatted(ExtraPlayerRenderer.MOD_ID, option.getType().getSimpleName(), values[i].name()));
        }
        var btn = new ConfigButton(
                WIDGET_WIDTH, WIDGET_HEIGHT,
                translations[option.getValue().ordinal()],
                self -> setter.setValue(values[(option.getValue().ordinal() + 1) % values.length]));
        var reset = getResetButton(option, setter);
        setter.setUpdateHandler(val -> {
            option.setValue(val);
            btn.setMessage(translations[option.getValue().ordinal()]);
            reset.active = !option.isValueDefault();
        });
        return new ConfigEntry(option.getName(), Tooltip.create(option.getDescription()), btn, reset);
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

    record OptionWidgetEntry<T, U extends ConfigOption<T>>(Class<T> type, Class<U> optionType,
                                                           Function<U, ListWidget.ListEntry> widgetProvider) {
    }

    public static class ConfigButton extends Button implements Retextured {
        public ConfigButton(int width, int height, Component message, OnPress action) {
            super(0, 0, width, height, message, action, DEFAULT_NARRATION);
        }

        @Override
        public ResourceLocation retexture(ResourceLocation oldTexture) {
            return ExtraPlayerRenderer.withExtraPlayerRendererNamespace(oldTexture.getPath());
        }
    }

    private static class ConfigTextField extends EditBox implements Retextured {
        public ConfigTextField(int width, int height) {
            super(Minecraft.getInstance().font, width, height, Component.empty());
        }

        @Override
        public ResourceLocation retexture(ResourceLocation oldTexture) {
            return ExtraPlayerRenderer.withExtraPlayerRendererNamespace(oldTexture.getPath());
        }
    }

    private static class ConfigSlider extends AbstractSliderButton implements Retextured {
        @NotNull
        private final Consumer<Double> changeListener;
        @NotNull
        private final Function<Double, Component> messageProvider;

        public ConfigSlider(int width, int height, double value,
                            @NotNull Consumer<Double> changeListener, @NotNull Function<Double, Component> messageProvider) {
            super(0, 0, width, height, Component.empty(), value);
            this.changeListener = changeListener;
            this.messageProvider = messageProvider;
            this.updateMessage();
        }

        @Override
        public ResourceLocation retexture(ResourceLocation oldTexture) {
            return ExtraPlayerRenderer.withExtraPlayerRendererNamespace(oldTexture.getPath());
        }

        @Override
        protected void applyValue() {
            this.changeListener.accept(this.value);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(this.messageProvider.apply(this.value));
        }

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

        private final List<AbstractWidget> children;
        private final StringWidget label;
        private final AbstractWidget widget;
        private final Button reset;

        private ConfigEntry(Component label, Tooltip labelTooltip, AbstractWidget widget, Button resetButton) {
            var labelWidget = new StringWidget(label, Minecraft.getInstance().font);
            labelWidget.setTooltip(labelTooltip);
            this.label = labelWidget;
            this.widget = widget;
            this.reset = resetButton;
            this.children = ImmutableList.of(this.widget, this.reset);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            label.setPosition(x, y + LABEL_Y_OFFSET);
            reset.setPosition(x + entryWidth - reset.getWidth(), y);
            widget.setPosition(x + entryWidth - reset.getWidth() - widget.getWidth() - GAP_WIDTH, y);
            widget.render(context, mouseX, mouseY, tickDelta);
            reset.render(context, mouseX, mouseY, tickDelta);
            label.render(context, mouseX, mouseY, tickDelta);
        }
    }

}
