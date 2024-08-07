package github.io.lucunji.explayerenderer.mixin.patch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import github.io.lucunji.explayerenderer.mixininterface.ImmediateMixinInterface;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VertexConsumerProvider.Immediate.class)
public abstract class ImmediateMixin implements ImmediateMixinInterface {

    @Unique
    private boolean forceDisableCulling = false;

    @Override
    public void extraPlayerRenderer$setForceDisableCulling(boolean disableCulling) { this.forceDisableCulling = disableCulling; }

    @WrapOperation(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/BufferBuilder;)V"))
    void disableCulling(VertexConsumerProvider.Immediate instance, RenderLayer layer, BufferBuilder builder, Operation<Void> original) {
        if (this.forceDisableCulling) {
            RenderSystem.disableCull();
            original.call(instance, layer, builder);
            RenderSystem.enableCull();
        } else {
            original.call(instance, layer, builder);
        }
    }
}
