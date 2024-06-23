package github.io.lucunji.explayerenderer.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.isxander.yacl3.gui.YACLScreen;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin {

    @WrapWithCondition(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public boolean skipDrawingButtonBackground(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (!Configs.isConfigScreen(minecraftClient.currentScreen))
            return true;
        var currentTab = (YACLScreen.CategoryTab) ((YACLScreen) minecraftClient.currentScreen).tabManager.getCurrentTab();
        var thisBtn = (ClickableWidget) (Object) this;
        return currentTab == null || currentTab.undoButton != thisBtn && currentTab.cancelResetButton != thisBtn && currentTab.saveFinishedButton != thisBtn;
    }
}
