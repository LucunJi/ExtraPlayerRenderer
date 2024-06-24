package github.io.lucunji.explayerenderer.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import github.io.lucunji.explayerenderer.Main;
import github.io.lucunji.explayerenderer.config.ConfigScreen;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, Main.CONFIGS.getOptions());
    }
}