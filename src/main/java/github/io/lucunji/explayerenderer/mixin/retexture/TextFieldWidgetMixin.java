package github.io.lucunji.explayerenderer.mixin.retexture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.api.config.view.Retextured;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends ClickableWidget {
    public TextFieldWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(method = "renderWidget", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V")
    })
    public void drawTransparentTextFieldTexture(DrawContext instance, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
        if (this instanceof Retextured retextured) {
            instance.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            original.call(instance, retextured.retexture(texture), x, y, width, height);
            instance.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            return;
        }
        original.call(instance, texture, x, y, width, height);
    }
}
