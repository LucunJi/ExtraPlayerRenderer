package github.io.lucunji.explayerenderer.mixin.yacl;

import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin {
    @Inject(method = "drawButtonRect", at = @At(value = "HEAD"), cancellable = true)
    public void skipDrawingButtonBackground(DrawContext graphics, int x1, int y1, int x2, int y2, boolean hovered, boolean enabled, CallbackInfo ci) {
        //noinspection ConstantValue
        if (((Element) this) instanceof ControllerWidget<?> controllerWidget
                && Configs.isConfigScreen(((ControllerWidgetAccessor) controllerWidget).getScreen())) {
            ci.cancel();
        }
    }
}
