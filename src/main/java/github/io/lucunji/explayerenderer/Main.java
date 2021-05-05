package github.io.lucunji.explayerenderer;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import github.io.lucunji.explayerenderer.config.ConfigHandler;
import github.io.lucunji.explayerenderer.config.Configs;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer{

    public static final String MOD_ID = "explayerenderer";

    @Override
    public void onInitialize(){
        ConfigManager.getInstance().registerConfigHandler(MOD_ID, new ConfigHandler());
        new Configs();
        ConfigHandler.loadFile();
        InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());
        Configs.MENU_OPEN_KEY.getKeybind().setCallback(new KeyBindHandler());
    }
}
