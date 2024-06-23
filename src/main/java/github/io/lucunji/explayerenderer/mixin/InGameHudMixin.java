package github.io.lucunji.explayerenderer.mixin;

import github.io.lucunji.explayerenderer.client.render.PlayerHUDRenderer;
import github.io.lucunji.explayerenderer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private final PlayerHUDRenderer playerHUDRenderer = new PlayerHUDRenderer();
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At("RETURN"))
    void onRenderFinish(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.skipGameRender || (this.client.currentScreen != null && !Configs.isConfigScreen(client.currentScreen)))
            return;
        playerHUDRenderer.render(client.getRenderTickCounter().getTickDelta(true));
    }
}
