package github.io.lucunji.explayerenderer.mixin.retexture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import github.io.lucunji.explayerenderer.api.config.view.Retextured;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SliderWidget.class)
public class SliderWidgetMixin {
    @WrapOperation(method = "renderWidget", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    })
    public void drawTransparentTextFieldTexture(DrawContext instance, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
        if (this instanceof Retextured retextured) original.call(instance, retextured.retexture(texture), x, y, width, height);
        else original.call(instance, texture, x, y, width, height);
    }
}
