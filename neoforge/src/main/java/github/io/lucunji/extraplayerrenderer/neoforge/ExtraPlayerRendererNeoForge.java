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

package github.io.lucunji.extraplayerrenderer.neoforge;

import github.io.lucunji.extraplayerrenderer.CommonInterfaceInstances;
import github.io.lucunji.extraplayerrenderer.ExtraPlayerRenderer;
import github.io.lucunji.extraplayerrenderer.config.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ExtraPlayerRenderer.MOD_ID, dist = Dist.CLIENT)
public final class ExtraPlayerRendererNeoForge {
    public ExtraPlayerRendererNeoForge() {
        CommonInterfaceInstances.keyHelper = KeyMapping::getKey;

        // Run our common setup.
        ExtraPlayerRenderer.init();

        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (modContainer, lastScreen) -> new ConfigScreen(lastScreen, ExtraPlayerRenderer.CONFIGS.getOptions())
        );
    }
}
