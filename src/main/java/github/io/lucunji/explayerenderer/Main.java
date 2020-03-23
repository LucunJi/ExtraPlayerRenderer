package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.settings.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Main implements ModInitializer{

    public static final String MOD_ID = "explayerenderer";

    protected static final FabricKeyBinding MASTER_CONTROL = FabricKeyBinding.Builder.create(
            new Identifier(MOD_ID, ".master_control"),
            InputUtil.Type.KEYSYM,
            InputUtil.fromName("key.keyboard.f8").getKeyCode(),
            "key.categories.ui").build();

    public static class Settings {
        public static final BooleanSetting MIRROR = register("mirror", new BooleanSetting(false));
        public static final IntegerSetting OFFSET_X = register("offset_x", new IntegerSetting(0));
        public static final IntegerSetting OFFSET_Y = register("offset_y", new IntegerSetting(0));
        public static final DoubleSetting SIZE = register("size", new DoubleSetting(1D));

        private static <T extends AbstractSetting> T register(String key, T setting) {
            SettingManager.INSTANCE.getSettings().put(key, setting);
            return setting;
        }
    }

    @Override
    public void onInitialize(){
        SettingManager.INSTANCE.load();
        KeyBindingRegistryImpl.INSTANCE.register(MASTER_CONTROL);
        ClientTickCallback.EVENT.register(new KeyBindHandler());

    }
}
