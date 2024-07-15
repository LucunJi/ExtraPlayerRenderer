package github.io.lucunji.explayerenderer.config;

import github.io.lucunji.explayerenderer.Main;
import github.io.lucunji.explayerenderer.api.config.model.ConfigOption;
import github.io.lucunji.explayerenderer.api.config.ConfigWidgetRegistry;
import github.io.lucunji.explayerenderer.api.config.view.ListWidget;
import github.io.lucunji.explayerenderer.api.config.view.Tab;
import github.io.lucunji.explayerenderer.client.gui.hud.ExtraPlayerHud;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static github.io.lucunji.explayerenderer.Main.CONFIGS;
import static github.io.lucunji.explayerenderer.Main.id;


public class ConfigScreen extends Screen {
    private static final int TAB_BUTTON_HEIGHT = 24;
    private static final int ENTRY_HEIGHT = 30;
    private static final int LIST_WIDTH_OFFSET = -50;

    private final Screen parent;

    private final ExtraPlayerHud previewHud;
    private final TabManager tabManager;
    private Tab[] tabs;
    private TabNavigationWidget tabNav;
    private final List<ListWidget> listWidgets;

    private final List<? extends ConfigOption<?>> options;

    public ConfigScreen(Screen parent, List<? extends ConfigOption<?>> options) {
        super(Text.of("Config Screen"));
        this.parent = parent;
        this.previewHud = new ExtraPlayerHud(MinecraftClient.getInstance());
        this.tabManager = new TabManager(this::addDrawableChild, this::remove);
        this.options = options;
        this.listWidgets = new ArrayList<>();
    }

    @Override
    protected void init() {
        this.tabs = generateTabs();
        this.addDrawableChild(this.tabNav = TabNavigationWidget
                .builder(this.tabManager, this.width)
                .tabs(tabs).build());

        int tabIdx = CONFIGS.lastConfigTabIdx.getValue();
        if (tabIdx < 0 || tabIdx >= tabs.length)
            CONFIGS.lastConfigTabIdx.setValue(tabIdx = 0); // will be saved when screen closes
        this.tabNav.selectTab(tabIdx, false);
        this.initTabNavigation();
    }

    private Tab[] generateTabs() {
        var tabs = new ArrayList<Tab>();
        var categoryLists = new HashMap<Identifier, ListWidget>();
        for (var option : options) {
            if (option.getCategory().equals(Configs.HIDDEN_CATEGORY)) continue;
            var configEntryOptioal = ConfigWidgetRegistry.DEFAULT.getConfigEntry(option);
            if (configEntryOptioal.isEmpty()) {
                Main.LOGGER.error("Could not find widget for option {}", option.getId());
                continue;
            }
            //noinspection DataFlowIssue
            var label = new TextWidget(option.getName(), this.client.textRenderer);
            label.setTooltip(Tooltip.of(option.getDescription()));

            var category = option.getCategory();
            if (!categoryLists.containsKey(category)) {
                var tab = new Tab(Text.translatable("config.%s.category.%s".formatted(category.getNamespace(), category.getPath())));
                var list = new ListWidget(this.width, this.height - TAB_BUTTON_HEIGHT, TAB_BUTTON_HEIGHT, ENTRY_HEIGHT);
                this.listWidgets.add(list);
                tab.addChild(list);
                tabs.add(tab);
                categoryLists.put(category, list);
            }
            categoryLists.get(category).addEntry(configEntryOptioal.get());

            if (option.getId().equals(id("enabled"))) {
                categoryLists.get(category).addEntry(this.getPresetsConfigEntry());
            }
        }

        if (tabs.isEmpty()) tabs.add(new Tab(Text.of("")));
        return tabs.toArray(Tab[]::new);
    }

    @Override
    protected void initTabNavigation() {
        if (this.tabNav == null) return;

        this.tabNav.setWidth(this.width);
        this.tabNav.init();
        for (var listWidget : this.listWidgets) {
            listWidget.setDimensions(this.width, this.height - TAB_BUTTON_HEIGHT);
            listWidget.setRowWidth(this.width + LIST_WIDTH_OFFSET);
        }
    }

    @Override
    public void close() {
        // -1 will become 0 after validation
        CONFIGS.lastConfigTabIdx.setValue(ArrayUtils.indexOf(tabs, tabManager.getCurrentTab()));
        //noinspection DataFlowIssue
        this.client.setScreen(this.parent);
        Main.CONFIG_PERSISTENCE.save(CONFIGS.getOptions());
    }

    @Override
    protected void applyBlur(float delta) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // only render when the screen is opened in game
        //noinspection DataFlowIssue
        if (this.client.world != null) {
            this.previewHud.render(this.client.getRenderTickCounter().getTickDelta(true));
            // put behind GUI
            context.getMatrices().translate(0, 0, 200);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {return false;}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(Main.CONFIG_KEY).getCode() && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private ListWidget.ListEntry getPresetsConfigEntry() {
        final int buttonWidth = 70, gap = 10, buttonHeight = 20, labelYOffset = 7;
        @SuppressWarnings("DataFlowIssue")
        var presetLabel = new TextWidget(Text.translatable("config.%s.option.presets".formatted(Main.MOD_ID)), this.client.textRenderer);
        presetLabel.setTooltip(Tooltip.of(Text.translatable("config.%s.option.presets.desc".formatted(Main.MOD_ID))));
        var topLeft = new ConfigWidgetRegistry.ConfigButton(buttonWidth, buttonHeight, getPresetText("top_left"), getPresetPressAction(CONFIGS.topLeft));
        var topRight = new ConfigWidgetRegistry.ConfigButton(buttonWidth, buttonHeight, getPresetText("top_right"), getPresetPressAction(CONFIGS.topRight));
        var bottomLeft = new ConfigWidgetRegistry.ConfigButton(buttonWidth, buttonHeight, getPresetText("bottom_left"), getPresetPressAction(CONFIGS.bottomLeft));
        var bottomRight = new ConfigWidgetRegistry.ConfigButton(buttonWidth, buttonHeight, getPresetText("bottom_right"), getPresetPressAction(CONFIGS.bottomRight));
        var children = List.of(presetLabel, topLeft, topRight, bottomLeft, bottomRight);
        return new ListWidget.ListEntry() {
            @Override
            public List<? extends Selectable> selectableChildren() {return children;}

            @Override
            public List<? extends Element> children() {return children;}

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                presetLabel.setPosition(x, y + labelYOffset);
                topLeft.setPosition(x + entryWidth - buttonWidth * 4 - gap * 3, y);
                topRight.setPosition(x + entryWidth - buttonWidth * 3 - gap * 2, y);
                bottomLeft.setPosition(x + entryWidth - buttonWidth * 2 - gap, y);
                bottomRight.setPosition(x + entryWidth - buttonWidth, y);

                for (ClickableWidget child : children) child.render(context, mouseX, mouseY, tickDelta);
            }
        };
    }

    private Text getPresetText(String id) {return Text.translatable("config.%s.presets.%s".formatted(Main.MOD_ID, id));}

    private ButtonWidget.PressAction getPresetPressAction(Configs.Presets presets) {
        return ignored -> {
            presets.load();
            // clear everything and re-init, AVOID MEMORY LEAK
            int tabIdx = ArrayUtils.indexOf(tabs, tabManager.getCurrentTab());
            this.clearChildren();
            this.listWidgets.clear();
            this.init();
            this.tabNav.selectTab(tabIdx, false);
        };
    }
}
