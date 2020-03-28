package github.io.lucunji.explayerenderer.client.render.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import github.io.lucunji.explayerenderer.client.render.PlayerHUD;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.Main;

import java.util.List;

public class GuiConfig extends GuiConfigsBase {
    private static Configs.Category currentTab = Configs.Category.PARAMETERS;
    PlayerHUD playerHUD;
    int ticks;

    public GuiConfig() {
        super(10, 50, Main.MOD_ID, null, "explayerenderer.gui.setting_screen");
        this.playerHUD = new PlayerHUD();
        this.ticks = 0;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        playerHUD.render(++this.ticks);
        GlStateManager.translated(0, 0, 1000);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(currentTab.getConfigs());
    }
}
