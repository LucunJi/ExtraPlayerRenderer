package github.io.lucunji.explayerenderer.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import github.io.lucunji.explayerenderer.config.Configs;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> Configs.HANDLER.generateGui().generateScreen(screen);
    }
}