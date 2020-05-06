package github.io.lucunji.explayerenderer.client.render.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import github.io.lucunji.explayerenderer.client.render.PlayerHUD;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.Main;

import java.util.List;

public class GuiConfig extends GuiConfigsBase {
    private static Configs.Category currentTab = Configs.Category.PARAMETERS;
    PlayerHUD playerHUD;
    int ticks;

    public GuiConfig() {
        super(10, 50, Main.MOD_ID, null, "explayerenderer.gui.settings");
        this.playerHUD = new PlayerHUD();
        this.ticks = 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();
        int x = 10, y = 26;
        for (Configs.Category category : Configs.Category.values()) {
            ButtonGeneric tabButton = new TabButton(category, x, y, -1, 20, StringUtils.translate(category.getKey()));
            tabButton.setEnabled(true);
            this.addButton(tabButton, (buttonBase, i) -> {
                currentTab = ((TabButton)buttonBase).category;
                this.reCreateListWidget();
                this.getListWidget().resetScrollbarPosition();
                this.initGui();
            });
            x += tabButton.getWidth() + 2;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.translated(0, 0, -1000);
        playerHUD.render(++this.ticks);
        GlStateManager.translated(0, 0, 1000);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(currentTab.getConfigs());
    }

    public static class TabButton extends ButtonGeneric {
        private final Configs.Category category;
        public TabButton(Configs.Category category, int x, int y, int width, int height, String text, String... hoverStrings) {
            super(x, y, width, height, text, hoverStrings);
            this.category = category;
        }
    }
}
