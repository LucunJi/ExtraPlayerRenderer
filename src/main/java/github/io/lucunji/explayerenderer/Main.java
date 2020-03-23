package github.io.lucunji.explayerenderer;

import github.io.lucunji.explayerenderer.settings.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class Main implements ModInitializer{

    public static final String MOD_ID = "explayerenderer";

    protected static final FabricKeyBinding MASTER_CONTROL = FabricKeyBinding.Builder.create(
            new Identifier(MOD_ID, "master_control"),
            InputUtil.Type.KEYSYM,
            InputUtil.fromName("key.keyboard.f8").getKeyCode(),
            "key.categories.ui").build();

    public static final BooleanSetting MIRROR = register("mirror", new BooleanSetting(false));
    public static final IntegerSetting OFFSET_X = register("offset_x", new IntegerSetting(0));
    public static final IntegerSetting OFFSET_Y = register("offset_y", new IntegerSetting(0));
    public static final DoubleSetting SIZE = register("size", new DoubleSetting(1D));

    public static final BooleanSetting HURT_FLASH = register("hurt_flash", new BooleanSetting(true));
    public static final BooleanSetting SWING_HANDS = register("swing_hands", new BooleanSetting(true));
    public static final DoubleSetting LIGHT_DEGREE = register("light_degree", new DoubleSetting(0d));

    public static final DoubleSetting PITCH_MIN = register("pitch_min", new DoubleSetting(-20d));
    public static final DoubleSetting PITCH_MAX = register("pitch_max", new DoubleSetting(20d));
    public static final DoubleSetting HEAD_YAW_MIN = register("head_yaw_min", new DoubleSetting(-15d));
    public static final DoubleSetting HEAD_YAW_MAX = register("head_yaw_max", new DoubleSetting(-15d));
    public static final DoubleSetting BODY_YAW_MIN = register("body_yaw_min", new DoubleSetting(0d));
    public static final DoubleSetting BODY_YAW_MAX = register("body_yaw_max", new DoubleSetting(0d));

    public static final DoubleSetting SNEAKING_OFFSET_Y = register("sneaking_offset_y", new DoubleSetting(-30d));
    public static final DoubleSetting ELYTRA_OFFSET_Y = register("elytra_offset_y", new DoubleSetting(-120d));


    private static <T extends AbstractSetting> T register(String key, T setting) {
        SettingManager.INSTANCE.getSettings().put(key, setting);
        return setting;
    }

    @Override
    public void onInitialize(){
        SettingManager.INSTANCE.load();
        KeyBindingRegistryImpl.INSTANCE.register(MASTER_CONTROL);
        ClientTickCallback.EVENT.register(new KeyBindHandler());

    }
}
