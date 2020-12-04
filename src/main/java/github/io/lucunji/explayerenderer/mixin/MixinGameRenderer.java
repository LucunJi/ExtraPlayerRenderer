package github.io.lucunji.explayerenderer.mixin;

import github.io.lucunji.explayerenderer.client.render.PlayerHUD;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.GameRenderer.class)
public class MixinGameRenderer {

    @Shadow private int ticks;
    @Unique MinecraftClient client  = MinecraftClient.getInstance();

    @Unique PlayerHUD playerHUD = new PlayerHUD();

    @Inject(method = "render(FJZ)V", at = @At(
            value = "RETURN"
    ))
    private void onRenderGameHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (client.skipGameRender || !tick || MinecraftClient.getInstance().world == null) return;
        if (client.currentScreen != null || client.options.hudHidden) return;
        playerHUD.render(ticks, tickDelta);
    }
}
