package github.io.lucunji.explayerenderer.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ControllerWidget.class)
public abstract class ControllerWidgetMixin {
    @Final
    @Shadow(remap = false)
    protected YACLScreen screen;

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/gui/controllers/ControllerWidget;drawButtonRect(Lnet/minecraft/client/gui/DrawContext;IIIIZZ)V"))
    public boolean skipDrawingButtonBackground(ControllerWidget<?> instance, DrawContext drawContext, int x1, int y1, int x2, int y2, boolean hovered, boolean enabled) {
        return !Configs.isConfigScreen(this.screen);
    }
}
