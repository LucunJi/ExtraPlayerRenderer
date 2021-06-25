package github.io.lucunji.explayerenderer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.Main;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexConsumerProvider.Immediate.class)
public class ImmediateMixin {
    @Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At("HEAD"))
    void onDrawLayer(RenderLayer layer, CallbackInfo callbackInfo) {
        if (Main.PLAYER_HUD_RENDERER.needFixMirrorItem())
            RenderSystem.disableCull();
    }

    @Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At("RETURN"))
    void onDrawLayerFinish(RenderLayer layer, CallbackInfo callbackInfo) {
        if (Main.PLAYER_HUD_RENDERER.needFixMirrorItem())
            RenderSystem.enableCull();
    }
}
