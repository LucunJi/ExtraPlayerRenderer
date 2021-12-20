package github.io.lucunji.explayerenderer.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import github.io.lucunji.explayerenderer.client.render.screen.GuiConfig;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {

        return (screen) -> {
            GuiConfig gui = new GuiConfig();
            gui.setParentGui(screen);
            return gui;
        };
    }
}