package github.io.lucunji.explayerenderer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.mixininterface.ImmediateMixinInterface;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexConsumerProvider.Immediate.class)
public abstract class ImmediateMixin implements ImmediateMixinInterface {

    @Unique
    private boolean forceDisableCulling = false;

    @Override
    public void extraPlayerRenderer$setForceDisableCulling(boolean disableCulling) { this.forceDisableCulling = disableCulling; }


    @Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At("HEAD"))
    void onDrawLayer(RenderLayer layer, CallbackInfo callbackInfo) {
        if (this.forceDisableCulling) RenderSystem.disableCull();
    }

    @Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At("RETURN"))
    void onDrawLayerFinish(RenderLayer layer, CallbackInfo callbackInfo) {
        if (this.forceDisableCulling) RenderSystem.enableCull();
    }
}
