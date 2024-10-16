/*
 *     Highly configurable paper doll mod.
 *     Copyright (C) 2024  LucunJi, And all  Contributors
 *
 *     This file is part of Extra Player Renderer.
 *
 *     Extra Player Renderer is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Extra Player Renderer is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Extra Player Renderer.  If not, see <https://www.gnu.org/licenses/>.
 */

package github.io.lucunji.extraplayerrenderer.mixin.patch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import github.io.lucunji.extraplayerrenderer.mixininterface.ImmediateMixinInterface;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiBufferSource.BufferSource.class)
public abstract class ImmediateMixin implements ImmediateMixinInterface {

    @Unique
    private boolean extraPlayerRenderer$forceDisableCulling = false;

    @Override
    public void extraPlayerRenderer$setForceDisableCulling(boolean disableCulling) {
        this.extraPlayerRenderer$forceDisableCulling = disableCulling;
    }

    // strangely, WrapMethod has no effect
    @WrapOperation(method = "endBatch(Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/BufferBuilder;)V"))
    void disableCulling(MultiBufferSource.BufferSource instance, RenderType layer, BufferBuilder builder, Operation<Void> original) {
        if (this.extraPlayerRenderer$forceDisableCulling) {
            RenderSystem.disableCull();
            original.call(instance, layer, builder);
            RenderSystem.enableCull();
        } else {
            original.call(instance, layer, builder);
        }
    }
}
