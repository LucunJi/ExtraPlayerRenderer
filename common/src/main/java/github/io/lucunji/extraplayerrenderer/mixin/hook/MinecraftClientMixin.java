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

package github.io.lucunji.extraplayerrenderer.mixin.hook;

import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import github.io.lucunji.extraplayerrenderer.config.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    //@Shadow
    @Nullable
    public Screen extraPlayerRenderer$currentScreen;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "handleKeybinds", at = @At("RETURN"))
    private void onHandleInputEventsFinish(CallbackInfo ci) {
        while (ExtraPlayerRenderer.CONFIG_KEY.consumeClick()) {
            this.setScreen(new ConfigScreen(this.extraPlayerRenderer$currentScreen, ExtraPlayerRenderer.CONFIGS.getOptions()));
        }
    }
}
