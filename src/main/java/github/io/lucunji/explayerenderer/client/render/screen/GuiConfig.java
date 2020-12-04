package github.io.lucunji.explayerenderer.client.render.screen;

import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import github.io.lucunji.explayerenderer.client.render.PlayerHUD;
import github.io.lucunji.explayerenderer.config.Configs;
import github.io.lucunji.explayerenderer.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import org.apache.logging.log4j.LogManager;

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
                currentTab = ((TabButton) buttonBase).category;
                this.reCreateListWidget();
                this.getListWidget().resetScrollbarPosition();
                this.initGui();
            });
            x += tabButton.getWidth() + 2;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        playerHUD.render(++this.ticks, partialTicks);
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

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        if (super.onKeyTyped(keyCode, scanCode, modifiers)) return true;
        if (Main.MASTER_CONTROL.matchesKey(keyCode, scanCode)) {
            this.onClose();
        }
        return true;
    }

}
