package github.io.lucunji.explayerenderer.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public class ScreenMixin {
    @WrapWithCondition(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;applyBlur(F)V"))
    public boolean skipBlurringBackground(Screen instance, float delta) {
        return !Configs.isConfigScreen(((Screen) (Object) this));
    }
}
