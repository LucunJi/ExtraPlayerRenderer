package github.io.lucunji.explayerenderer.mixin.yacl;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.yacl3.gui.SearchFieldWidget;
import github.io.lucunji.explayerenderer.Main;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin {
    @Unique
    private static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.of(Main.MOD_ID, "widget/text_field"),
            Identifier.of(Main.MOD_ID, "widget/text_field_highlighted"));

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ButtonTextures;get(ZZ)Lnet/minecraft/util/Identifier;"))
    public Identifier drawTransparentTextFieldTexture(ButtonTextures instance, boolean enabled, boolean focused, Operation<Identifier> original) {
        //noinspection ConstantValue
        if (Configs.isConfigScreen(MinecraftClient.getInstance().currentScreen) &&
                ((TextFieldWidget) (Object) this) instanceof SearchFieldWidget) {
            return TEXTURES.get(enabled, focused);
        }
        return original.call(instance, enabled, focused);
    }
}
