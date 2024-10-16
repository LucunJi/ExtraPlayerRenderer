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

package github.io.lucunji.extraplayerrenderer.mixin.retexture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import github.io.lucunji.extraplayerrenderer.config.view.Retextured;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSelectionList.class)
public class EntryListWidgetMixin {
    /**
     * This is an incomplete implementation. The background and separator/header/footer are untouched.
     */
    @WrapOperation(method = "renderWidget", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V")
    })
    public void drawTransparentTextFieldTexture(GuiGraphics instance, ResourceLocation texture, int x, int y, int width, int height, Operation<Void> original) {
        if (this instanceof Retextured retextured)
            original.call(instance, retextured.retexture(texture), x, y, width, height);
        else original.call(instance, texture, x, y, width, height);
    }
}
