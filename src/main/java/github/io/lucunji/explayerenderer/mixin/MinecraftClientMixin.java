package github.io.lucunji.explayerenderer.mixin;

import github.io.lucunji.explayerenderer.Main;
import github.io.lucunji.explayerenderer.config.ConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Shadow @Nullable public Screen currentScreen;

    @Inject(method = "handleInputEvents", at = @At("RETURN"))
    private void onHandleInputEventsFinish(CallbackInfo ci) {
        while (Main.CONFIG_KEY.wasPressed()) {
            this.setScreen(new ConfigScreen(this.currentScreen, Main.CONFIGS.getOptions()));
        }
    }
}
